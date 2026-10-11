package gt.chn.tarjetas.compartido.error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ManejadorErrores extends ResponseEntityExceptionHandler {

    private static final Logger registro = LoggerFactory.getLogger(ManejadorErrores.class);

    @ExceptionHandler(ExcepcionNegocio.class)
    public ResponseEntity<ProblemDetail> manejarErrorDeNegocio(ExcepcionNegocio excepcion) {
        CodigoError codigo = excepcion.getCodigo();
        ProblemDetail problema = crearProblema(codigo, excepcion.getMessage());
        return ResponseEntity.status(codigo.getEstado()).body(problema);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> manejarErrorInesperado(Exception excepcion) {

        registro.error("Error inesperado", excepcion);

        CodigoError codigo = CodigoError.ERROR_INTERNO;
        ProblemDetail problema = crearProblema(codigo, "Ocurrió un error inesperado. Intente nuevamente.");
        return ResponseEntity.status(codigo.getEstado()).body(problema);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException excepcion,
            HttpHeaders cabeceras,
            HttpStatusCode estado,
            WebRequest peticion) {

        List<Map<String, String>> errores = new ArrayList<>();
        for (FieldError campoConError : excepcion.getBindingResult().getFieldErrors()) {
            String mensaje = campoConError.getDefaultMessage();
            if (mensaje == null) {
                mensaje = "valor inválido";
            }
            Map<String, String> error = new HashMap<>();
            error.put("campo", campoConError.getField());
            error.put("mensaje", mensaje);
            errores.add(error);
        }

        ProblemDetail problema = crearProblema(CodigoError.ERROR_VALIDACION, "Uno o más campos no son válidos.");
        problema.setProperty("errores", errores);
        return handleExceptionInternal(excepcion, problema, cabeceras, HttpStatus.BAD_REQUEST, peticion);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception excepcion,
            Object cuerpo,
            HttpHeaders cabeceras,
            HttpStatusCode estado,
            WebRequest peticion) {

        ResponseEntity<Object> respuesta = super.handleExceptionInternal(excepcion, cuerpo, cabeceras, estado, peticion);

        if (respuesta != null && respuesta.getBody() instanceof ProblemDetail) {
            ProblemDetail problema = (ProblemDetail) respuesta.getBody();
            Map<String, Object> propiedades = problema.getProperties();
            boolean yaTieneCodigo = propiedades != null && propiedades.containsKey("codigo");
            if (!yaTieneCodigo) {
                problema.setProperty("codigo", codigoSegunEstado(estado));
            }
        }
        return respuesta;
    }


    private ProblemDetail crearProblema(CodigoError codigo, String mensaje) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(codigo.getEstado(), mensaje);
        problema.setTitle(codigo.getTitulo());
        problema.setProperty("codigo", codigo.name());
        return problema;
    }


    private String codigoSegunEstado(HttpStatusCode estado) {
        int numero = estado.value();
        if (numero == 400) {
            return CodigoError.ERROR_VALIDACION.name();
        }
        if (numero == 404) {
            return "RECURSO_NO_ENCONTRADO";
        }
        return "ERROR_PETICION";
    }
}
package gt.chn.tarjetas.compartido.error;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Punto único de traducción de errores a respuestas HTTP (RFC 9457, Problem Details).
 * Toda respuesta de error incluye el campo "codigo".
 */
@RestControllerAdvice
public class ManejadorErrores extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ManejadorErrores.class);

    @ExceptionHandler(ExcepcionNegocio.class)
    public ResponseEntity<ProblemDetail> manejarNegocio(ExcepcionNegocio ex) {
        CodigoError codigo = ex.getCodigo();
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(codigo.getEstado(), ex.getMessage());
        problema.setTitle(codigo.getTitulo());
        problema.setProperty("codigo", codigo.name());
        return ResponseEntity.status(codigo.getEstado()).body(problema);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> manejarInesperado(Exception ex) {
        // El detalle técnico se registra en el servidor y nunca se envía al cliente.
        log.error("Error inesperado", ex);
        CodigoError codigo = CodigoError.ERROR_INTERNO;
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                codigo.getEstado(), "Ocurrió un error inesperado. Intente nuevamente.");
        problema.setTitle(codigo.getTitulo());
        problema.setProperty("codigo", codigo.name());
        return ResponseEntity.status(codigo.getEstado()).body(problema);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<Map<String, String>> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of(
                        "campo", e.getField(),
                        "mensaje", e.getDefaultMessage() == null ? "valor inválido" : e.getDefaultMessage()))
                .toList();
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Uno o más campos no son válidos.");
        problema.setTitle(CodigoError.ERROR_VALIDACION.getTitulo());
        problema.setProperty("codigo", CodigoError.ERROR_VALIDACION.name());
        problema.setProperty("errores", errores);
        return handleExceptionInternal(ex, problema, headers, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Los errores estándar de Spring (ruta inexistente, JSON mal formado, método no permitido)
     * pasan por aquí: se les agrega el campo "codigo" para mantener un formato uniforme.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        ResponseEntity<Object> respuesta = super.handleExceptionInternal(ex, body, headers, statusCode, request);
        if (respuesta != null && respuesta.getBody() instanceof ProblemDetail problema) {
            Map<String, Object> propiedades = problema.getProperties();
            if (propiedades == null || !propiedades.containsKey("codigo")) {
                problema.setProperty("codigo", codigoPorEstado(statusCode));
            }
        }
        return respuesta;
    }

    private static String codigoPorEstado(HttpStatusCode estado) {
        return switch (estado.value()) {
            case 400 -> CodigoError.ERROR_VALIDACION.name();
            case 404 -> "RECURSO_NO_ENCONTRADO";
            default -> "ERROR_PETICION";
        };
    }
}
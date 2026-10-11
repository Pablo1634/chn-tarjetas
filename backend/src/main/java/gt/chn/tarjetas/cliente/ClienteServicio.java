package gt.chn.tarjetas.cliente;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gt.chn.tarjetas.compartido.error.CodigoError;
import gt.chn.tarjetas.compartido.error.ExcepcionNegocio;

@Service
public class ClienteServicio {

    private static final String RESTRICCION_IDENTIFICACION = "uq_cliente_identificacion";

    private final ClienteRepositorio repositorio;
    private final Clock reloj;

    public ClienteServicio(ClienteRepositorio repositorio, Clock reloj) {
        this.repositorio = repositorio;
        this.reloj = reloj;
    }

    @Transactional
    public ClienteRespuesta crear(ClienteSolicitud solicitud) {
        String identificacion = solicitud.numeroIdentificacion().trim();

        // Primera defensa: mensaje claro. Cuenta también a los clientes eliminados.
        if (repositorio.contarPorIdentificacionIncluidosEliminados(identificacion) > 0) {
            throw identificacionDuplicada(identificacion);
        }

        Cliente cliente = new Cliente(
                solicitud.nombre(), solicitud.apellido(), identificacion,
                solicitud.fechaNacimiento(), solicitud.direccion(),
                solicitud.correoElectronico(), solicitud.telefono(), ahora());

        try {
            return ClienteRespuesta.desde(repositorio.saveAndFlush(cliente));
        } catch (DataIntegrityViolationException ex) {
            // Segunda defensa: dos peticiones simultáneas pasaron la comprobación de arriba
            // y la base de datos (UNIQUE) rechazó a la segunda.
            String causa = NestedExceptionUtils.getMostSpecificCause(ex).getMessage();
            if (causa != null && causa.contains(RESTRICCION_IDENTIFICACION)) {
                throw identificacionDuplicada(identificacion);
            }
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public List<ClienteRespuesta> listar() {
        return repositorio.findAllByOrderByApellidoAscNombreAsc().stream()
                .map(ClienteRespuesta::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteRespuesta obtener(Long id) {
        return ClienteRespuesta.desde(buscar(id));
    }

    @Transactional
    public ClienteRespuesta editar(Long id, ClienteEdicion edicion) {
        Cliente cliente = buscar(id);
        cliente.actualizarDatos(
                edicion.nombre(), edicion.apellido(), edicion.fechaNacimiento(),
                edicion.direccion(), edicion.correoElectronico(), edicion.telefono(), ahora());
        return ClienteRespuesta.desde(cliente);
    }

    private Cliente buscar(Long id) {
        return repositorio.findById(id).orElseThrow(() -> new ExcepcionNegocio(
                CodigoError.CLIENTE_NO_ENCONTRADO, "No existe un cliente con id " + id + "."));
    }

    private LocalDateTime ahora() {
        return LocalDateTime.now(reloj);
    }

    private static ExcepcionNegocio identificacionDuplicada(String identificacion) {
        return new ExcepcionNegocio(CodigoError.IDENTIFICACION_YA_EXISTE,
                "La identificación " + identificacion + " ya está registrada. "
                        + "Las identificaciones de clientes eliminados también quedan reservadas.");
    }
}
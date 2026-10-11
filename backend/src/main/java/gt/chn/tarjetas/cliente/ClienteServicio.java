package gt.chn.tarjetas.cliente;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gt.chn.tarjetas.compartido.error.CodigoError;
import gt.chn.tarjetas.compartido.error.ExcepcionNegocio;

@Service
public class ClienteServicio {

    private final ClienteRepositorio repositorio;
    private final Clock reloj;

    public ClienteServicio(ClienteRepositorio repositorio, Clock reloj) {
        this.repositorio = repositorio;
        this.reloj = reloj;
    }

    @Transactional
    public ClienteRespuesta crear(ClienteSolicitud solicitud) {

        String identificacion = solicitud.numeroIdentificacion().trim();

        long cantidad = repositorio.contarPorIdentificacionIncluidosEliminados(identificacion);
        if (cantidad > 0) {
            throw identificacionDuplicada(identificacion);
        }

        Cliente cliente = new Cliente(
                solicitud.nombre(),
                solicitud.apellido(),
                identificacion,
                solicitud.fechaNacimiento(),
                solicitud.direccion(),
                solicitud.correoElectronico(),
                solicitud.telefono(),
                ahora());

        try {
            Cliente guardado = repositorio.saveAndFlush(cliente);
            return ClienteRespuesta.desde(guardado);
        } catch (DataIntegrityViolationException error) {
            String detalle = error.getMostSpecificCause().getMessage();
            if (detalle != null && detalle.contains("uq_cliente_identificacion")) {
                throw identificacionDuplicada(identificacion);
            }
            throw error;
        }
    }

    @Transactional(readOnly = true)
    public List<ClienteRespuesta> listar() {
        List<Cliente> clientes = repositorio.findAllByOrderByApellidoAscNombreAsc();
        List<ClienteRespuesta> respuestas = new ArrayList<>();
        for (Cliente cliente : clientes) {
            respuestas.add(ClienteRespuesta.desde(cliente));
        }
        return respuestas;
    }

    @Transactional(readOnly = true)
    public ClienteRespuesta obtener(Long id) {
        Cliente cliente = buscar(id);
        return ClienteRespuesta.desde(cliente);
    }

    @Transactional
    public ClienteRespuesta editar(Long id, ClienteEdicion edicion) {
        Cliente cliente = buscar(id);
        cliente.actualizarDatos(
                edicion.nombre(),
                edicion.apellido(),
                edicion.fechaNacimiento(),
                edicion.direccion(),
                edicion.correoElectronico(),
                edicion.telefono(),
                ahora());

        return ClienteRespuesta.desde(cliente);
    }

    private Cliente buscar(Long id) {
        Optional<Cliente> encontrado = repositorio.findById(id);
        if (encontrado.isEmpty()) {
            throw new ExcepcionNegocio(CodigoError.CLIENTE_NO_ENCONTRADO,
                    "No existe un cliente con id " + id + ".");
        }
        return encontrado.get();
    }

    private LocalDateTime ahora() {
        return LocalDateTime.now(reloj);
    }

    private ExcepcionNegocio identificacionDuplicada(String identificacion) {
        return new ExcepcionNegocio(CodigoError.IDENTIFICACION_YA_EXISTE,
                "La identificación " + identificacion + " ya está registrada. "
                        + "Las identificaciones de clientes eliminados también quedan reservadas.");
    }
}
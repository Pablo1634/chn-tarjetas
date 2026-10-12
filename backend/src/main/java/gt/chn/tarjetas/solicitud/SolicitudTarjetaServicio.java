package gt.chn.tarjetas.solicitud;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gt.chn.tarjetas.cliente.Cliente;
import gt.chn.tarjetas.cliente.ClienteRepositorio;
import gt.chn.tarjetas.compartido.error.CodigoError;
import gt.chn.tarjetas.compartido.error.ExcepcionNegocio;
import gt.chn.tarjetas.tarjeta.GeneradorNumeroTarjeta;
import gt.chn.tarjetas.tarjeta.Tarjeta;
import gt.chn.tarjetas.tarjeta.TarjetaRepositorio;

@Service
public class SolicitudTarjetaServicio {

    private final SolicitudTarjetaRepositorio repositorio;
    private final ClienteRepositorio clienteRepositorio;
    private final TarjetaRepositorio tarjetaRepositorio;
    private final GeneradorNumeroTarjeta generadorNumeroTarjeta;
    private final Clock reloj;

    public SolicitudTarjetaServicio(
            SolicitudTarjetaRepositorio repositorio,
            ClienteRepositorio clienteRepositorio,
            TarjetaRepositorio tarjetaRepositorio,
            GeneradorNumeroTarjeta generadorNumeroTarjeta,
            Clock reloj) {
        this.repositorio = repositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.tarjetaRepositorio = tarjetaRepositorio;
        this.generadorNumeroTarjeta = generadorNumeroTarjeta;
        this.reloj = reloj;
    }

    @Transactional
    public SolicitudTarjetaRespuesta crear(SolicitudTarjetaSolicitud solicitud) {
        Cliente cliente = buscarCliente(solicitud.clienteId());

        SolicitudTarjeta nuevaSolicitud = new SolicitudTarjeta(
                cliente,
                solicitud.tipoTarjeta(),
                solicitud.ingresoMensualDeclarado(),
                ahora());

        SolicitudTarjeta guardada = repositorio.save(nuevaSolicitud);
        return SolicitudTarjetaRespuesta.desde(guardada);
    }

    @Transactional(readOnly = true)
    public List<SolicitudTarjetaRespuesta> listar() {
        List<SolicitudTarjeta> solicitudes = repositorio.findAll();
        List<SolicitudTarjetaRespuesta> respuestas = new ArrayList<>();

        for (SolicitudTarjeta solicitud : solicitudes) {
            respuestas.add(SolicitudTarjetaRespuesta.desde(solicitud));
        }

        return respuestas;
    }

    @Transactional(readOnly = true)
    public SolicitudTarjetaRespuesta obtener(Long id) {
        SolicitudTarjeta solicitud = buscar(id);
        return SolicitudTarjetaRespuesta.desde(solicitud);
    }

    @Transactional
    public SolicitudTarjetaRespuesta aprobar(
            Long id,
            SolicitudTarjetaAprobacion aprobacion) {

        SolicitudTarjeta solicitud = buscar(id);
        verificarPendiente(solicitud);

        BigDecimal montoAprobado = aprobacion.montoAprobado();
        LocalDateTime fechaDecision = ahora();

        String numeroTarjeta = generarNumeroDisponible();

        Tarjeta tarjeta = new Tarjeta(
                numeroTarjeta,
                solicitud.getCliente(),
                solicitud,
                solicitud.getTipoTarjeta(),
                montoAprobado,
                fechaDecision);

        // La solicitud y la tarjeta se guardan dentro de la misma transacción.
        solicitud.aprobar(montoAprobado, fechaDecision);
        tarjetaRepositorio.save(tarjeta);

        return SolicitudTarjetaRespuesta.desde(solicitud);
    }

    @Transactional
    public SolicitudTarjetaRespuesta rechazar(
            Long id,
            SolicitudTarjetaRechazo rechazo) {

        SolicitudTarjeta solicitud = buscar(id);
        verificarPendiente(solicitud);

        solicitud.rechazar(
                rechazo.comentarioDecision().trim(),
                ahora());

        return SolicitudTarjetaRespuesta.desde(solicitud);
    }

    private void verificarPendiente(SolicitudTarjeta solicitud) {
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new ExcepcionNegocio(
                    CodigoError.SOLICITUD_NO_PENDIENTE,
                    "La solicitud ya fue decidida y no puede modificarse.");
        }
    }

    private String generarNumeroDisponible() {
        // Se hacen varios intentos para evitar reutilizar un número existente.
        for (int intento = 0; intento < 10; intento++) {
            String numero = generadorNumeroTarjeta.generar();

            if (!tarjetaRepositorio.existsByNumeroTarjeta(numero)) {
                return numero;
            }
        }

        throw new ExcepcionNegocio(
                CodigoError.ERROR_INTERNO,
                "No fue posible generar un número de tarjeta disponible.");
    }

    private SolicitudTarjeta buscar(Long id) {
        Optional<SolicitudTarjeta> encontrada = repositorio.findById(id);

        if (encontrada.isEmpty()) {
            throw new ExcepcionNegocio(
                    CodigoError.SOLICITUD_NO_ENCONTRADA,
                    "No existe una solicitud con id " + id + ".");
        }

        return encontrada.get();
    }

    private Cliente buscarCliente(Long id) {
        Optional<Cliente> encontrado = clienteRepositorio.findById(id);

        if (encontrado.isEmpty()) {
            throw new ExcepcionNegocio(
                    CodigoError.CLIENTE_NO_ENCONTRADO,
                    "No existe un cliente activo con id " + id + ".");
        }

        return encontrado.get();
    }

    private LocalDateTime ahora() {
        return LocalDateTime.now(reloj);
    }
}
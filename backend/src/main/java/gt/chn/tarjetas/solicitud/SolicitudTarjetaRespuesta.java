package gt.chn.tarjetas.solicitud;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SolicitudTarjetaRespuesta(
        Long id,
        Long clienteId,
        TipoTarjeta tipoTarjeta,
        BigDecimal ingresoMensualDeclarado,
        LocalDateTime fechaSolicitud,
        EstadoSolicitud estado,
        LocalDateTime fechaDecision,
        String comentarioDecision,
        BigDecimal montoAprobado) {

    public static SolicitudTarjetaRespuesta desde(SolicitudTarjeta solicitud) {
        return new SolicitudTarjetaRespuesta(
                solicitud.getId(),
                solicitud.getCliente().getId(),
                solicitud.getTipoTarjeta(),
                solicitud.getIngresoMensualDeclarado(),
                solicitud.getFechaSolicitud(),
                solicitud.getEstado(),
                solicitud.getFechaDecision(),
                solicitud.getComentarioDecision(),
                solicitud.getMontoAprobado());
    }
}
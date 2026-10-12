package gt.chn.tarjetas.tarjeta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MovimientoTarjetaRespuesta(
        Long id,
        Long tarjetaId,
        TipoMovimiento tipoMovimiento,
        BigDecimal monto,
        LocalDate fechaMovimiento,
        String descripcion,
        LocalDateTime creadoEn
) {

    public static MovimientoTarjetaRespuesta desde(
            MovimientoTarjeta movimiento) {

        return new MovimientoTarjetaRespuesta(
                movimiento.getId(),
                movimiento.getTarjeta().getId(),
                movimiento.getTipoMovimiento(),
                movimiento.getMonto(),
                movimiento.getFechaMovimiento(),
                movimiento.getDescripcion(),
                movimiento.getCreadoEn());
    }
}
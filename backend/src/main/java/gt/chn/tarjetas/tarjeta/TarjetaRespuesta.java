package gt.chn.tarjetas.tarjeta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import gt.chn.tarjetas.solicitud.TipoTarjeta;

public record TarjetaRespuesta(
        Long id,
        String numeroTarjeta,
        Long clienteId,
        Long solicitudId,
        TipoTarjeta tipoTarjeta,
        BigDecimal limiteCredito,
        BigDecimal saldoActual,
        LocalDateTime fechaEmision
) {

    public static TarjetaRespuesta desde(Tarjeta tarjeta) {
        return new TarjetaRespuesta(
                tarjeta.getId(),
                tarjeta.getNumeroTarjeta(),
                tarjeta.getCliente().getId(),
                tarjeta.getSolicitud().getId(),
                tarjeta.getTipoTarjeta(),
                tarjeta.getLimiteCredito(),
                tarjeta.getSaldoActual(),
                tarjeta.getFechaEmision());
    }
}

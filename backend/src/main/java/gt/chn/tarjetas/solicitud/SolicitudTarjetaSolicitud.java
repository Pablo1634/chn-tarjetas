package gt.chn.tarjetas.solicitud;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SolicitudTarjetaSolicitud(
        @NotNull
        @Positive
        Long clienteId,

        @NotNull
        TipoTarjeta tipoTarjeta,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal ingresoMensualDeclarado) {
}
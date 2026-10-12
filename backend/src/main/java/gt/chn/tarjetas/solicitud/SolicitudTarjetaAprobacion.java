package gt.chn.tarjetas.solicitud;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record SolicitudTarjetaAprobacion(

        @NotNull(message = "El monto aprobado es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto aprobado debe ser mayor que cero")
        BigDecimal montoAprobado

) {
}
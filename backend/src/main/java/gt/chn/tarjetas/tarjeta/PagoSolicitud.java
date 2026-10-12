package gt.chn.tarjetas.tarjeta;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PagoSolicitud(

        @NotNull(message = "El monto del pago es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero")
        BigDecimal monto,

        @Size(max = 200,
                message = "La descripción no puede superar los 200 caracteres")
        String descripcion

) {
}
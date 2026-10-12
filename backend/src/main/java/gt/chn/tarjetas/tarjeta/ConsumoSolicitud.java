package gt.chn.tarjetas.tarjeta;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ConsumoSolicitud(

        @NotNull(message = "El monto del consumo es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero")
        BigDecimal monto,

        @NotNull(message = "La descripción es obligatoria")
        @Size(min = 1, max = 200,
                message = "La descripción debe tener entre 1 y 200 caracteres")
        String descripcion

) {
}

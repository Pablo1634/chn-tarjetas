package gt.chn.tarjetas.solicitud;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SolicitudTarjetaRechazo(

        @NotBlank(message = "El comentario de rechazo es obligatorio")
        @Size(max = 500, message = "El comentario no puede superar los 500 caracteres")
        String comentarioDecision

) {
}
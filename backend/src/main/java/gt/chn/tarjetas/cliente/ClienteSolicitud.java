package gt.chn.tarjetas.cliente;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record ClienteSolicitud(

        @NotBlank(message = "El nombre es obligatorio.")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres.")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio.")
        @Size(max = 100, message = "El apellido no puede superar 100 caracteres.")
        String apellido,

        @NotBlank(message = "El número de identificación es obligatorio.")
        @Size(max = 30, message = "El número de identificación no puede superar 30 caracteres.")
        String numeroIdentificacion,

        @NotNull(message = "La fecha de nacimiento es obligatoria.")
        @Past(message = "La fecha de nacimiento debe ser anterior a hoy.")
        LocalDate fechaNacimiento,

        @NotBlank(message = "La dirección es obligatoria.")
        @Size(max = 250, message = "La dirección no puede superar 250 caracteres.")
        String direccion,

        @NotBlank(message = "El correo electrónico es obligatorio.")
        @Email(message = "El correo electrónico no tiene un formato válido.")
        @Size(max = 150, message = "El correo electrónico no puede superar 150 caracteres.")
        String correoElectronico,

        @NotBlank(message = "El teléfono es obligatorio.")
        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres.")
        String telefono) {
}
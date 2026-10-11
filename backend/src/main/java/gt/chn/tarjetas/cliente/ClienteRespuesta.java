package gt.chn.tarjetas.cliente;

import java.time.LocalDate;

/** Lo que la API devuelve de un cliente. */
public record ClienteRespuesta(
        Long id,
        String nombre,
        String apellido,
        String numeroIdentificacion,
        LocalDate fechaNacimiento,
        String direccion,
        String correoElectronico,
        String telefono) {

    public static ClienteRespuesta desde(Cliente cliente) {
        return new ClienteRespuesta(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getNumeroIdentificacion(),
                cliente.getFechaNacimiento(),
                cliente.getDireccion(),
                cliente.getCorreoElectronico(),
                cliente.getTelefono());
    }
}
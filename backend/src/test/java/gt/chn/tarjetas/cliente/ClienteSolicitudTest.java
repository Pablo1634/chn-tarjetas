package gt.chn.tarjetas.cliente;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class ClienteSolicitudTest {

    private static Validator validador;

    @BeforeAll
    static void iniciar() {
        validador = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private Set<String> camposInvalidos(ClienteSolicitud solicitud) {
        Set<ConstraintViolation<ClienteSolicitud>> violaciones = validador.validate(solicitud);
        Set<String> campos = new HashSet<>();
        for (ConstraintViolation<ClienteSolicitud> violacion : violaciones) {
            campos.add(violacion.getPropertyPath().toString());
        }
        return campos;
    }

    private ClienteSolicitud solicitudValida() {
        return new ClienteSolicitud("Ana", "Pérez", "1234567-8", LocalDate.of(1990, 5, 20),
                "Zona 1, Ciudad de Guatemala", "ana@correo.com", "55551234");
    }

    @Test
    void unaSolicitudCompletaEsValida() {
        Set<String> campos = camposInvalidos(solicitudValida());

        assertTrue(campos.isEmpty());
    }

    @Test
    void sinNingunCampoSeRechazanLosSiete() {
        ClienteSolicitud vacia = new ClienteSolicitud(null, null, null, null, null, null, null);

        Set<String> campos = camposInvalidos(vacia);

        assertEquals(7, campos.size());
        assertTrue(campos.contains("nombre"));
        assertTrue(campos.contains("apellido"));
        assertTrue(campos.contains("numeroIdentificacion"));
        assertTrue(campos.contains("fechaNacimiento"));
        assertTrue(campos.contains("direccion"));
        assertTrue(campos.contains("correoElectronico"));
        assertTrue(campos.contains("telefono"));
    }

    @Test
    void unNombreSoloConEspaciosEsRechazado() {
        ClienteSolicitud solicitud = new ClienteSolicitud("   ", "Pérez", "1234567-8",
                LocalDate.of(1990, 5, 20), "Zona 1", "ana@correo.com", "55551234");

        Set<String> campos = camposInvalidos(solicitud);

        assertEquals(1, campos.size());
        assertTrue(campos.contains("nombre"));
    }

    @Test
    void unCorreoSinFormatoEsRechazado() {
        ClienteSolicitud solicitud = new ClienteSolicitud("Ana", "Pérez", "1234567-8",
                LocalDate.of(1990, 5, 20), "Zona 1", "esto-no-es-un-correo", "55551234");

        Set<String> campos = camposInvalidos(solicitud);

        assertEquals(1, campos.size());
        assertTrue(campos.contains("correoElectronico"));
    }

    @Test
    void unaFechaDeNacimientoDeHoyOFuturaEsRechazada() {
        ClienteSolicitud conFechaDeHoy = new ClienteSolicitud("Ana", "Pérez", "1234567-8",
                LocalDate.now(), "Zona 1", "ana@correo.com", "55551234");
        ClienteSolicitud conFechaDeManana = new ClienteSolicitud("Ana", "Pérez", "1234567-8",
                LocalDate.now().plusDays(1), "Zona 1", "ana@correo.com", "55551234");

        assertTrue(camposInvalidos(conFechaDeHoy).contains("fechaNacimiento"));
        assertTrue(camposInvalidos(conFechaDeManana).contains("fechaNacimiento"));
    }

    @Test
    void unTextoQueSuperaLaLongitudDeLaColumnaEsRechazado() {
        String nombreDe101Letras = "A".repeat(101);
        ClienteSolicitud solicitud = new ClienteSolicitud(nombreDe101Letras, "Pérez", "1234567-8",
                LocalDate.of(1990, 5, 20), "Zona 1", "ana@correo.com", "55551234");

        Set<String> campos = camposInvalidos(solicitud);

        assertEquals(1, campos.size());
        assertTrue(campos.contains("nombre"));
    }
}
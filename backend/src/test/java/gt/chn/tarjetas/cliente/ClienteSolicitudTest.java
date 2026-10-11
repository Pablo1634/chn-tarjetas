package gt.chn.tarjetas.cliente;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/** Pruebas de las validaciones de entrada. Cubre AC-C02 y AC-C08 sin base de datos. */
class ClienteSolicitudTest {

    private static Validator validador;

    @BeforeAll
    static void iniciar() {
        validador = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private static ClienteSolicitud valida() {
        return new ClienteSolicitud("Ana", "Pérez", "1234567-8", LocalDate.of(1990, 5, 20),
                "Zona 1, Ciudad de Guatemala", "ana@correo.com", "55551234");
    }

    private static Set<String> camposInvalidos(ClienteSolicitud solicitud) {
        Set<ConstraintViolation<ClienteSolicitud>> violaciones = validador.validate(solicitud);
        return violaciones.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    void unaSolicitudCompletaEsValida() {
        assertThat(camposInvalidos(valida())).isEmpty();
    }

    @Test
    void sinNingunCampoSeRechazanLosSiete() {
        var vacia = new ClienteSolicitud(null, null, null, null, null, null, null);
        assertThat(camposInvalidos(vacia)).containsExactlyInAnyOrder(
                "nombre", "apellido", "numeroIdentificacion", "fechaNacimiento",
                "direccion", "correoElectronico", "telefono");
    }

    @Test
    void unNombreSoloConEspaciosEsRechazado() {
        var s = new ClienteSolicitud("   ", "Pérez", "1234567-8", LocalDate.of(1990, 5, 20),
                "Zona 1", "ana@correo.com", "55551234");
        assertThat(camposInvalidos(s)).containsExactly("nombre");
    }

    @Test
    void unCorreoSinFormatoEsRechazado() {
        var s = new ClienteSolicitud("Ana", "Pérez", "1234567-8", LocalDate.of(1990, 5, 20),
                "Zona 1", "esto-no-es-un-correo", "55551234");
        assertThat(camposInvalidos(s)).containsExactly("correoElectronico");
    }

    @Test
    void unaFechaDeNacimientoDeHoyOFuturaEsRechazada() {
        var hoy = new ClienteSolicitud("Ana", "Pérez", "1234567-8", LocalDate.now(),
                "Zona 1", "ana@correo.com", "55551234");
        var manana = new ClienteSolicitud("Ana", "Pérez", "1234567-8", LocalDate.now().plusDays(1),
                "Zona 1", "ana@correo.com", "55551234");
        assertThat(camposInvalidos(hoy)).containsExactly("fechaNacimiento");
        assertThat(camposInvalidos(manana)).containsExactly("fechaNacimiento");
    }

    @Test
    void unTextoQueSuperaLaLongitudDeLaColumnaEsRechazado() {
        var s = new ClienteSolicitud("A".repeat(101), "Pérez", "1234567-8", LocalDate.of(1990, 5, 20),
                "Zona 1", "ana@correo.com", "55551234");
        assertThat(camposInvalidos(s)).containsExactly("nombre");
    }
}
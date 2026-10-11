package gt.chn.tarjetas.cliente;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gt.chn.tarjetas.compartido.error.CodigoError;
import gt.chn.tarjetas.compartido.error.ExcepcionNegocio;

class ClienteServicioTest {

    private ClienteRepositorio repositorio;
    private ClienteServicio servicio;

    @BeforeEach
    void preparar() {
        repositorio = mock(ClienteRepositorio.class);
        Clock relojFijo = Clock.fixed(Instant.parse("2026-10-11T12:00:00Z"), ZoneOffset.UTC);
        servicio = new ClienteServicio(repositorio, relojFijo);
        when(repositorio.saveAndFlush(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private static ClienteSolicitud solicitud(String identificacion) {
        return new ClienteSolicitud("Ana", "Pérez", identificacion, LocalDate.of(1990, 5, 20),
                "Zona 1", "ana@correo.com", "55551234");
    }

    private static void esperarCodigo(Throwable error, CodigoError esperado) {
        assertThat(error).isInstanceOf(ExcepcionNegocio.class);
        assertThat(((ExcepcionNegocio) error).getCodigo()).isEqualTo(esperado);
    }

    @Test
    void crearGuardaElClienteYDevuelveSusDatos() {
        when(repositorio.contarPorIdentificacionIncluidosEliminados("1234567-8")).thenReturn(0L);

        ClienteRespuesta respuesta = servicio.crear(solicitud("1234567-8"));

        assertThat(respuesta.nombre()).isEqualTo("Ana");
        assertThat(respuesta.numeroIdentificacion()).isEqualTo("1234567-8");
    }

    @Test
    void crearQuitaLosEspaciosDeLaIdentificacion() {
        when(repositorio.contarPorIdentificacionIncluidosEliminados("1234567-8")).thenReturn(0L);

        ClienteRespuesta respuesta = servicio.crear(solicitud("  1234567-8  "));

        assertThat(respuesta.numeroIdentificacion()).isEqualTo("1234567-8");
    }

    @Test
    void crearConIdentificacionRepetidaFallaYNoGuarda() {
        when(repositorio.contarPorIdentificacionIncluidosEliminados("1234567-8")).thenReturn(1L);

        assertThatThrownBy(() -> servicio.crear(solicitud("1234567-8")))
                .satisfies(error -> esperarCodigo(error, CodigoError.IDENTIFICACION_YA_EXISTE));
        verify(repositorio, never()).saveAndFlush(any());
    }

    @Test
    void obtenerUnClienteInexistenteFalla() {
        when(repositorio.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.obtener(99L))
                .satisfies(error -> esperarCodigo(error, CodigoError.CLIENTE_NO_ENCONTRADO));
    }

    @Test
    void editarCambiaLosDatosPeroNoLaIdentificacion() {
        Cliente existente = new Cliente("Ana", "Pérez", "1234567-8", LocalDate.of(1990, 5, 20),
                "Zona 1", "ana@correo.com", "55551234", LocalDateTime.of(2026, 1, 1, 0, 0));
        when(repositorio.findById(1L)).thenReturn(Optional.of(existente));

        var edicion = new ClienteEdicion("Ana María", "López", LocalDate.of(1991, 6, 21),
                "Zona 10", "nuevo@correo.com", "44440000");
        ClienteRespuesta respuesta = servicio.editar(1L, edicion);

        assertThat(respuesta.nombre()).isEqualTo("Ana María");
        assertThat(respuesta.correoElectronico()).isEqualTo("nuevo@correo.com");
        assertThat(respuesta.numeroIdentificacion()).isEqualTo("1234567-8");
    }

    @Test
    void editarUnClienteInexistenteFalla() {
        when(repositorio.findById(99L)).thenReturn(Optional.empty());
        var edicion = new ClienteEdicion("A", "B", LocalDate.of(1990, 1, 1), "d", "a@b.com", "1");

        assertThatThrownBy(() -> servicio.editar(99L, edicion))
                .satisfies(error -> esperarCodigo(error, CodigoError.CLIENTE_NO_ENCONTRADO));
    }
}
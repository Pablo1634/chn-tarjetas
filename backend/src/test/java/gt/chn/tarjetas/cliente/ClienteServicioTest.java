package gt.chn.tarjetas.cliente;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.mockito.ArgumentCaptor;

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
    }

    private ClienteSolicitud solicitud(String identificacion) {
        return new ClienteSolicitud("Ana", "Pérez", identificacion, LocalDate.of(1990, 5, 20),
                "Zona 1", "ana@correo.com", "55551234");
    }

    private Cliente clienteExistente() {
        return new Cliente("Ana", "Pérez", "1234567-8", LocalDate.of(1990, 5, 20),
                "Zona 1", "ana@correo.com", "55551234", LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    @Test
    void crearGuardaElClienteYDevuelveSusDatos() {
        when(repositorio.contarPorIdentificacionIncluidosEliminados("1234567-8")).thenReturn(0L);
        when(repositorio.saveAndFlush(any(Cliente.class))).thenReturn(clienteExistente());

        ClienteRespuesta respuesta = servicio.crear(solicitud("1234567-8"));

        assertEquals("Ana", respuesta.nombre());
        assertEquals("1234567-8", respuesta.numeroIdentificacion());
    }

    @Test
    void crearQuitaLosEspaciosDeLaIdentificacion() {
        when(repositorio.contarPorIdentificacionIncluidosEliminados("1234567-8")).thenReturn(0L);
        when(repositorio.saveAndFlush(any(Cliente.class))).thenReturn(clienteExistente());

        servicio.crear(solicitud("  1234567-8  "));

        ArgumentCaptor<Cliente> capturador = ArgumentCaptor.forClass(Cliente.class);
        verify(repositorio).saveAndFlush(capturador.capture());
        assertEquals("1234567-8", capturador.getValue().getNumeroIdentificacion());
    }

    @Test
    void crearConIdentificacionRepetidaFallaYNoGuarda() {
        when(repositorio.contarPorIdentificacionIncluidosEliminados("1234567-8")).thenReturn(1L);

        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> servicio.crear(solicitud("1234567-8")));

        assertEquals(CodigoError.IDENTIFICACION_YA_EXISTE, error.getCodigo());
        verify(repositorio, never()).saveAndFlush(any());
    }

    @Test
    void obtenerUnClienteInexistenteFalla() {
        when(repositorio.findById(99L)).thenReturn(Optional.empty());

        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> servicio.obtener(99L));

        assertEquals(CodigoError.CLIENTE_NO_ENCONTRADO, error.getCodigo());
    }

    @Test
    void editarCambiaLosDatosPeroNoLaIdentificacion() {
        when(repositorio.findById(1L)).thenReturn(Optional.of(clienteExistente()));
        ClienteEdicion edicion = new ClienteEdicion("Ana María", "López", LocalDate.of(1991, 6, 21),
                "Zona 10", "nuevo@correo.com", "44440000");

        ClienteRespuesta respuesta = servicio.editar(1L, edicion);

        assertEquals("Ana María", respuesta.nombre());
        assertEquals("nuevo@correo.com", respuesta.correoElectronico());
        assertEquals("1234567-8", respuesta.numeroIdentificacion());
    }

    @Test
    void editarUnClienteInexistenteFalla() {
        when(repositorio.findById(99L)).thenReturn(Optional.empty());
        ClienteEdicion edicion = new ClienteEdicion("A", "B", LocalDate.of(1990, 1, 1),
                "dirección", "a@b.com", "1");

        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> servicio.editar(99L, edicion));

        assertEquals(CodigoError.CLIENTE_NO_ENCONTRADO, error.getCodigo());
    }
}
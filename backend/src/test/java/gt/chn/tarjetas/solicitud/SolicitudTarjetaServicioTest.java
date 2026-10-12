package gt.chn.tarjetas.solicitud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import gt.chn.tarjetas.cliente.Cliente;
import gt.chn.tarjetas.cliente.ClienteRepositorio;
import gt.chn.tarjetas.compartido.error.CodigoError;
import gt.chn.tarjetas.compartido.error.ExcepcionNegocio;
import gt.chn.tarjetas.tarjeta.GeneradorNumeroTarjeta;
import gt.chn.tarjetas.tarjeta.TarjetaRepositorio;

class SolicitudTarjetaServicioTest {

    private SolicitudTarjetaRepositorio repositorio;
    private ClienteRepositorio clienteRepositorio;
    private TarjetaRepositorio tarjetaRepositorio;
    private GeneradorNumeroTarjeta generadorNumeroTarjeta;
    private SolicitudTarjetaServicio servicio;

    @BeforeEach
    void preparar() {
        repositorio = mock(SolicitudTarjetaRepositorio.class);
        clienteRepositorio = mock(ClienteRepositorio.class);
        tarjetaRepositorio = mock(TarjetaRepositorio.class);
        generadorNumeroTarjeta = mock(GeneradorNumeroTarjeta.class);

        Clock relojFijo = Clock.fixed(
                Instant.parse("2026-10-11T12:00:00Z"),
                ZoneOffset.UTC);

        servicio = new SolicitudTarjetaServicio(
                repositorio,
                clienteRepositorio,
                tarjetaRepositorio,
                generadorNumeroTarjeta,
                relojFijo);
    }

    private Cliente clienteExistente() {
        return new Cliente(
                "Ana",
                "Pérez",
                "1234567-8",
                LocalDate.of(1990, 5, 20),
                "Zona 1",
                "ana@correo.com",
                "55551234",
                LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    private SolicitudTarjetaSolicitud solicitudValida() {
        return new SolicitudTarjetaSolicitud(
                1L,
                TipoTarjeta.CLASICA,
                new BigDecimal("5000.00"));
    }

    @Test
    void crearGuardaLaSolicitudComoPendiente() {
        Cliente cliente = clienteExistente();

        when(clienteRepositorio.findById(1L))
                .thenReturn(Optional.of(cliente));

        when(repositorio.save(any(SolicitudTarjeta.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        SolicitudTarjetaRespuesta respuesta =
                servicio.crear(solicitudValida());

        assertEquals(EstadoSolicitud.PENDIENTE, respuesta.estado());
        assertEquals(TipoTarjeta.CLASICA, respuesta.tipoTarjeta());
        assertEquals(
                new BigDecimal("5000.00"),
                respuesta.ingresoMensualDeclarado());
        assertEquals(
                LocalDateTime.of(2026, 10, 11, 12, 0),
                respuesta.fechaSolicitud());

        ArgumentCaptor<SolicitudTarjeta> capturador =
                ArgumentCaptor.forClass(SolicitudTarjeta.class);

        verify(repositorio).save(capturador.capture());
        verify(clienteRepositorio).findById(1L);

        assertEquals(
                cliente.getNumeroIdentificacion(),
                capturador.getValue().getCliente().getNumeroIdentificacion());
    }

    @Test
    void crearConClienteInexistenteFallaYNoGuarda() {
        when(clienteRepositorio.findById(99L))
                .thenReturn(Optional.empty());

        SolicitudTarjetaSolicitud solicitud = new SolicitudTarjetaSolicitud(
                99L,
                TipoTarjeta.CLASICA,
                new BigDecimal("5000.00"));

        ExcepcionNegocio error = assertThrows(
                ExcepcionNegocio.class,
                () -> servicio.crear(solicitud));

        assertEquals(
                CodigoError.CLIENTE_NO_ENCONTRADO,
                error.getCodigo());

        verify(repositorio, never()).save(any());
    }

    @Test
    void listarDevuelveLasSolicitudes() {
        SolicitudTarjeta solicitud = new SolicitudTarjeta(
                clienteExistente(),
                TipoTarjeta.ORO,
                new BigDecimal("8000.00"),
                LocalDateTime.of(2026, 10, 10, 10, 0));

        when(repositorio.findAll()).thenReturn(List.of(solicitud));

        List<SolicitudTarjetaRespuesta> respuestas = servicio.listar();

        assertEquals(1, respuestas.size());
        assertEquals(TipoTarjeta.ORO, respuestas.get(0).tipoTarjeta());
        assertEquals(EstadoSolicitud.PENDIENTE, respuestas.get(0).estado());
    }

    @Test
    void obtenerUnaSolicitudInexistenteFalla() {
        when(repositorio.findById(99L))
                .thenReturn(Optional.empty());

        ExcepcionNegocio error = assertThrows(
                ExcepcionNegocio.class,
                () -> servicio.obtener(99L));

        assertEquals(
                CodigoError.SOLICITUD_NO_ENCONTRADA,
                error.getCodigo());
    }

    
@Test
void aprobarSolicitudGuardaUnaTarjeta() {
    Cliente cliente = clienteExistente();

    SolicitudTarjeta solicitud = new SolicitudTarjeta(
            cliente,
            TipoTarjeta.CLASICA,
            new BigDecimal("5000.00"),
            LocalDateTime.of(2026, 10, 10, 10, 0));

    when(repositorio.findById(1L))
            .thenReturn(Optional.of(solicitud));

    when(generadorNumeroTarjeta.generar())
            .thenReturn("1234567890123456");

    when(tarjetaRepositorio.existsByNumeroTarjeta("1234567890123456"))
            .thenReturn(false);

    when(tarjetaRepositorio.save(any()))
            .thenAnswer(invocacion -> invocacion.getArgument(0));

    SolicitudTarjetaAprobacion aprobacion =
            new SolicitudTarjetaAprobacion(new BigDecimal("10000.00"));

    SolicitudTarjetaRespuesta respuesta =
            servicio.aprobar(1L, aprobacion);

    assertEquals(EstadoSolicitud.APROBADA, respuesta.estado());
    assertEquals(new BigDecimal("10000.00"), respuesta.montoAprobado());

    verify(tarjetaRepositorio, times(1)).save(any());
}

@Test
void rechazarSolicitudNoGuardaUnaTarjeta() {
    SolicitudTarjeta solicitud = new SolicitudTarjeta(
            clienteExistente(),
            TipoTarjeta.CLASICA,
            new BigDecimal("5000.00"),
            LocalDateTime.of(2026, 10, 10, 10, 0));

    when(repositorio.findById(1L))
            .thenReturn(Optional.of(solicitud));

    SolicitudTarjetaRechazo rechazo =
            new SolicitudTarjetaRechazo("Ingresos insuficientes.");

    SolicitudTarjetaRespuesta respuesta =
            servicio.rechazar(1L, rechazo);

    assertEquals(EstadoSolicitud.RECHAZADA, respuesta.estado());
    assertEquals(
            "Ingresos insuficientes.",
            respuesta.comentarioDecision());

    verify(tarjetaRepositorio, never()).save(any());
}

@Test
void aprobarSolicitudYaAprobadaFalla() {
    SolicitudTarjeta solicitud = new SolicitudTarjeta(
            clienteExistente(),
            TipoTarjeta.CLASICA,
            new BigDecimal("5000.00"),
            LocalDateTime.of(2026, 10, 10, 10, 0));

    solicitud.aprobar(
            new BigDecimal("10000.00"),
            LocalDateTime.of(2026, 10, 11, 11, 0));

    when(repositorio.findById(1L))
            .thenReturn(Optional.of(solicitud));

    SolicitudTarjetaAprobacion aprobacion =
            new SolicitudTarjetaAprobacion(new BigDecimal("12000.00"));

    ExcepcionNegocio error = assertThrows(
            ExcepcionNegocio.class,
            () -> servicio.aprobar(1L, aprobacion));

    assertEquals(CodigoError.SOLICITUD_NO_PENDIENTE, error.getCodigo());

    verify(tarjetaRepositorio, never()).save(any());
}

}
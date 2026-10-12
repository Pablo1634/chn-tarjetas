package gt.chn.tarjetas.tarjeta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gt.chn.tarjetas.cliente.Cliente;
import gt.chn.tarjetas.compartido.error.CodigoError;
import gt.chn.tarjetas.compartido.error.ExcepcionNegocio;
import gt.chn.tarjetas.solicitud.SolicitudTarjeta;
import gt.chn.tarjetas.solicitud.TipoTarjeta;

class MovimientoTarjetaServicioTest {

    private TarjetaRepositorio tarjetaRepositorio;
    private MovimientoTarjetaRepositorio movimientoRepositorio;
    private MovimientoTarjetaServicio servicio;

    @BeforeEach
    void preparar() {
        tarjetaRepositorio = mock(TarjetaRepositorio.class);
        movimientoRepositorio = mock(MovimientoTarjetaRepositorio.class);

        Clock reloj = Clock.fixed(
                Instant.parse("2026-10-11T12:00:00Z"),
                ZoneOffset.UTC);

        servicio = new MovimientoTarjetaServicio(
                tarjetaRepositorio,
                movimientoRepositorio,
                reloj);
    }

    private Tarjeta crearTarjeta(BigDecimal limite, BigDecimal saldo) {
        Cliente cliente = new Cliente(
                "Ana",
                "Pérez",
                "1234567-8",
                LocalDate.of(1990, 5, 20),
                "Zona 1",
                "ana@correo.com",
                "55551234",
                LocalDateTime.of(2026, 1, 1, 0, 0));

        SolicitudTarjeta solicitud = new SolicitudTarjeta(
                cliente,
                TipoTarjeta.CLASICA,
                new BigDecimal("5000.00"),
                LocalDateTime.of(2026, 10, 10, 10, 0));

        Tarjeta tarjeta = new Tarjeta(
                "1234567890123456",
                cliente,
                solicitud,
                TipoTarjeta.CLASICA,
                limite,
                LocalDateTime.of(2026, 10, 10, 10, 0));

        if (saldo.compareTo(BigDecimal.ZERO) > 0) {
            tarjeta.registrarConsumo(saldo);
        }

        return tarjeta;
    }

    @Test
    void consumoQueSuperaLimiteEsRechazado() {
        Tarjeta tarjeta = crearTarjeta(
                new BigDecimal("1000.00"),
                new BigDecimal("900.00"));

        when(tarjetaRepositorio.findById(1L))
                .thenReturn(Optional.of(tarjeta));

        ConsumoSolicitud consumo = new ConsumoSolicitud(
                new BigDecimal("200.00"),
                "Compra de prueba");

        ExcepcionNegocio error = assertThrows(
                ExcepcionNegocio.class,
                () -> servicio.registrarConsumo(1L, consumo));

        assertEquals(
                CodigoError.LIMITE_CREDITO_EXCEDIDO,
                error.getCodigo());

        verify(movimientoRepositorio, never()).save(
                org.mockito.ArgumentMatchers.any(MovimientoTarjeta.class));
    }

    @Test
    void pagoQueSuperaSaldoEsRechazado() {
        Tarjeta tarjeta = crearTarjeta(
                new BigDecimal("1000.00"),
                new BigDecimal("300.00"));

        when(tarjetaRepositorio.findById(1L))
                .thenReturn(Optional.of(tarjeta));

        ExcepcionNegocio error = assertThrows(
                ExcepcionNegocio.class,
                () -> servicio.registrarPago(
                        1L,
                        new BigDecimal("400.00"),
                        "Pago de prueba"));

        assertEquals(
                CodigoError.PAGO_EXCEDE_SALDO,
                error.getCodigo());

        verify(movimientoRepositorio, never()).save(
                org.mockito.ArgumentMatchers.any(MovimientoTarjeta.class));
    }

@Test
void consumoValidoActualizaElSaldoYGuardaMovimiento() {
    Tarjeta tarjeta = crearTarjeta(
            new BigDecimal("1000.00"),
            new BigDecimal("200.00"));

    when(tarjetaRepositorio.findById(1L))
            .thenReturn(Optional.of(tarjeta));

    ConsumoSolicitud consumo = new ConsumoSolicitud(
            new BigDecimal("300.00"),
            "Compra de prueba");

    when(movimientoRepositorio.save(
            org.mockito.ArgumentMatchers.any(MovimientoTarjeta.class)))
            .thenAnswer(invocacion -> invocacion.getArgument(0));

    MovimientoTarjeta movimiento =
            servicio.registrarConsumo(1L, consumo);

    assertEquals(
            new BigDecimal("500.00"),
            tarjeta.getSaldoActual());

    assertEquals(TipoMovimiento.CONSUMO, movimiento.getTipoMovimiento());
    assertEquals(new BigDecimal("300.00"), movimiento.getMonto());

    verify(tarjetaRepositorio).save(tarjeta);
    verify(movimientoRepositorio).save(
            org.mockito.ArgumentMatchers.any(MovimientoTarjeta.class));
}

@Test
void pagoValidoReduceElSaldoYGuardaMovimiento() {
    Tarjeta tarjeta = crearTarjeta(
            new BigDecimal("1000.00"),
            new BigDecimal("300.00"));

    when(tarjetaRepositorio.findById(1L))
            .thenReturn(Optional.of(tarjeta));

    when(movimientoRepositorio.save(
            org.mockito.ArgumentMatchers.any(MovimientoTarjeta.class)))
            .thenAnswer(invocacion -> invocacion.getArgument(0));

    MovimientoTarjeta movimiento = servicio.registrarPago(
            1L,
            new BigDecimal("100.00"),
            "Pago de prueba");

    assertEquals(
            new BigDecimal("200.00"),
            tarjeta.getSaldoActual());

    assertEquals(TipoMovimiento.PAGO, movimiento.getTipoMovimiento());
    assertEquals(new BigDecimal("100.00"), movimiento.getMonto());

    verify(tarjetaRepositorio).save(tarjeta);
    verify(movimientoRepositorio).save(
            org.mockito.ArgumentMatchers.any(MovimientoTarjeta.class));
}
}
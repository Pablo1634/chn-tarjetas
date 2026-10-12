package gt.chn.tarjetas.tarjeta;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gt.chn.tarjetas.compartido.error.CodigoError;
import gt.chn.tarjetas.compartido.error.ExcepcionNegocio;

@Service
public class MovimientoTarjetaServicio {

    private final TarjetaRepositorio tarjetaRepositorio;
    private final MovimientoTarjetaRepositorio movimientoRepositorio;
    private final Clock reloj;

    public MovimientoTarjetaServicio(
            TarjetaRepositorio tarjetaRepositorio,
            MovimientoTarjetaRepositorio movimientoRepositorio,
            Clock reloj) {
        this.tarjetaRepositorio = tarjetaRepositorio;
        this.movimientoRepositorio = movimientoRepositorio;
        this.reloj = reloj;
    }

    @Transactional
    public MovimientoTarjeta registrarConsumo(
            Long tarjetaId,
            ConsumoSolicitud solicitud) {

        Tarjeta tarjeta = buscarTarjeta(tarjetaId);
        BigDecimal monto = solicitud.monto();

        BigDecimal nuevoSaldo = tarjeta.getSaldoActual().add(monto);

        if (nuevoSaldo.compareTo(tarjeta.getLimiteCredito()) > 0) {
            throw new ExcepcionNegocio(
                    CodigoError.LIMITE_CREDITO_EXCEDIDO,
                    "El consumo supera el límite de crédito disponible.");
        }

        tarjeta.registrarConsumo(monto);

        MovimientoTarjeta movimiento = new MovimientoTarjeta(
                tarjeta,
                TipoMovimiento.CONSUMO,
                monto,
                LocalDate.now(reloj),
                solicitud.descripcion().trim(),
                LocalDateTime.now(reloj));

        tarjetaRepositorio.save(tarjeta);
        return movimientoRepositorio.save(movimiento);
    }

    @Transactional
    public MovimientoTarjeta registrarPago(
            Long tarjetaId,
            BigDecimal monto,
            String descripcion) {

        Tarjeta tarjeta = buscarTarjeta(tarjetaId);

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El monto del pago debe ser mayor que cero.");
        }

        if (monto.compareTo(tarjeta.getSaldoActual()) > 0) {
            throw new ExcepcionNegocio(
                    CodigoError.PAGO_EXCEDE_SALDO,
                    "El pago no puede superar el saldo pendiente.");
        }

        tarjeta.registrarPago(monto);

        MovimientoTarjeta movimiento = new MovimientoTarjeta(
                tarjeta,
                TipoMovimiento.PAGO,
                monto,
                LocalDate.now(reloj),
                descripcion == null ? "Pago de tarjeta" : descripcion.trim(),
                LocalDateTime.now(reloj));

        tarjetaRepositorio.save(tarjeta);
        return movimientoRepositorio.save(movimiento);
    }

    private Tarjeta buscarTarjeta(Long id) {
        Optional<Tarjeta> encontrada = tarjetaRepositorio.findById(id);

        if (encontrada.isEmpty()) {
            throw new ExcepcionNegocio(
                    CodigoError.TARJETA_NO_ENCONTRADA,
                    "No existe una tarjeta activa con id " + id + ".");
        }

        return encontrada.get();
    }

    @Transactional(readOnly = true)
public java.util.List<MovimientoTarjetaRespuesta> listarPorTarjeta(
        Long tarjetaId) {

    buscarTarjeta(tarjetaId);

    java.util.List<MovimientoTarjeta> movimientos =
            movimientoRepositorio
                    .findByTarjetaIdOrderByFechaMovimientoDescIdDesc(tarjetaId);

    java.util.List<MovimientoTarjetaRespuesta> respuestas =
            new java.util.ArrayList<>();

    for (MovimientoTarjeta movimiento : movimientos) {
        respuestas.add(MovimientoTarjetaRespuesta.desde(movimiento));
    }

    return respuestas;
}
}
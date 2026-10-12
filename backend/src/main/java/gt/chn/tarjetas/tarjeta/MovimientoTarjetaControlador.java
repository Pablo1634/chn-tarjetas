package gt.chn.tarjetas.tarjeta;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tarjetas/{tarjetaId}")
public class MovimientoTarjetaControlador {

    private final MovimientoTarjetaServicio servicio;

    public MovimientoTarjetaControlador(
            MovimientoTarjetaServicio servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/consumos")
    public ResponseEntity<MovimientoTarjetaRespuesta> registrarConsumo(
            @PathVariable Long tarjetaId,
            @Valid @RequestBody ConsumoSolicitud solicitud) {

        MovimientoTarjeta movimiento =
                servicio.registrarConsumo(tarjetaId, solicitud);

        return ResponseEntity.status(201)
                .body(MovimientoTarjetaRespuesta.desde(movimiento));
    }

    @PostMapping("/pagos")
    public ResponseEntity<MovimientoTarjetaRespuesta> registrarPago(
            @PathVariable Long tarjetaId,
            @Valid @RequestBody PagoSolicitud solicitud) {

        MovimientoTarjeta movimiento = servicio.registrarPago(
                tarjetaId,
                solicitud.monto(),
                solicitud.descripcion());

        return ResponseEntity.status(201)
                .body(MovimientoTarjetaRespuesta.desde(movimiento));
    }

    @GetMapping("/movimientos")
    public ResponseEntity<List<MovimientoTarjetaRespuesta>> listarMovimientos(
            @PathVariable Long tarjetaId) {

        return ResponseEntity.ok(
                servicio.listarPorTarjeta(tarjetaId));
    }
}
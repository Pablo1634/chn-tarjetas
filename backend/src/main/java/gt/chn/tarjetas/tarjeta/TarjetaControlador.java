package gt.chn.tarjetas.tarjeta;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tarjetas")
public class TarjetaControlador {

    private final TarjetaServicio servicio;

    public TarjetaControlador(TarjetaServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public ResponseEntity<List<TarjetaRespuesta>> listar() {
        return ResponseEntity.ok(servicio.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarjetaRespuesta> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.obtener(id));
    }
}
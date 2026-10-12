package gt.chn.tarjetas.solicitud;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudTarjetaControlador {

    private final SolicitudTarjetaServicio servicio;

    public SolicitudTarjetaControlador(SolicitudTarjetaServicio servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<SolicitudTarjetaRespuesta> crear(
            @Valid @RequestBody SolicitudTarjetaSolicitud solicitud) {

        SolicitudTarjetaRespuesta respuesta = servicio.crear(solicitud);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();

        return ResponseEntity.created(ubicacion).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<SolicitudTarjetaRespuesta>> listar() {
        return ResponseEntity.ok(servicio.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudTarjetaRespuesta> obtener(
            @PathVariable Long id) {

        return ResponseEntity.ok(servicio.obtener(id));
    }

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<SolicitudTarjetaRespuesta> aprobar(
            @PathVariable Long id,
            @Valid @RequestBody SolicitudTarjetaAprobacion aprobacion) {

        SolicitudTarjetaRespuesta respuesta =
                servicio.aprobar(id, aprobacion);

        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudTarjetaRespuesta> rechazar(
            @PathVariable Long id,
            @Valid @RequestBody SolicitudTarjetaRechazo rechazo) {

        SolicitudTarjetaRespuesta respuesta =
                servicio.rechazar(id, rechazo);

        return ResponseEntity.ok(respuesta);
    }
}
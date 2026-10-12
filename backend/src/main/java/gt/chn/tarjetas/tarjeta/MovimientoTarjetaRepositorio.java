package gt.chn.tarjetas.tarjeta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoTarjetaRepositorio
        extends JpaRepository<MovimientoTarjeta, Long> {

    List<MovimientoTarjeta> findByTarjetaIdOrderByFechaMovimientoDescIdDesc(
            Long tarjetaId);
}
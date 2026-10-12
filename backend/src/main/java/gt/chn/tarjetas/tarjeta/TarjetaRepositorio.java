package gt.chn.tarjetas.tarjeta;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TarjetaRepositorio extends JpaRepository<Tarjeta, Long> {

    boolean existsByNumeroTarjeta(String numeroTarjeta);
}
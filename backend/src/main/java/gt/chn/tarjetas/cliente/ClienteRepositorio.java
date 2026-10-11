package gt.chn.tarjetas.cliente;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {

    /** Lista los clientes activos, ordenados por apellido y nombre. */
    List<Cliente> findAllByOrderByApellidoAscNombreAsc();
    @Query(value = "SELECT COUNT(*) FROM cliente WHERE numero_identificacion = :numero",
            nativeQuery = true)
    long contarPorIdentificacionIncluidosEliminados(@Param("numero") String numero);
}
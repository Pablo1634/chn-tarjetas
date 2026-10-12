package gt.chn.tarjetas.tarjeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gt.chn.tarjetas.compartido.error.CodigoError;
import gt.chn.tarjetas.compartido.error.ExcepcionNegocio;

@Service
public class TarjetaServicio {

    private final TarjetaRepositorio repositorio;

    public TarjetaServicio(TarjetaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<TarjetaRespuesta> listar() {
        List<Tarjeta> tarjetas = repositorio.findAll();
        List<TarjetaRespuesta> respuestas = new ArrayList<>();

        for (Tarjeta tarjeta : tarjetas) {
            respuestas.add(TarjetaRespuesta.desde(tarjeta));
        }

        return respuestas;
    }

    @Transactional(readOnly = true)
    public TarjetaRespuesta obtener(Long id) {
        Optional<Tarjeta> encontrada = repositorio.findById(id);

        if (encontrada.isEmpty()) {
            throw new ExcepcionNegocio(
                    CodigoError.TARJETA_NO_ENCONTRADA,
                    "No existe una tarjeta activa con id " + id + ".");
        }

        return TarjetaRespuesta.desde(encontrada.get());
    }
}
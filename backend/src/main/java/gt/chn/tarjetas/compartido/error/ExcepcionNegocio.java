package gt.chn.tarjetas.compartido.error;

/**
 * Error de regla de negocio. Lleva un código estable y un mensaje para la persona usuaria.
 */
public class ExcepcionNegocio extends RuntimeException {

    private final CodigoError codigo;

    public ExcepcionNegocio(CodigoError codigo, String detalle) {
        super(detalle);
        this.codigo = codigo;
    }

    public CodigoError getCodigo() {
        return codigo;
    }
}
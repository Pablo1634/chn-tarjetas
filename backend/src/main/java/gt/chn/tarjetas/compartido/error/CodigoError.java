package gt.chn.tarjetas.compartido.error;

import org.springframework.http.HttpStatus;

/**
 * Códigos de error estables que el frontend puede interpretar sin leer mensajes de texto.
 */
public enum CodigoError {

    ERROR_VALIDACION(HttpStatus.BAD_REQUEST, "Datos inválidos"),
    CLIENTE_NO_ENCONTRADO(HttpStatus.NOT_FOUND, "Cliente no encontrado"),
    SOLICITUD_NO_ENCONTRADA(HttpStatus.NOT_FOUND, "Solicitud no encontrada"),
    TARJETA_NO_ENCONTRADA(HttpStatus.NOT_FOUND, "Tarjeta no encontrada"),
    IDENTIFICACION_YA_EXISTE(HttpStatus.CONFLICT, "Identificación ya registrada"),
    SOLICITUD_NO_PENDIENTE(HttpStatus.CONFLICT, "La solicitud ya fue decidida"),
    ACTUALIZACION_CONCURRENTE(HttpStatus.CONFLICT, "Conflicto de concurrencia"),
    LIMITE_CREDITO_EXCEDIDO(HttpStatus.valueOf(422), "Límite de crédito excedido"),
    PAGO_EXCEDE_SALDO(HttpStatus.valueOf(422), "El pago excede el saldo"),
    ERROR_INTERNO(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno");

    private final HttpStatus estado;
    private final String titulo;

    CodigoError(HttpStatus estado, String titulo) {
        this.estado = estado;
        this.titulo = titulo;
    }

    public HttpStatus getEstado() {
        return estado;
    }

    public String getTitulo() {
        return titulo;
    }
}

package gt.chn.tarjetas.solicitud;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.SQLRestriction;

import gt.chn.tarjetas.cliente.Cliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "solicitud_tarjeta")
@SQLRestriction("eliminado_en IS NULL")
public class SolicitudTarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false, updatable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_tarjeta", nullable = false, length = 10, updatable = false)
    private TipoTarjeta tipoTarjeta;

    @Column(name = "ingreso_mensual_declarado", nullable = false, precision = 12, scale = 2, updatable = false)
    private BigDecimal ingresoMensualDeclarado;

    @Column(name = "fecha_solicitud", nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 10)
    private EstadoSolicitud estado;

    @Column(name = "fecha_decision")
    private LocalDateTime fechaDecision;

    @Nationalized
    @Column(name = "comentario_decision", length = 500)
    private String comentarioDecision;

    @Column(name = "monto_aprobado", precision = 12, scale = 2)
    private BigDecimal montoAprobado;

    protected SolicitudTarjeta() {
    }

    public SolicitudTarjeta(
            Cliente cliente,
            TipoTarjeta tipoTarjeta,
            BigDecimal ingresoMensualDeclarado,
            LocalDateTime fechaSolicitud) {
        this.cliente = cliente;
        this.tipoTarjeta = tipoTarjeta;
        this.ingresoMensualDeclarado = ingresoMensualDeclarado;
        this.fechaSolicitud = fechaSolicitud;
        this.estado = EstadoSolicitud.PENDIENTE;
    }

    public void aprobar(BigDecimal montoAprobado, LocalDateTime fechaDecision) {
        this.estado = EstadoSolicitud.APROBADA;
        this.montoAprobado = montoAprobado;
        this.fechaDecision = fechaDecision;
        this.comentarioDecision = null;
    }

    public void rechazar(String comentarioDecision, LocalDateTime fechaDecision) {
        this.estado = EstadoSolicitud.RECHAZADA;
        this.montoAprobado = null;
        this.comentarioDecision = comentarioDecision;
        this.fechaDecision = fechaDecision;
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public TipoTarjeta getTipoTarjeta() {
        return tipoTarjeta;
    }

    public BigDecimal getIngresoMensualDeclarado() {
        return ingresoMensualDeclarado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public LocalDateTime getFechaDecision() {
        return fechaDecision;
    }

    public String getComentarioDecision() {
        return comentarioDecision;
    }

    public BigDecimal getMontoAprobado() {
        return montoAprobado;
    }
}
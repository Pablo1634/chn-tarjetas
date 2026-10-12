package gt.chn.tarjetas.tarjeta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import gt.chn.tarjetas.cliente.Cliente;
import gt.chn.tarjetas.solicitud.SolicitudTarjeta;
import gt.chn.tarjetas.solicitud.TipoTarjeta;
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
@Table(name = "tarjeta")
@SQLRestriction("eliminado_en IS NULL")
public class Tarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "numero_tarjeta", nullable = false, length = 16, updatable = false)
    private String numeroTarjeta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false, updatable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitud_id", nullable = false, updatable = false)
    private SolicitudTarjeta solicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_tarjeta", nullable = false, length = 10, updatable = false)
    private TipoTarjeta tipoTarjeta;

    @Column(name = "limite_credito", nullable = false, precision = 12, scale = 2, updatable = false)
    private BigDecimal limiteCredito;

    @Column(name = "saldo_actual", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoActual;

    @Column(name = "fecha_emision", nullable = false, updatable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    protected Tarjeta() {
    }

    public Tarjeta(
            String numeroTarjeta,
            Cliente cliente,
            SolicitudTarjeta solicitud,
            TipoTarjeta tipoTarjeta,
            BigDecimal limiteCredito,
            LocalDateTime fechaEmision) {

        this.numeroTarjeta = numeroTarjeta;
        this.cliente = cliente;
        this.solicitud = solicitud;
        this.tipoTarjeta = tipoTarjeta;
        this.limiteCredito = limiteCredito;
        this.saldoActual = BigDecimal.ZERO;
        this.fechaEmision = fechaEmision;
    }

    public Long getId() {
        return id;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public SolicitudTarjeta getSolicitud() {
        return solicitud;
    }

    public TipoTarjeta getTipoTarjeta() {
        return tipoTarjeta;
    }

    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public BigDecimal getSaldoActual() {
        return saldoActual;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public LocalDateTime getEliminadoEn() {
        return eliminadoEn;
    }

    public void registrarConsumo(BigDecimal monto) {
    this.saldoActual = this.saldoActual.add(monto);
    }

    public void registrarPago(BigDecimal monto) {
    this.saldoActual = this.saldoActual.subtract(monto);
    }
}
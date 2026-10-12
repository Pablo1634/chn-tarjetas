package gt.chn.tarjetas.tarjeta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
@Table(name = "movimiento_tarjeta")
public class MovimientoTarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tarjeta_id", nullable = false, updatable = false)
    private Tarjeta tarjeta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 10, updatable = false)
    private TipoMovimiento tipoMovimiento;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2, updatable = false)
    private BigDecimal monto;

    @Column(name = "fecha_movimiento", nullable = false, updatable = false)
    private LocalDate fechaMovimiento;

    @Column(name = "descripcion", nullable = false, length = 200, updatable = false)
    private String descripcion;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    protected MovimientoTarjeta() {
    }

    public MovimientoTarjeta(
            Tarjeta tarjeta,
            TipoMovimiento tipoMovimiento,
            BigDecimal monto,
            LocalDate fechaMovimiento,
            String descripcion,
            LocalDateTime creadoEn) {

        this.tarjeta = tarjeta;
        this.tipoMovimiento = tipoMovimiento;
        this.monto = monto;
        this.fechaMovimiento = fechaMovimiento;
        this.descripcion = descripcion;
        this.creadoEn = creadoEn;
    }

    public Long getId() {
        return id;
    }

    public Tarjeta getTarjeta() {
        return tarjeta;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDate getFechaMovimiento() {
        return fechaMovimiento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
}
package gt.chn.tarjetas.cliente;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
@SQLRestriction("eliminado_en IS NULL")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Nationalized
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "numero_identificacion", nullable = false, length = 30, updatable = false)
    private String numeroIdentificacion;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Nationalized
    @Column(name = "direccion", nullable = false, length = 250)
    private String direccion;

    @Nationalized
    @Column(name = "correo_electronico", nullable = false, length = 150)
    private String correoElectronico;

    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    /** Constructor vacío: lo exige JPA para crear objetos al leer de la base. */
    protected Cliente() {
    }

    public Cliente(String nombre, String apellido, String numeroIdentificacion,
            LocalDate fechaNacimiento, String direccion, String correoElectronico,
            String telefono, LocalDateTime ahora) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.numeroIdentificacion = numeroIdentificacion;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.correoElectronico = correoElectronico;
        this.telefono = telefono;
        this.creadoEn = ahora;
        this.actualizadoEn = ahora;
    }

    /** Edita los datos permitidos. La identificación no se puede cambiar (decisión A). */
    public void actualizarDatos(String nombre, String apellido, LocalDate fechaNacimiento,
            String direccion, String correoElectronico, String telefono, LocalDateTime ahora) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.correoElectronico = correoElectronico;
        this.telefono = telefono;
        this.actualizadoEn = ahora;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getNumeroIdentificacion() { return numeroIdentificacion; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getDireccion() { return direccion; }
    public String getCorreoElectronico() { return correoElectronico; }
    public String getTelefono() { return telefono; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public LocalDateTime getEliminadoEn() { return eliminadoEn; }
}
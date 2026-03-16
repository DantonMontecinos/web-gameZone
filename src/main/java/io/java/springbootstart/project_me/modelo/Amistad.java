package io.java.springbootstart.project_me.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una relación de amistad entre dos jugadores.
 *
 * Flujo:
 * 1. El solicitante envía una solicitud → se crea con estado PENDIENTE.
 * 2. El receptor puede ACEPTAR o RECHAZAR la solicitud.
 * 3. Solo cuando el estado es ACEPTADA, ambos usuarios pueden chatear entre sí.
 *
 * La relación es unidireccional en cuanto a quién la inició (solicitante → receptor),
 * pero una vez aceptada, la amistad es bidireccional.
 */
@Entity
@Table(name = "amistades",
       uniqueConstraints = @UniqueConstraint(columnNames = {"solicitante_id", "receptor_id"}))
public class Amistad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Jugador que envía la solicitud de amistad */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Jugador solicitante;

    /** Jugador que recibe la solicitud de amistad */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receptor_id", nullable = false)
    private Jugador receptor;

    /** Estado actual de la solicitud */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAmistad estado;

    /** Fecha y hora en que se envió la solicitud */
    @Column(name = "fecha_solicitud", nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    /** Fecha y hora en que se respondió (aceptó o rechazó) */
    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    // ========================
    // Constructor vacío (JPA)
    // ========================
    public Amistad() {
    }

    // ========================
    // Callback JPA: establece la fecha de solicitud automáticamente
    // ========================
    @PrePersist
    protected void onCreate() {
        this.fechaSolicitud = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoAmistad.PENDIENTE;
        }
    }

    // ========================
    // Getters y Setters
    // ========================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Jugador getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Jugador solicitante) {
        this.solicitante = solicitante;
    }

    public Jugador getReceptor() {
        return receptor;
    }

    public void setReceptor(Jugador receptor) {
        this.receptor = receptor;
    }

    public EstadoAmistad getEstado() {
        return estado;
    }

    public void setEstado(EstadoAmistad estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }
}

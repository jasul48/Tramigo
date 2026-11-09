package com.tramigo.seguimiento.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_seguimiento")
public class HistorialSeguimiento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitud_id", nullable = false)
    @JsonIgnore
    private Solicitud solicitud;
    @Column(nullable = false, length = 30)
    private String estado;
    @Column(length = 500)
    private String comentario;
    @Column(nullable = false)
    private LocalDateTime fecha;
    protected HistorialSeguimiento() {}
    public HistorialSeguimiento(Solicitud solicitud, String estado, String comentario) {
        this.solicitud = solicitud; this.estado = estado; this.comentario = comentario; this.fecha = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public String getEstado() { return estado; }
    public String getComentario() { return comentario; }
    public LocalDateTime getFecha() { return fecha; }
}

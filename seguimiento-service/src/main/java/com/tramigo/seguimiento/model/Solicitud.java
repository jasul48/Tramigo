package com.tramigo.seguimiento.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "solicitudes_seguimiento")
public class Solicitud {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;
    @Column(name = "tramite_id")
    private Integer tramiteId;
    @Column(nullable = false, length = 120)
    private String titulo;
    @Column(length = 1000)
    private String descripcion;
    @Column(nullable = false, length = 30)
    private String estado = "RECIBIDA";
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("fecha ASC")
    private List<HistorialSeguimiento> historial = new ArrayList<>();

    @PrePersist void alCrear() { fechaCreacion = LocalDateTime.now(); fechaActualizacion = fechaCreacion; }
    @PreUpdate void alActualizar() { fechaActualizacion = LocalDateTime.now(); }
    public void agregarEvento(String estado, String comentario) {
        HistorialSeguimiento evento = new HistorialSeguimiento(this, estado, comentario);
        historial.add(evento);
    }
    public Long getId() { return id; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public Integer getTramiteId() { return tramiteId; }
    public void setTramiteId(Integer tramiteId) { this.tramiteId = tramiteId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public List<HistorialSeguimiento> getHistorial() { return historial; }
}

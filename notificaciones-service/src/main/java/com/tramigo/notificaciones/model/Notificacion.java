package com.tramigo.notificaciones.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Notificaciones")
public class Notificacion {

    public static final String TIPO_CONTACTO = "CONTACTO";

    public static final String ESTADO_ENVIADA = "ENVIADA";
    public static final String ESTADO_FALLIDA = "FALLIDA";
    public static final String ESTADO_OMITIDA = "OMITIDA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notificacion_id")
    private Integer notificacionId;

    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo;

    @Column(name = "destinatario", length = 150)
    private String destinatario;

    @Column(name = "remitente_nombre", length = 100)
    private String remitenteNombre;

    @Column(name = "remitente_correo", length = 100)
    private String remitenteCorreo;

    @Column(name = "asunto", nullable = false, length = 200)
    private String asunto;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "detalle_error", length = 500)
    private String detalleError;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }

    public Notificacion() {
    }

    public Integer getNotificacionId() {
        return notificacionId;
    }

    public void setNotificacionId(Integer notificacionId) {
        this.notificacionId = notificacionId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getRemitenteNombre() {
        return remitenteNombre;
    }

    public void setRemitenteNombre(String remitenteNombre) {
        this.remitenteNombre = remitenteNombre;
    }

    public String getRemitenteCorreo() {
        return remitenteCorreo;
    }

    public void setRemitenteCorreo(String remitenteCorreo) {
        this.remitenteCorreo = remitenteCorreo;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDetalleError() {
        return detalleError;
    }

    public void setDetalleError(String detalleError) {
        this.detalleError = detalleError;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}

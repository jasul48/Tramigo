package com.tramigo.notificaciones.dto;

import com.tramigo.notificaciones.model.Notificacion;
import java.time.LocalDateTime;

public class NotificacionDTO {

    private Integer notificacionId;
    private String tipo;
    private String destinatario;
    private String remitenteNombre;
    private String remitenteCorreo;
    private String asunto;
    private String estado;
    private String detalleError;
    private LocalDateTime fechaCreacion;

    public NotificacionDTO() {
    }

    public static NotificacionDTO fromEntity(Notificacion n) {
        if (n == null) return null;
        NotificacionDTO dto = new NotificacionDTO();
        dto.setNotificacionId(n.getNotificacionId());
        dto.setTipo(n.getTipo());
        dto.setDestinatario(n.getDestinatario());
        dto.setRemitenteNombre(n.getRemitenteNombre());
        dto.setRemitenteCorreo(n.getRemitenteCorreo());
        dto.setAsunto(n.getAsunto());
        dto.setEstado(n.getEstado());
        dto.setDetalleError(n.getDetalleError());
        dto.setFechaCreacion(n.getFechaCreacion());
        return dto;
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

package com.tramigo.soporte.dto;

import com.tramigo.soporte.model.Soporte;
import java.time.LocalDateTime;

public class TicketDTO {

    private Integer ticketId;
    private String nombre;
    private String correo;
    private String asunto;
    private String mensaje;
    private String estado;
    private LocalDateTime fechaCreacion;

    public TicketDTO() {
    }

    public static TicketDTO fromEntity(Soporte s) {
        if (s == null) return null;
        TicketDTO dto = new TicketDTO();
        dto.setTicketId(s.getTicketId());
        dto.setNombre(s.getNombre());
        dto.setCorreo(s.getCorreo());
        dto.setAsunto(s.getAsunto());
        dto.setMensaje(s.getMensaje());
        dto.setEstado(s.getEstado());
        dto.setFechaCreacion(s.getFechaCreacion());
        return dto;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}

package com.tramigo.usuarios.dto;

import com.tramigo.usuarios.model.Usuario;
import java.time.LocalDateTime;

public class UsuarioDTO {

    private String usuarioId;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
    private String fotoPerfil;
    private LocalDateTime fechaRegistro;

    public UsuarioDTO() {
    }

    public static UsuarioDTO fromEntity(Usuario u) {
        if (u == null) return null;
        UsuarioDTO dto = new UsuarioDTO();
        dto.setUsuarioId(u.getUsuarioId());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setTelefono(u.getTelefono());
        dto.setCorreo(u.getCorreo());
        dto.setFotoPerfil(u.getFotoPerfil());
        dto.setFechaRegistro(u.getFechaRegistro());
        return dto;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}

package com.tramigo.documentos.dto;

import com.tramigo.documentos.model.Documento;
import java.time.LocalDateTime;

public class DocumentoDTO {

    private Integer documentoId;
    private String usuarioId;
    private String nombrePersonalizado;
    private String nombreArchivo;
    private String tipoArchivo;
    private LocalDateTime fechaSubida;

    public DocumentoDTO() {
    }

    public static DocumentoDTO fromEntity(Documento d) {
        if (d == null) return null;
        DocumentoDTO dto = new DocumentoDTO();
        dto.setDocumentoId(d.getDocumentoId());
        dto.setUsuarioId(d.getUsuarioId());
        dto.setNombrePersonalizado(d.getNombrePersonalizado());
        dto.setNombreArchivo(d.getNombreArchivo());
        dto.setTipoArchivo(d.getTipoArchivo());
        dto.setFechaSubida(d.getFechaSubida());
        return dto;
    }

    public Integer getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(Integer documentoId) {
        this.documentoId = documentoId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombrePersonalizado() {
        return nombrePersonalizado;
    }

    public void setNombrePersonalizado(String nombrePersonalizado) {
        this.nombrePersonalizado = nombrePersonalizado;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }
}

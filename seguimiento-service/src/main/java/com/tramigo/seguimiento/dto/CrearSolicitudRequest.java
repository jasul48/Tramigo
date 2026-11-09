package com.tramigo.seguimiento.dto;

public record CrearSolicitudRequest(Integer usuarioId, Integer tramiteId, String titulo, String descripcion) {}

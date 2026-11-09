package com.tramigo.tramites.model;

import jakarta.persistence.*;

/**
 * Entrada del catálogo de Tramigo: un trámite (INE, CURP, pasaporte...) o un banco
 * dentro del trámite "Apertura de cuenta bancaria" (campo grupo = "cuenta-bancaria").
 */
@Entity
@Table(name = "Tramites")
public class Tramite {

    public static final String TIPO_TRAMITE = "TRAMITE";
    public static final String TIPO_BANCO = "BANCO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tramite_id")
    private Integer tramiteId;

    @Column(name = "slug", unique = true, nullable = false, length = 60)
    private String slug;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "categoria", nullable = false, length = 50)
    private String categoria;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "imagen", length = 255)
    private String imagen;

    @Column(name = "ruta_guia", length = 255)
    private String rutaGuia;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;

    @Column(name = "grupo", length = 60)
    private String grupo;

    @Column(name = "orden")
    private Integer orden;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public Tramite() {
    }

    public Tramite(String slug, String nombre, String categoria, String descripcion,
                   String imagen, String rutaGuia, String tipo, String grupo, Integer orden) {
        this.slug = slug;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.rutaGuia = rutaGuia;
        this.tipo = tipo;
        this.grupo = grupo;
        this.orden = orden;
        this.activo = Boolean.TRUE;
    }

    public Integer getTramiteId() {
        return tramiteId;
    }

    public void setTramiteId(Integer tramiteId) {
        this.tramiteId = tramiteId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getRutaGuia() {
        return rutaGuia;
    }

    public void setRutaGuia(String rutaGuia) {
        this.rutaGuia = rutaGuia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}

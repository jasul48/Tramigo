package com.tramigo.tramites.service;

import com.tramigo.tramites.model.Tramite;
import com.tramigo.tramites.repository.TramiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class TramiteService {

    private final TramiteRepository tramiteRepository;

    public TramiteService(TramiteRepository tramiteRepository) {
        this.tramiteRepository = tramiteRepository;
    }

    /**
     * Lista el catálogo activo.
     * - grupo == null: solo trámites de primer nivel (los de la pantalla /categorias).
     * - grupo != null: los elementos de ese grupo (ej. "cuenta-bancaria" -> los bancos).
     */
    public List<Tramite> listar(String categoria, String q, String grupo) {
        String cat = normalizar(categoria);
        String busqueda = normalizar(q);
        String grp = grupo == null ? null : grupo.trim();

        return tramiteRepository.findByActivoTrueOrderByOrdenAscNombreAsc().stream()
                .filter(t -> (grp == null || grp.isEmpty())
                        ? t.getGrupo() == null
                        : grp.equalsIgnoreCase(t.getGrupo()))
                .filter(t -> cat.isEmpty() || normalizar(t.getCategoria()).equals(cat))
                .filter(t -> busqueda.isEmpty()
                        || normalizar(t.getNombre()).contains(busqueda)
                        || normalizar(t.getDescripcion()).contains(busqueda))
                .toList();
    }

    public List<String> categorias() {
        return tramiteRepository.findByActivoTrueOrderByOrdenAscNombreAsc().stream()
                .filter(t -> t.getGrupo() == null)
                .map(Tramite::getCategoria)
                .distinct()
                .toList();
    }

    public Optional<Tramite> buscarPorSlug(String slug) {
        return tramiteRepository.findBySlug(slug);
    }

    @Transactional
    public Tramite crear(Tramite datos) {
        datos.setTramiteId(null);
        datos.setSlug(slugify(datos.getSlug()));
        if (tramiteRepository.existsBySlug(datos.getSlug())) {
            throw new IllegalStateException("Ya existe un trámite con el slug '" + datos.getSlug() + "'");
        }
        if (datos.getTipo() == null || datos.getTipo().isBlank()) {
            datos.setTipo(Tramite.TIPO_TRAMITE);
        }
        if (datos.getOrden() == null) {
            datos.setOrden(0);
        }
        if (datos.getActivo() == null) {
            datos.setActivo(Boolean.TRUE);
        }
        return tramiteRepository.save(datos);
    }

    @Transactional
    public Tramite actualizar(String slug, Tramite datos) {
        Tramite existente = tramiteRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Trámite no encontrado: " + slug));

        if (datos.getNombre() != null) existente.setNombre(datos.getNombre().trim());
        if (datos.getCategoria() != null) existente.setCategoria(datos.getCategoria().trim());
        if (datos.getDescripcion() != null) existente.setDescripcion(datos.getDescripcion().trim());
        if (datos.getImagen() != null) existente.setImagen(datos.getImagen());
        if (datos.getRutaGuia() != null) existente.setRutaGuia(datos.getRutaGuia());
        if (datos.getGrupo() != null) existente.setGrupo(datos.getGrupo().isBlank() ? null : datos.getGrupo().trim());
        if (datos.getOrden() != null) existente.setOrden(datos.getOrden());
        if (datos.getActivo() != null) existente.setActivo(datos.getActivo());
        return tramiteRepository.save(existente);
    }

    @Transactional
    public boolean eliminar(String slug) {
        Optional<Tramite> opt = tramiteRepository.findBySlug(slug);
        if (opt.isEmpty()) {
            return false;
        }
        tramiteRepository.delete(opt.get());
        return true;
    }

    private static String normalizar(String s) {
        if (s == null) return "";
        String sinAcentos = Normalizer.normalize(s.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinAcentos.toLowerCase(Locale.ROOT);
    }

    private static String slugify(String s) {
        String base = normalizar(s).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        if (base.isEmpty()) {
            throw new IllegalArgumentException("El slug es obligatorio");
        }
        return base;
    }
}

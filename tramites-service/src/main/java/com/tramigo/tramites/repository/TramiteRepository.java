package com.tramigo.tramites.repository;

import com.tramigo.tramites.model.Tramite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TramiteRepository extends JpaRepository<Tramite, Integer> {
    List<Tramite> findByActivoTrueOrderByOrdenAscNombreAsc();
    Optional<Tramite> findBySlug(String slug);
    boolean existsBySlug(String slug);
}

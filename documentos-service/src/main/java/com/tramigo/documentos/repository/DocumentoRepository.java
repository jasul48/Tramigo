package com.tramigo.documentos.repository;

import com.tramigo.documentos.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Integer> {
    List<Documento> findByUsuarioIdOrderByFechaSubidaDesc(String usuarioId);
    Optional<Documento> findByDocumentoIdAndUsuarioId(Integer documentoId, String usuarioId);
}

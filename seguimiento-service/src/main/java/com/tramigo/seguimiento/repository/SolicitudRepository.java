package com.tramigo.seguimiento.repository;

import com.tramigo.seguimiento.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByUsuarioIdOrderByFechaCreacionDesc(Integer usuarioId);
}

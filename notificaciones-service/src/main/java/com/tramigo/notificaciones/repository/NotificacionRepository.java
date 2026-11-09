package com.tramigo.notificaciones.repository;

import com.tramigo.notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findAllByOrderByFechaCreacionDesc();
    List<Notificacion> findByEstadoOrderByFechaCreacionDesc(String estado);
}

package com.tramigo.soporte.repository;

import com.tramigo.soporte.model.Soporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoporteRepository extends JpaRepository<Soporte, Integer> {
    List<Soporte> findAllByOrderByFechaCreacionDesc();
    List<Soporte> findByEstadoOrderByFechaCreacionDesc(String estado);
    List<Soporte> findByCorreoIgnoreCaseOrderByFechaCreacionDesc(String correo);
}

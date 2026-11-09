package com.tramigo.repository;

import com.tramigo.model.Soporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoporteRepository extends JpaRepository<Soporte, Integer> {
    List<Soporte> findAllByOrderByFechaCreacionDesc();
}

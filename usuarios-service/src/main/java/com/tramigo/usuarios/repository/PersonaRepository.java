package com.tramigo.usuarios.repository;

import com.tramigo.usuarios.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Integer> {

    List<Persona> findAllByOrderByIdAsc();

    @Query("SELECT p FROM Persona p WHERE p.id = :idNum OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY p.id ASC")
    List<Persona> searchByIdOrNombre(@Param("idNum") Integer idNum, @Param("query") String query);

    @Query("SELECT p FROM Persona p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY p.id ASC")
    List<Persona> searchByNombre(@Param("query") String query);
}

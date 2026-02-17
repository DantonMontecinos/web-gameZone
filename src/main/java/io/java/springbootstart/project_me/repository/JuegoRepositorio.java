package io.java.springbootstart.project_me.repository;

import io.java.springbootstart.project_me.modelo.Juego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JuegoRepositorio extends JpaRepository<Juego, Long> {

    Optional<Juego> findByNombre(String nombre);

}
package io.java.springbootstart.project_me.repository;

import io.java.springbootstart.project_me.modelo.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JugadorRepositorio extends JpaRepository<Jugador, Long> {

    /**
     * Buscar jugador por email
     */
    Optional<Jugador> findByEmail(String email);

    /**
     * Buscar jugador por usuario
     */
    Optional<Jugador> findByUsuario(String usuario);

    /**
     * Verificar si existe jugador con email
     */
    boolean existsByEmail(String email);

    /**
     * Verificar si existe jugador con usuario
     */
    boolean existsByUsuario(String usuario);

    /**
     * Obtener jugadores activos solamente
     */
    List<Jugador> findByActivoTrue();

    /**
     * Buscar jugadores por nombre o apellido (útil para búsquedas)
     */
    @Query("SELECT j FROM Jugador j WHERE " +
            "(LOWER(j.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(j.apellido) LIKE LOWER(CONCAT('%', :termino, '%'))) AND " +
            "j.activo = true")
    List<Jugador> findByNombreOrApellidoContainingIgnoreCase(@Param("termino") String termino);

    /**
     * Contar jugadores activos
     */
    long countByActivoTrue();
}
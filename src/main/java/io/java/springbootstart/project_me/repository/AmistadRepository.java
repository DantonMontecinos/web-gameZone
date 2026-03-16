package io.java.springbootstart.project_me.repository;

import io.java.springbootstart.project_me.modelo.Amistad;
import io.java.springbootstart.project_me.modelo.EstadoAmistad;
import io.java.springbootstart.project_me.modelo.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Amistad.
 *
 * Las queries buscan relaciones bidireccionales: un usuario puede ser
 * solicitante O receptor en una amistad aceptada.
 */
@Repository
public interface AmistadRepository extends JpaRepository<Amistad, Long> {

    /**
     * Obtener todas las amistades ACEPTADAS de un jugador.
     * Busca en ambas direcciones (como solicitante o como receptor).
     */
    @Query("SELECT a FROM Amistad a WHERE " +
           "(a.solicitante = :jugador OR a.receptor = :jugador) " +
           "AND a.estado = :estado")
    List<Amistad> findByJugadorAndEstado(@Param("jugador") Jugador jugador,
                                          @Param("estado") EstadoAmistad estado);

    /**
     * Obtener solicitudes pendientes RECIBIDAS por un jugador.
     */
    List<Amistad> findByReceptorAndEstado(Jugador receptor, EstadoAmistad estado);

    /**
     * Verificar si ya existe una amistad (en cualquier dirección) entre dos jugadores.
     * Esto evita solicitudes duplicadas.
     */
    @Query("SELECT a FROM Amistad a WHERE " +
           "((a.solicitante = :jugador1 AND a.receptor = :jugador2) OR " +
           " (a.solicitante = :jugador2 AND a.receptor = :jugador1))")
    Optional<Amistad> findAmistadEntreJugadores(@Param("jugador1") Jugador jugador1,
                                                 @Param("jugador2") Jugador jugador2);
}

package io.java.springbootstart.project_me.service;

import io.java.springbootstart.project_me.modelo.Amistad;
import io.java.springbootstart.project_me.modelo.EstadoAmistad;
import io.java.springbootstart.project_me.modelo.Jugador;
import io.java.springbootstart.project_me.repository.AmistadRepository;
import io.java.springbootstart.project_me.repository.JugadorRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar las relaciones de amistad entre jugadores.
 *
 * Flujo completo:
 * 1. Usuario A envía solicitud a Usuario B → enviarSolicitud()
 * 2. Usuario B ve sus solicitudes pendientes → obtenerSolicitudesPendientes()
 * 3. Usuario B acepta o rechaza → aceptarSolicitud() / rechazarSolicitud()
 * 4. Si fue aceptada, ambos aparecen en la lista de amigos del otro → obtenerAmigos()
 *
 * Validaciones:
 * - No se puede enviar solicitud a uno mismo
 * - No se pueden enviar solicitudes duplicadas
 * - Solo el receptor puede aceptar/rechazar una solicitud
 */
@Service
@Transactional
public class AmistadService {

    private final AmistadRepository amistadRepository;
    private final JugadorRepositorio jugadorRepositorio;

    public AmistadService(AmistadRepository amistadRepository,
                          JugadorRepositorio jugadorRepositorio) {
        this.amistadRepository = amistadRepository;
        this.jugadorRepositorio = jugadorRepositorio;
    }

    /**
     * Envía una solicitud de amistad desde el usuario autenticado a otro jugador.
     *
     * @param emailSolicitante Email del usuario que envía la solicitud (obtenido de Spring Security)
     * @param idReceptor       ID del jugador que recibirá la solicitud
     * @return La amistad creada con estado PENDIENTE
     * @throws IllegalArgumentException si la solicitud no es válida
     */
    public Amistad enviarSolicitud(String emailSolicitante, Long idReceptor) {
        // Buscar al solicitante por su email
        Jugador solicitante = jugadorRepositorio.findByEmail(emailSolicitante)
                .orElseThrow(() -> new IllegalArgumentException("Solicitante no encontrado"));

        // Buscar al receptor por su ID
        Jugador receptor = jugadorRepositorio.findById(idReceptor)
                .orElseThrow(() -> new IllegalArgumentException("Jugador receptor no encontrado"));

        // Validar que no se envíe solicitud a sí mismo
        if (solicitante.getId().equals(receptor.getId())) {
            throw new IllegalArgumentException("No puedes enviarte una solicitud a ti mismo");
        }

        // Verificar que no exista ya una amistad entre ambos
        Optional<Amistad> existente = amistadRepository.findAmistadEntreJugadores(solicitante, receptor);
        if (existente.isPresent()) {
            EstadoAmistad estadoActual = existente.get().getEstado();
            if (estadoActual == EstadoAmistad.PENDIENTE) {
                throw new IllegalArgumentException("Ya existe una solicitud pendiente entre estos usuarios");
            } else if (estadoActual == EstadoAmistad.ACEPTADA) {
                throw new IllegalArgumentException("Ya son amigos");
            }
            // Si fue RECHAZADA, permitimos crear una nueva solicitud eliminando la anterior
            amistadRepository.delete(existente.get());
        }

        // Crear la nueva solicitud de amistad
        Amistad amistad = new Amistad();
        amistad.setSolicitante(solicitante);
        amistad.setReceptor(receptor);
        amistad.setEstado(EstadoAmistad.PENDIENTE);

        return amistadRepository.save(amistad);
    }

    /**
     * Acepta una solicitud de amistad pendiente.
     * Solo el receptor de la solicitud puede aceptarla.
     *
     * @param amistadId    ID de la amistad a aceptar
     * @param emailUsuario Email del usuario que está aceptando (debe ser el receptor)
     * @return La amistad actualizada con estado ACEPTADA
     */
    public Amistad aceptarSolicitud(Long amistadId, String emailUsuario) {
        Amistad amistad = amistadRepository.findById(amistadId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        // Verificar que quien acepta es el receptor
        if (!amistad.getReceptor().getEmail().equals(emailUsuario)) {
            throw new IllegalArgumentException("Solo el receptor puede aceptar la solicitud");
        }

        // Verificar que la solicitud está pendiente
        if (amistad.getEstado() != EstadoAmistad.PENDIENTE) {
            throw new IllegalArgumentException("Esta solicitud ya fue respondida");
        }

        amistad.setEstado(EstadoAmistad.ACEPTADA);
        amistad.setFechaRespuesta(LocalDateTime.now());

        return amistadRepository.save(amistad);
    }

    /**
     * Rechaza una solicitud de amistad pendiente.
     * Solo el receptor de la solicitud puede rechazarla.
     *
     * @param amistadId    ID de la amistad a rechazar
     * @param emailUsuario Email del usuario que está rechazando (debe ser el receptor)
     * @return La amistad actualizada con estado RECHAZADA
     */
    public Amistad rechazarSolicitud(Long amistadId, String emailUsuario) {
        Amistad amistad = amistadRepository.findById(amistadId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        // Verificar que quien rechaza es el receptor
        if (!amistad.getReceptor().getEmail().equals(emailUsuario)) {
            throw new IllegalArgumentException("Solo el receptor puede rechazar la solicitud");
        }

        // Verificar que la solicitud está pendiente
        if (amistad.getEstado() != EstadoAmistad.PENDIENTE) {
            throw new IllegalArgumentException("Esta solicitud ya fue respondida");
        }

        amistad.setEstado(EstadoAmistad.RECHAZADA);
        amistad.setFechaRespuesta(LocalDateTime.now());

        return amistadRepository.save(amistad);
    }

    /**
     * Obtiene la lista de amigos (amistades aceptadas) de un usuario.
     * Extrae al "otro" jugador de cada relación de amistad.
     *
     * @param email Email del usuario autenticado
     * @return Lista de jugadores que son amigos del usuario
     */
    @Transactional(readOnly = true)
    public List<Jugador> obtenerAmigos(String email) {
        Jugador jugador = jugadorRepositorio.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado"));

        List<Amistad> amistades = amistadRepository.findByJugadorAndEstado(jugador, EstadoAmistad.ACEPTADA);

        // Extraer al "amigo" de cada relación (el otro jugador que no soy yo)
        List<Jugador> amigos = new ArrayList<>();
        for (Amistad amistad : amistades) {
            if (amistad.getSolicitante().getId().equals(jugador.getId())) {
                amigos.add(amistad.getReceptor());
            } else {
                amigos.add(amistad.getSolicitante());
            }
        }

        return amigos;
    }

    /**
     * Obtiene las solicitudes de amistad pendientes recibidas por el usuario.
     *
     * @param email Email del usuario autenticado
     * @return Lista de amistades con estado PENDIENTE donde el usuario es receptor
     */
    @Transactional(readOnly = true)
    public List<Amistad> obtenerSolicitudesPendientes(String email) {
        Jugador jugador = jugadorRepositorio.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado"));

        return amistadRepository.findByReceptorAndEstado(jugador, EstadoAmistad.PENDIENTE);
    }

    /**
     * Verifica si dos usuarios son amigos (tienen una amistad ACEPTADA).
     *
     * @param email1 Email del primer usuario
     * @param email2 Email del segundo usuario
     * @return true si son amigos
     */
    @Transactional(readOnly = true)
    public boolean sonAmigos(String email1, String email2) {
        Jugador jugador1 = jugadorRepositorio.findByEmail(email1).orElse(null);
        Jugador jugador2 = jugadorRepositorio.findByEmail(email2).orElse(null);

        if (jugador1 == null || jugador2 == null) {
            return false;
        }

        Optional<Amistad> amistad = amistadRepository.findAmistadEntreJugadores(jugador1, jugador2);
        return amistad.isPresent() && amistad.get().getEstado() == EstadoAmistad.ACEPTADA;
    }

    /**
     * Obtiene el estado de la relación entre el usuario autenticado y otro jugador.
     * Útil para mostrar el botón correcto en la vista de perfil.
     *
     * @param email      Email del usuario autenticado
     * @param jugadorId  ID del otro jugador
     * @return Optional con la amistad si existe, vacío si no hay relación
     */
    @Transactional(readOnly = true)
    public Optional<Amistad> obtenerEstadoRelacion(String email, Long jugadorId) {
        Jugador yo = jugadorRepositorio.findByEmail(email).orElse(null);
        Jugador otro = jugadorRepositorio.findById(jugadorId).orElse(null);

        if (yo == null || otro == null) {
            return Optional.empty();
        }

        return amistadRepository.findAmistadEntreJugadores(yo, otro);
    }
}

package io.java.springbootstart.project_me.service;


import io.java.springbootstart.project_me.dto.JugadorDTO;
import io.java.springbootstart.project_me.modelo.Juego;
import io.java.springbootstart.project_me.modelo.Jugador;
import io.java.springbootstart.project_me.repository.JuegoRepositorio;
import io.java.springbootstart.project_me.repository.JugadorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JugadorService {

    private final JugadorRepositorio jugadorRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final JuegoRepositorio juegoRepositorio;

    

    @Autowired
    public JugadorService(JugadorRepositorio jugadorRepositorio, PasswordEncoder passwordEncoder, JuegoRepositorio juegoRepositorio) {
        this.jugadorRepositorio = jugadorRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.juegoRepositorio = juegoRepositorio;
    }

    public Jugador buscarPorEmail(String email) {
        return jugadorRepositorio.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));
    }

    /**
     * Obtener todos los jugadores activos
     */
    @Transactional(readOnly = true)
    public List<Jugador> obtenerTodosLosJugadores() {
        return jugadorRepositorio.findByActivoTrue();
    }

    /**
     * Buscar jugador por ID
     */
    @Transactional(readOnly = true)
    public Optional<Jugador> obtenerJugadorPorId(Long id) {
        return jugadorRepositorio.findById(id);
    }

    /**
     * Buscar jugador por email
     */
    @Transactional(readOnly = true)
    public Optional<Jugador> obtenerJugadorPorEmail(String email) {
        return jugadorRepositorio.findByEmail(email);
    }

    /**
     * Verificar si existe un jugador con ese email
     */
    @Transactional(readOnly = true)
    public boolean existeJugadorConEmail(String email) {
        return jugadorRepositorio.existsByEmail(email);
    }

    /**
     * Verificar si existe un jugador con ese usuario
     */
    @Transactional(readOnly = true)
    public boolean existeJugadorConUsuario(String usuario) {
        return jugadorRepositorio.existsByUsuario(usuario);
    }

    /**
     * Crear nuevo jugador
     */
    public Jugador crearJugador(JugadorDTO jugadorDTO) {
        // Validar que no exista el email
        if (existeJugadorConEmail(jugadorDTO.getEmail())) {
            throw new IllegalArgumentException("Ya existe un jugador con ese email");
        }

        // Validar que no exista el usuario
        if (existeJugadorConUsuario(jugadorDTO.getUsuario())) {
            throw new IllegalArgumentException("Ya existe un jugador con ese nombre de usuario");
        }



        Jugador jugador = new Jugador();
        jugador.setNombre(jugadorDTO.getNombre());
        jugador.setApellido(jugadorDTO.getApellido());
        jugador.setEmail(jugadorDTO.getEmail());
        jugador.setUsuario(jugadorDTO.getUsuario());
        jugador.setPassword(passwordEncoder.encode(jugadorDTO.getPassword()));
        jugador.setFechaNacimiento(jugadorDTO.getFechaNacimiento());
        jugador.setDescripcion(jugadorDTO.getDescripcion());

        Juego juego = juegoRepositorio.findById(jugadorDTO.getJuegoId())
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado"));

        jugador.setJuego(juego);



        return jugadorRepositorio.save(jugador);
    }

    /**
     * Actualizar jugador existente
     */
    public Jugador actualizarJugador(Long id, JugadorDTO jugadorDTO) {
        Jugador jugadorExistente = jugadorRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado con ID: " + id));

        // Verificar email solo si es diferente al actual
        if (!jugadorExistente.getEmail().equals(jugadorDTO.getEmail()) &&
                existeJugadorConEmail(jugadorDTO.getEmail())) {
            throw new IllegalArgumentException("Ya existe un jugador con ese email");
        }

        // Verificar usuario solo si es diferente al actual
        if (!jugadorExistente.getUsuario().equals(jugadorDTO.getUsuario()) &&
                existeJugadorConUsuario(jugadorDTO.getUsuario())) {
            throw new IllegalArgumentException("Ya existe un jugador con ese nombre de usuario");
        }

        // Actualizar campos
        jugadorExistente.setNombre(jugadorDTO.getNombre());
        jugadorExistente.setApellido(jugadorDTO.getApellido());
        jugadorExistente.setEmail(jugadorDTO.getEmail());
        jugadorExistente.setUsuario(jugadorDTO.getUsuario());
        jugadorExistente.setFechaNacimiento(jugadorDTO.getFechaNacimiento());
        jugadorExistente.setDescripcion(jugadorDTO.getDescripcion());

        // Solo actualizar contraseña si se proporciona una nueva
        if (jugadorDTO.getPassword() != null && !jugadorDTO.getPassword().isEmpty()) {
            jugadorExistente.setPassword(passwordEncoder.encode(jugadorDTO.getPassword()));
        }

        return jugadorRepositorio.save(jugadorExistente);
    }

    /**
     * Desactivar jugador (borrado lógico)
     */
    public void desactivarJugador(Long id) {
        Jugador jugador = jugadorRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado con ID: " + id));

        jugador.setActivo(false);
        jugadorRepositorio.save(jugador);
    }

    /**
     * Eliminar jugador permanentemente (usar con cuidado)
     */
    public void eliminarJugadorPermanentemente(Long id) {
        if (!jugadorRepositorio.existsById(id)) {
            throw new IllegalArgumentException("Jugador no encontrado con ID: " + id);
        }
        jugadorRepositorio.deleteById(id);
    }

    /**
     * Convertir Jugador a JugadorDTO (sin contraseña)
     */
    public JugadorDTO convertirADto(Jugador jugador) {
        JugadorDTO dto = new JugadorDTO();
        dto.setNombre(jugador.getNombre());
        dto.setApellido(jugador.getApellido());
        dto.setEmail(jugador.getEmail());
        dto.setUsuario(jugador.getUsuario());
        dto.setFechaNacimiento(jugador.getFechaNacimiento());
        dto.setDescripcion(jugador.getDescripcion());
        return dto;
    }
}
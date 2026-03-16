package io.java.springbootstart.project_me.controller;

import io.java.springbootstart.project_me.modelo.Amistad;
import io.java.springbootstart.project_me.modelo.Jugador;
import io.java.springbootstart.project_me.service.AmistadService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar las relaciones de amistad.
 *
 * Todos los endpoints requieren autenticación (cubierto por SecurityConfig).
 * El email del usuario autenticado se obtiene del objeto Authentication de Spring Security.
 *
 * Endpoints:
 * - POST /amigos/solicitar/{jugadorId}  → Enviar solicitud de amistad
 * - POST /amigos/aceptar/{amistadId}    → Aceptar solicitud pendiente
 * - POST /amigos/rechazar/{amistadId}   → Rechazar solicitud pendiente
 * - GET  /amigos/lista                  → Obtener lista de amigos (JSON)
 * - GET  /amigos/pendientes             → Obtener solicitudes pendientes (JSON)
 */
@RestController
@RequestMapping("/amigos")
public class AmistadController {

    private final AmistadService amistadService;

    public AmistadController(AmistadService amistadService) {
        this.amistadService = amistadService;
    }

    /**
     * Enviar solicitud de amistad a otro jugador.
     *
     * @param jugadorId ID del jugador al que se envía la solicitud
     * @param authentication Objeto de autenticación de Spring Security
     * @return Respuesta con el estado de la operación
     */
    @PostMapping("/solicitar/{jugadorId}")
    public ResponseEntity<Map<String, Object>> enviarSolicitud(
            @PathVariable Long jugadorId,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            String email = authentication.getName();
            amistadService.enviarSolicitud(email, jugadorId);
            response.put("exito", true);
            response.put("mensaje", "Solicitud de amistad enviada");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("exito", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Aceptar una solicitud de amistad pendiente.
     *
     * @param amistadId ID de la amistad a aceptar
     * @param authentication Objeto de autenticación de Spring Security
     * @return Respuesta con el estado de la operación
     */
    @PostMapping("/aceptar/{amistadId}")
    public ResponseEntity<Map<String, Object>> aceptarSolicitud(
            @PathVariable Long amistadId,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            String email = authentication.getName();
            amistadService.aceptarSolicitud(amistadId, email);
            response.put("exito", true);
            response.put("mensaje", "Solicitud aceptada. ¡Ahora son amigos!");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("exito", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Rechazar una solicitud de amistad pendiente.
     *
     * @param amistadId ID de la amistad a rechazar
     * @param authentication Objeto de autenticación de Spring Security
     * @return Respuesta con el estado de la operación
     */
    @PostMapping("/rechazar/{amistadId}")
    public ResponseEntity<Map<String, Object>> rechazarSolicitud(
            @PathVariable Long amistadId,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            String email = authentication.getName();
            amistadService.rechazarSolicitud(amistadId, email);
            response.put("exito", true);
            response.put("mensaje", "Solicitud rechazada");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("exito", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtener la lista de amigos del usuario autenticado.
     * Retorna datos simplificados (id, nombre, apellido, avatar, email) como JSON.
     *
     * @param authentication Objeto de autenticación de Spring Security
     * @return Lista de amigos en formato JSON
     */
    @GetMapping("/lista")
    public ResponseEntity<List<Map<String, Object>>> obtenerAmigos(Authentication authentication) {
        String email = authentication.getName();
        List<Jugador> amigos = amistadService.obtenerAmigos(email);

        // Mapear a una estructura simplificada (no exponer todos los campos del modelo)
        List<Map<String, Object>> resultado = amigos.stream().map(amigo -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", amigo.getId());
            map.put("nombre", amigo.getNombre());
            map.put("apellido", amigo.getApellido());
            map.put("nombreCompleto", amigo.getNombreCompleto());
            map.put("avatar", amigo.getAvatar());
            map.put("email", amigo.getEmail());
            map.put("usuario", amigo.getUsuario());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtener las solicitudes de amistad pendientes del usuario autenticado.
     *
     * @param authentication Objeto de autenticación de Spring Security
     * @return Lista de solicitudes pendientes en formato JSON
     */
    @GetMapping("/pendientes")
    public ResponseEntity<List<Map<String, Object>>> obtenerPendientes(Authentication authentication) {
        String email = authentication.getName();
        List<Amistad> pendientes = amistadService.obtenerSolicitudesPendientes(email);

        List<Map<String, Object>> resultado = pendientes.stream().map(amistad -> {
            Map<String, Object> map = new HashMap<>();
            map.put("amistadId", amistad.getId());
            map.put("solicitanteId", amistad.getSolicitante().getId());
            map.put("solicitanteNombre", amistad.getSolicitante().getNombreCompleto());
            map.put("solicitanteAvatar", amistad.getSolicitante().getAvatar());
            map.put("solicitanteUsuario", amistad.getSolicitante().getUsuario());
            map.put("fechaSolicitud", amistad.getFechaSolicitud().toString());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }
}

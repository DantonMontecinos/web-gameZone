package io.java.springbootstart.project_me.controller;

import io.java.springbootstart.project_me.modelo.ChatMessage;
import io.java.springbootstart.project_me.modelo.Jugador;
import io.java.springbootstart.project_me.service.AmistadService;
import io.java.springbootstart.project_me.service.ChatService;
import io.java.springbootstart.project_me.service.JugadorService;
import io.java.springbootstart.project_me.service.PresenceService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

/**
 * Controlador principal del chat.
 *
 * Combina tres responsabilidades:
 * 1. Servir la vista Thymeleaf del chat (GET /chat)
 * 2. Recibir mensajes vía WebSocket STOMP (@MessageMapping)
 * 3. Proveer el historial de mensajes vía REST (GET /messages/{user1}/{user2})
 *
 * Flujo de un mensaje:
 * ┌────────────┐  STOMP /app/chat.send  ┌────────────────┐  convertAndSendToUser  ┌────────────┐
 * │  Sender    │ ─────────────────────► │  ChatController │ ────────────────────► │  Receiver  │
 * │  (Browser) │                        │  → ChatService  │                       │  (Browser) │
 * └────────────┘                        │  → PostgreSQL   │                       └────────────┘
 *                                       └────────────────┘
 */
@Controller
public class ChatController {

    private final ChatService chatService;
    private final AmistadService amistadService;
    private final JugadorService jugadorService;
    private final PresenceService presenceService;

    public ChatController(ChatService chatService,
                          AmistadService amistadService,
                          JugadorService jugadorService,
                          PresenceService presenceService) {
        this.chatService = chatService;
        this.amistadService = amistadService;
        this.jugadorService = jugadorService;
        this.presenceService = presenceService;
    }

    /**
     * Sirve la página principal del chat.
     * Inyecta en el modelo:
     * - Lista de amigos del usuario autenticado
     * - Email del usuario actual (para el cliente JavaScript)
     * - Set de usuarios actualmente online
     *
     * @param model          Modelo de Thymeleaf
     * @param authentication Objeto de autenticación de Spring Security
     * @return Vista "chat/chat"
     */
    @GetMapping("/chat")
    public String mostrarChat(Model model, Authentication authentication) {
        String email = authentication.getName();

        // Obtener los amigos del usuario autenticado
        List<Jugador> amigos = amistadService.obtenerAmigos(email);

        // Obtener el jugador actual para mostrar su nombre
        Jugador jugadorActual = jugadorService.buscarPorEmail(email);

        // Obtener los usuarios actualmente online
        Set<String> onlineUsers = presenceService.getOnlineUsers();

        model.addAttribute("amigos", amigos);
        model.addAttribute("currentUserEmail", email);
        model.addAttribute("currentUserName", jugadorActual.getNombreCompleto());
        model.addAttribute("onlineUsers", onlineUsers);

        return "chat/chat";
    }

    /**
     * Recibe un mensaje de chat vía WebSocket STOMP.
     *
     * El cliente envía el mensaje a: /app/chat.send
     * El @MessageMapping recorta el prefijo /app, escuchando en "chat.send".
     *
     * El Principal se obtiene automáticamente de la sesión WebSocket
     * (proporcionado por Spring Security durante el handshake).
     *
     * @param message   Payload del mensaje (deserializado de JSON)
     * @param principal Usuario autenticado (su getName() retorna el email)
     */
    @MessageMapping("/chat.send")
    public void enviarMensaje(@Payload ChatMessage message, Principal principal) {
        String senderEmail = principal.getName();

        try {
            chatService.procesarMensaje(message, senderEmail);
        } catch (IllegalArgumentException e) {
            // En caso de error (ej: no son amigos), el mensaje simplemente no se envía.
            // Se podría enviar un mensaje de error al sender si se quisiera.
            System.err.println("Error al procesar mensaje: " + e.getMessage());
        }
    }

    /**
     * Endpoint REST para obtener el historial de mensajes entre dos usuarios.
     * Se usa al abrir un chat popup para cargar la conversación existente.
     *
     * @param user1 Email del primer usuario
     * @param user2 Email del segundo usuario
     * @return Lista de mensajes en orden cronológico (JSON)
     */
    @GetMapping("/messages/{user1}/{user2}")
    @ResponseBody
    public List<ChatMessage> obtenerHistorial(@PathVariable String user1,
                                              @PathVariable String user2) {
        return chatService.obtenerHistorial(user1, user2);
    }

    /**
     * Endpoint REST para obtener los usuarios actualmente online.
     * Útil para obtener el estado inicial al cargar la página.
     *
     * @return Set de emails de usuarios online (JSON)
     */
    @GetMapping("/chat/online-users")
    @ResponseBody
    public Set<String> obtenerUsuariosOnline() {
        return presenceService.getOnlineUsers();
    }
}

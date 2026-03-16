package io.java.springbootstart.project_me.config;

import io.java.springbootstart.project_me.service.PresenceService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

/**
 * Listener de eventos del ciclo de vida de las sesiones WebSocket.
 *
 * Escucha dos eventos clave:
 * 1. SessionConnectEvent  → cuando un usuario establece conexión WebSocket
 * 2. SessionDisconnectEvent → cuando un usuario cierra la conexión (cierra pestaña, pierde red, etc.)
 *
 * Estos eventos se delegan al PresenceService para actualizar el estado
 * online/offline y notificar a todos los clientes conectados.
 */
@Component
public class WebSocketEventListener {

    private final PresenceService presenceService;

    public WebSocketEventListener(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    /**
     * Se ejecuta cuando un usuario se conecta al WebSocket.
     * Extrae el email del Principal (configurado por Spring Security)
     * y lo registra como ONLINE en el PresenceService.
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();

        if (user != null) {
            String email = user.getName();
            presenceService.userConnected(email);
            System.out.println(">>> Usuario conectado al WebSocket: " + email);
        }
    }

    /**
     * Se ejecuta cuando un usuario se desconecta del WebSocket.
     * Extrae el email del Principal y lo registra como OFFLINE.
     *
     * Nota: este evento se dispara tanto cuando el usuario cierra el navegador
     * como cuando pierde la conexión de red.
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();

        if (user != null) {
            String email = user.getName();
            presenceService.userDisconnected(email);
            System.out.println("<<< Usuario desconectado del WebSocket: " + email);
        }
    }
}

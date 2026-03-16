package io.java.springbootstart.project_me.service;

import io.java.springbootstart.project_me.modelo.UserStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio de presencia de usuarios (online/offline).
 *
 * Mantiene un mapa en memoria (ConcurrentHashMap) de usuarios conectados al WebSocket.
 * Cuando un usuario se conecta o desconecta, envía una notificación broadcast
 * a todos los clientes suscritos al topic "/topic/presence".
 *
 * Nota: Este servicio utiliza almacenamiento en memoria, lo cual es adecuado
 * para una instancia única del servidor. Para un sistema distribuido (múltiples
 * instancias), sería necesario usar Redis o similar como store compartido.
 */
@Service
public class PresenceService {

    /** Mapa thread-safe que almacena los estados de usuarios conectados (email → UserStatus) */
    private final ConcurrentHashMap<String, UserStatus> onlineUsers = new ConcurrentHashMap<>();

    /** Template para enviar mensajes STOMP a los clientes */
    private final SimpMessagingTemplate messagingTemplate;

    public PresenceService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Registra un usuario como ONLINE.
     * Se llama desde WebSocketEventListener cuando se establece una conexión.
     *
     * @param email Email del usuario que se conecta
     */
    public void userConnected(String email) {
        UserStatus status = new UserStatus(email, UserStatus.Status.ONLINE);
        onlineUsers.put(email, status);

        // Notificar a TODOS los clientes que este usuario está online
        messagingTemplate.convertAndSend("/topic/presence", status);
    }

    /**
     * Registra un usuario como OFFLINE.
     * Se llama desde WebSocketEventListener cuando se cierra una conexión.
     *
     * @param email Email del usuario que se desconecta
     */
    public void userDisconnected(String email) {
        UserStatus status = new UserStatus(email, UserStatus.Status.OFFLINE);
        status.setLastSeen(LocalDateTime.now());
        onlineUsers.remove(email);

        // Notificar a TODOS los clientes que este usuario está offline
        messagingTemplate.convertAndSend("/topic/presence", status);
    }

    /**
     * Verifica si un usuario específico está actualmente online.
     *
     * @param email Email del usuario a verificar
     * @return true si el usuario está conectado al WebSocket
     */
    public boolean isUserOnline(String email) {
        return onlineUsers.containsKey(email);
    }

    /**
     * Obtiene el conjunto de emails de todos los usuarios actualmente online.
     * Retorna una vista no modificable para evitar modificaciones externas.
     *
     * @return Set inmutable de emails de usuarios online
     */
    public Set<String> getOnlineUsers() {
        return Collections.unmodifiableSet(onlineUsers.keySet());
    }
}

package io.java.springbootstart.project_me.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración de WebSocket con STOMP para el sistema de chat en tiempo real.
 *
 * Arquitectura:
 * ┌─────────────┐       STOMP/SockJS        ┌──────────────────────┐
 * │   Browser   │ ◄──────────────────────────► Endpoint: /chat      │
 * │  (SockJS +  │                            │                      │
 * │   STOMP.js) │   /app/chat.send           │  @MessageMapping     │
 * │             │ ────────────────────────────►  (ChatController)    │
 * │             │                            │                      │
 * │             │   /user/{email}/queue/msgs  │  SimpMessaging-      │
 * │             │ ◄────────────────────────── │  Template            │
 * │             │                            │                      │
 * │             │   /topic/presence           │  Broadcast de        │
 * │             │ ◄────────────────────────── │  presencia           │
 * └─────────────┘                            └──────────────────────┘
 *
 * - /queue → destinos privados (mensajes directos entre usuarios)
 * - /topic → destinos públicos (estado de presencia broadcast a todos)
 * - /app   → prefijo para los @MessageMapping del servidor
 * - /chat  → endpoint SockJS donde el cliente establece la conexión
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configura el message broker en memoria.
     *
     * - enableSimpleBroker("/queue", "/topic"):
     *   Habilita un broker simple que enruta mensajes a suscriptores en esos destinos.
     *   /queue → mensajes privados (usados con convertAndSendToUser)
     *   /topic → broadcast (presencia online/offline)
     *
     * - setApplicationDestinationPrefixes("/app"):
     *   Los mensajes enviados a /app/* serán procesados por @MessageMapping en los controladores.
     *
     * - setUserDestinationPrefix("/user"):
     *   Spring traduce /user/{email}/queue/messages a una cola individual por usuario.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue", "/topic");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Registra el endpoint STOMP donde los clientes se conectan.
     *
     * - addEndpoint("/chat"): URL de conexión WebSocket.
     * - withSockJS(): habilita SockJS como fallback para navegadores
     *   que no soportan WebSocket nativo (genera URLs como /chat/info, /chat/websocket, etc.)
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")
                .withSockJS();
    }
}

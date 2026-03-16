package io.java.springbootstart.project_me.service;

import io.java.springbootstart.project_me.modelo.ChatMessage;
import io.java.springbootstart.project_me.repository.ChatMessageRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de chat que gestiona el procesamiento, validación,
 * persistencia y envío de mensajes privados entre usuarios.
 *
 * Flujo de un mensaje:
 * 1. El ChatController recibe el mensaje vía @MessageMapping
 * 2. ChatService valida que sender y receiver sean amigos
 * 3. ChatService persiste el mensaje en PostgreSQL
 * 4. ChatService envía el mensaje al destinatario vía SimpMessagingTemplate
 *
 * El destinatario recibe el mensaje en /user/{email}/queue/messages
 * gracias a convertAndSendToUser() de Spring.
 */
@Service
@Transactional
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final AmistadService amistadService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(ChatMessageRepository chatMessageRepository,
                       AmistadService amistadService,
                       SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.amistadService = amistadService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Procesa un mensaje de chat: valida, persiste y envía al destinatario.
     *
     * @param message     El mensaje recibido del cliente (con receiverId y content)
     * @param senderEmail Email del remitente (obtenido del Principal de Spring Security)
     * @return El mensaje guardado con timestamp y senderId establecidos
     * @throws IllegalArgumentException si los usuarios no son amigos
     */
    public ChatMessage procesarMensaje(ChatMessage message, String senderEmail) {
        // Establecer el sender con el email autenticado (seguridad: no confiar en datos del cliente)
        message.setSenderId(senderEmail);
        message.setTimestamp(LocalDateTime.now());

        // Validar que el contenido no esté vacío
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje no puede estar vacío");
        }

        // Validar que sender y receiver sean amigos
        if (!amistadService.sonAmigos(senderEmail, message.getReceiverId())) {
            throw new IllegalArgumentException("Solo puedes enviar mensajes a tus amigos");
        }

        // Persistir el mensaje en la base de datos
        ChatMessage savedMessage = chatMessageRepository.save(message);

        // Enviar el mensaje al destinatario en tiempo real
        // Spring traduce esto a: /user/{receiverEmail}/queue/messages
        messagingTemplate.convertAndSendToUser(
                message.getReceiverId(),    // email del destinatario
                "/queue/messages",           // destino (Spring añade el prefijo /user/{email})
                savedMessage                 // payload (se serializa a JSON automáticamente)
        );

        return savedMessage;
    }

    /**
     * Obtiene el historial de mensajes entre dos usuarios.
     * Retorna todos los mensajes en orden cronológico ascendente.
     *
     * @param email1 Email del primer usuario
     * @param email2 Email del segundo usuario
     * @return Lista de mensajes ordenados por timestamp
     */
    @Transactional(readOnly = true)
    public List<ChatMessage> obtenerHistorial(String email1, String email2) {
        return chatMessageRepository.findConversation(email1, email2);
    }
}

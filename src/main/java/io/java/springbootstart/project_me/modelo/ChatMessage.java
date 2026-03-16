package io.java.springbootstart.project_me.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un mensaje de chat privado entre dos usuarios.
 *
 * Cada mensaje tiene un remitente (senderId) y un destinatario (receiverId),
 * ambos identificados por su email (que es el identificador de Spring Security).
 * El contenido se almacena como TEXT para permitir mensajes largos.
 * El timestamp se establece automáticamente al momento de la creación.
 */
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Email del usuario que envía el mensaje */
    @Column(name = "sender_id", nullable = false, length = 100)
    private String senderId;

    /** Email del usuario que recibe el mensaje */
    @Column(name = "receiver_id", nullable = false, length = 100)
    private String receiverId;

    /** Contenido del mensaje */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** Fecha y hora de envío del mensaje */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    // ========================
    // Constructor vacío (JPA)
    // ========================
    public ChatMessage() {
    }

    // ========================
    // Constructor con parámetros
    // ========================
    public ChatMessage(String senderId, String receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    // ========================
    // Callback JPA: establece timestamp automáticamente
    // ========================
    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    // ========================
    // Getters y Setters
    // ========================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

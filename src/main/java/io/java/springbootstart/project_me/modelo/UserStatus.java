package io.java.springbootstart.project_me.modelo;

import java.time.LocalDateTime;

/**
 * POJO (no es entidad JPA) que representa el estado de conexión de un usuario.
 *
 * Se usa para:
 * - Notificar a los clientes cuando un usuario se conecta o desconecta del WebSocket.
 * - Mantener un registro en memoria de los usuarios actualmente online.
 *
 * Se envía como JSON a través del topic WebSocket /topic/presence.
 */
public class UserStatus {

    /** Enum interno que define los posibles estados de conexión */
    public enum Status {
        ONLINE,
        OFFLINE
    }

    /** Email del usuario (identidad de Spring Security) */
    private String username;

    /** Estado actual: ONLINE u OFFLINE */
    private Status status;

    /** Última vez que el usuario estuvo conectado */
    private LocalDateTime lastSeen;

    // ========================
    // Constructores
    // ========================
    public UserStatus() {
    }

    public UserStatus(String username, Status status) {
        this.username = username;
        this.status = status;
        this.lastSeen = LocalDateTime.now();
    }

    // ========================
    // Getters y Setters
    // ========================
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }
}

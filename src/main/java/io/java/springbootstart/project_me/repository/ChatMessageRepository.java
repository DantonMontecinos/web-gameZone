package io.java.springbootstart.project_me.repository;

import io.java.springbootstart.project_me.modelo.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad ChatMessage.
 *
 * Proporciona acceso al historial de conversaciones entre dos usuarios.
 * Los mensajes se identifican por el email del sender y receiver.
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Obtener el historial completo de mensajes entre dos usuarios.
     * Busca en ambas direcciones (A→B y B→A) y ordena por timestamp ascendente
     * para mostrar la conversación en orden cronológico.
     */
    @Query("SELECT m FROM ChatMessage m WHERE " +
           "(m.senderId = :user1 AND m.receiverId = :user2) OR " +
           "(m.senderId = :user2 AND m.receiverId = :user1) " +
           "ORDER BY m.timestamp ASC")
    List<ChatMessage> findConversation(@Param("user1") String user1,
                                       @Param("user2") String user2);
}

package io.java.springbootstart.project_me.modelo;

/**
 * Enum que representa los posibles estados de una solicitud de amistad.
 *
 * PENDIENTE  → La solicitud fue enviada pero aún no fue respondida.
 * ACEPTADA   → El receptor aceptó la solicitud; ambos usuarios son amigos.
 * RECHAZADA  → El receptor rechazó la solicitud.
 */
public enum EstadoAmistad {
    PENDIENTE,
    ACEPTADA,
    RECHAZADA
}

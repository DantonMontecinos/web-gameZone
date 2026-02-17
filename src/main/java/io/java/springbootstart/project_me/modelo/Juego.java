package io.java.springbootstart.project_me.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "juegos")
public class Juego {
    public Juego() {
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;



    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

}

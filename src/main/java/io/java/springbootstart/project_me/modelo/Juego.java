package io.java.springbootstart.project_me.modelo;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "juegos")
public class Juego {
    public Juego() {
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @OneToMany(mappedBy = "juego", fetch = FetchType.EAGER)
    private List<Jugador> jugadores;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

}

package io.java.springbootstart.project_me.service;

import io.java.springbootstart.project_me.modelo.Juego;
import io.java.springbootstart.project_me.repository.JuegoRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JuegoService {

    private final JuegoRepositorio juegoRepository;

    public JuegoService(JuegoRepositorio juegoRepository) {
        this.juegoRepository = juegoRepository;
    }

    public List<Juego> obtenerTodosLosJuegos() {
        return juegoRepository.findAll();
    }

}

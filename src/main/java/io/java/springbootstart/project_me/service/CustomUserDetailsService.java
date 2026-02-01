package io.java.springbootstart.project_me.service;

import io.java.springbootstart.project_me.entity.SecurityUser;
import io.java.springbootstart.project_me.modelo.Jugador;
import io.java.springbootstart.project_me.repository.JugadorRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final JugadorRepositorio jugadorRepositorio;

    public CustomUserDetailsService(JugadorRepositorio jugadorRepositorio) {
        this.jugadorRepositorio = jugadorRepositorio;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Jugador jugador = jugadorRepositorio.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No encontrado: " + email));

        return User.withUsername(jugador.getEmail())
                .password(jugador.getPassword()) // La contraseña ya debe estar encriptada
                .roles("USER") // Puedes definir roles si es necesario
                .build();
    }
}
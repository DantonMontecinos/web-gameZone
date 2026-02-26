package io.java.springbootstart.project_me.entity;

import io.java.springbootstart.project_me.modelo.Jugador;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
@NoArgsConstructor
public class SecurityUser implements UserDetails {

    private Jugador jugador;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(jugador.getEmail()));
    }

    @Override
    public String getPassword() {
        return jugador.getPassword();
    }

    @Override
    public String getUsername() {
        return jugador.getEmail();
    }


}

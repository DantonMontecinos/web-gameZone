package io.java.springbootstart.project_me.security;

import io.java.springbootstart.project_me.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                                // Permitir TODAS las rutas temporalmente mientras reestructuras
                                .requestMatchers("/**").permitAll()

                        // Si quieres mantener algunas rutas protegidas en el futuro,
                        // puedes comentar la línea anterior y descomentar estas:
                        /*
                        .requestMatchers("/", "/index", "/crear", "/jugadores/nuevo", "/jugadores/guardar",
                                "/inicioLogeoCrear", "/login", "/listadoGames", "/css/**", "/js/**",
                                "/images/**", "/webjars/**", "/error", "/jugadores/**",
                                "/games/**", "/comunidades/**", "/perfil/**", "/admin/**").permitAll()
                        .anyRequest().authenticated()
                        */
                )
                // Deshabilitar CSRF temporalmente para facilitar desarrollo
                .csrf(csrf -> csrf.disable())
                .userDetailsService(userDetailsService)
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/index", true)
                        .failureUrl("/login?error=true")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                );

        return http.build();
    }
}
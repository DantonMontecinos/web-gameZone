package io.java.springbootstart.project_me.controller;

import io.java.springbootstart.project_me.dto.JugadorDTO;
import io.java.springbootstart.project_me.modelo.Juego;
import io.java.springbootstart.project_me.modelo.Jugador;
import io.java.springbootstart.project_me.repository.JuegoRepositorio;
import io.java.springbootstart.project_me.service.JuegoService;
import io.java.springbootstart.project_me.service.JugadorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class AppController {

    /**
     * Página de inicio
     */
    private final JuegoRepositorio juegoRepositorio;
    private final JugadorService jugadorService;

    private final JuegoService juegoService;

    public AppController(JugadorService jugadorService, JuegoRepositorio juegoRepositorio, JuegoService juegoService) {
        this.jugadorService = jugadorService;
        this.juegoRepositorio = juegoRepositorio;
        this.juegoService = juegoService;
    }

    @GetMapping("/listadoGames")
    public String listadoJuegos(Model model) {

        List<Juego> juegos = juegoService.obtenerTodosLosJuegos();
        model.addAttribute("juegos", juegos);
        model.addAttribute("titulo", "Listado de Juegos Populares");
        return "admin/listadoGames";
    }



    @GetMapping({"/", "/index", "/login"})
    public String inicio(@RequestParam(value = "error", required = false) String error,
                         @RequestParam(value = "logout", required = false) String logout,
                         Model model) {
        // Si el usuario ya está autenticado, redirigir al dashboard
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }

        if (error != null) {
            model.addAttribute("mensajeError", "Credenciales inválidas. Por favor, intenta de nuevo.");
        }
        if (logout != null) {
            model.addAttribute("mensajeExito", "Has cerrado sesión exitosamente.");
        }

        model.addAttribute("mensaje", "¡Bienvenidos a VideoGamesHub!");
        return "admin/index";
    }

    /**
     * Dashboard principal (solo usuarios autenticados)
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("mensaje", "¡Bienvenido al Dashboard!");
        return "admin/dashboard";
    }

    /**
     * Formulario de registro de jugador
     */
    @GetMapping("/crear")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("jugador", new JugadorDTO());
        model.addAttribute("juegos", juegoRepositorio.findAll());
        return "users/crear";
    }

    /**
     * Página de decisión: login o registro
     */
    @GetMapping("/inicioLogeoCrear")
    public String inicioLoginCrear() {
        return "users/inicioLogeoCrear";
    }

    /**
     * Página de error personalizada
     */
    @GetMapping("/error")
    public String error(Model model) {
        model.addAttribute("mensaje", "Ha ocurrido un error inesperado");
        return "error";
    }

    @GetMapping("/editar")
        public String editarPersona(){
            return "admin/editar";

    }



}
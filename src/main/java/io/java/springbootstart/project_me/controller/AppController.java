package io.java.springbootstart.project_me.controller;

import io.java.springbootstart.project_me.dto.JugadorDTO;
import io.java.springbootstart.project_me.modelo.Jugador;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AppController {

    /**
     * Página de inicio
     */
    @GetMapping({"/", "/index"})
    public String inicio(Model model) {
        model.addAttribute("mensaje", "¡Bienvenidos a VideoGamesHub!");
        return "admin/index";
    }

    /**
     * Listado de juegos
     */
    @GetMapping("/listadoGames")
    public String listadoJuegos(Model model) {
        model.addAttribute("titulo", "Listado de Juegos Populares");
        return "admin/listadoGames";
    }

    /**
     * Formulario de registro de jugador
     */
    @GetMapping("/crear")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("jugador", new JugadorDTO());
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
     * Página de login
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {

        if (error != null) {
            model.addAttribute("mensajeError", "Credenciales inválidas. Por favor, intenta de nuevo.");
        }

        if (logout != null) {
            model.addAttribute("mensajeExito", "Has cerrado sesión exitosamente.");
        }

        return "users/login";
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

    @GetMapping("/vistaJugador")
    public String verPerfil(Model model) {

        Jugador jugador = new Jugador();
        jugador.setNombre("Jugador Test");
        jugador.setApellido("Demo");
        jugador.setUsuario("test123");
        jugador.setEmail("test@email.com");

        model.addAttribute("jugador", jugador);

        return "admin/vistaJugador";
    }

    @GetMapping("/players")
    public String verPlayers(){

        return "admin/listaJugadores";
    }

}
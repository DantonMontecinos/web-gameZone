package io.java.springbootstart.project_me.controller;

import io.java.springbootstart.project_me.dto.JugadorDTO;
import io.java.springbootstart.project_me.modelo.Juego;
import io.java.springbootstart.project_me.modelo.Jugador;
import io.java.springbootstart.project_me.repository.JuegoRepositorio;
import io.java.springbootstart.project_me.repository.JugadorRepositorio;
import io.java.springbootstart.project_me.service.JugadorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/jugadores")
public class JugadorControlador {

    private final JugadorService jugadorService;
    private final JuegoRepositorio juegoRepositorio;

    @Autowired
    public JugadorControlador(JugadorService jugadorService, JuegoRepositorio juegoRepositorio) {
        this.jugadorService = jugadorService;
        this.juegoRepositorio = juegoRepositorio;
    }

    /**
     * Mostrar formulario de registro
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("jugador", new JugadorDTO());
        model.addAttribute("juegos", juegoRepositorio.findAll());


        return "users/crear";
    }

    /**
     * Guardar nuevo jugador
     */
    @PostMapping("/guardar")
    public String guardarJugador(@Valid @ModelAttribute("jugador") JugadorDTO jugadorDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        System.out.println("=== MÉTODO GUARDAR EJECUTÁNDOSE ===");
        System.out.println("Juego ID recibido: " + jugadorDTO.getJuegoId());
        System.out.println("Nombre: " + jugadorDTO.getNombre());
        System.out.println("Email: " + jugadorDTO.getEmail());

        if (result.hasErrors()) {
            System.out.println("Errores de validación: " + result.getAllErrors());
            List<Juego> juegos = juegoRepositorio.findAll();
            model.addAttribute("juegos", juegos);
            return "users/crear";
        }

        try {
            Jugador jugadorGuardado = jugadorService.crearJugador(jugadorDTO);
            System.out.println("Jugador guardado con ID: " + jugadorGuardado.getId());
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "¡Jugador registrado exitosamente! Ya puedes iniciar sesión.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/jugadores/nuevo";
        }
    }

    /**
     * Mostrar perfil de jugador
     */

    @GetMapping("/mi-perfil")
    public String redirigirAPerfil(Authentication authentication,
                                   RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();

        Optional<Jugador> jugadorOpt = jugadorService.obtenerJugadorPorEmail(email);

        if (jugadorOpt.isPresent()) {
            return "redirect:/jugadores/perfil/" + jugadorOpt.get().getId();
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "Jugador no encontrado");
            return "redirect:/login";
        }
    }


    @GetMapping("/perfil/{id}")
    public String mostrarPerfilJugador(@PathVariable Long id, Model model,
                                       RedirectAttributes redirectAttributes) {

        try {
            Optional<Jugador> jugadorOpt = jugadorService.obtenerJugadorPorId(id);

            if (jugadorOpt.isPresent()) {
                model.addAttribute("jugador", jugadorOpt.get());
                // 👇 SOLO CAMBIA ESTO: debe coincidir con tu archivo HTML
                return "admin/vistaJugador"; // Asegúrate que el archivo sea vistaJugador.html
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", "Jugador no encontrado");
                return "redirect:/admin/lista"; // Ajusta la ruta según tu mapeo
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al cargar el perfil");
            return "redirect:/admin/lista";
        }

    }


    /**
     * Listar jugadores
     */
    @GetMapping({"/lista", "/listaJugadores"})
    public String listarJugadores(Model model) {
        List<Jugador> jugadores = jugadorService.obtenerTodosLosJugadores();
        model.addAttribute("jugadores", jugadores);
        model.addAttribute("totalJugadores", jugadores.size());
        return "admin/listaJugadores";
    }

    /**
     * Mostrar formulario de edición
     */
    @GetMapping("/editar/{id}")
    public String editarJugador(@PathVariable Long id, Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            Optional<Jugador> jugadorOpt = jugadorService.obtenerJugadorPorId(id);

            if (jugadorOpt.isPresent()) {
                JugadorDTO jugadorDTO = jugadorService.convertirADto(jugadorOpt.get());
                model.addAttribute("jugador", jugadorDTO);
                model.addAttribute("jugadorId", id);
                return "admin/editar";
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", "Jugador no encontrado");
                return "redirect:/jugadores/lista";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al cargar el formulario de edición");
            return "redirect:/jugadores/lista";
        }
    }

    /**
     * Actualizar jugador
     */
    @PostMapping("/actualizar/{id}")
    public String actualizarJugador(@PathVariable Long id,
                                    @Valid @ModelAttribute("jugador") JugadorDTO jugadorDTO,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {

        if (result.hasErrors()) {
            model.addAttribute("jugadorId", id);
            return "admin/editar";
        }

        try {
            jugadorService.actualizarJugador(id, jugadorDTO);
            redirectAttributes.addFlashAttribute("mensajeExito", "Perfil actualizado exitosamente");
            return "redirect:/jugadores/perfil/" + id;

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/jugadores/editar/" + id;
        }
    }

    /**
     * Desactivar jugador (borrado lógico)
     */
    @GetMapping("/desactivar/{id}")
    public String desactivarJugador(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            jugadorService.desactivarJugador(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Jugador desactivado exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/jugadores/lista";
    }

    /**
     * Eliminar jugador permanentemente
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarJugador(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            jugadorService.eliminarJugadorPermanentemente(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Jugador eliminado permanentemente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/jugadores/lista";
    }

    /**
     * Buscar jugadores
     */
    @GetMapping("/buscar")
    public String buscarJugadores(@RequestParam(required = false) String termino, Model model) {
        List<Jugador> jugadores;

        if (termino != null && !termino.trim().isEmpty()) {
            // Aquí puedes implementar la búsqueda usando el método del repositorio
            jugadores = jugadorService.obtenerTodosLosJugadores(); // Por ahora obtiene todos
            model.addAttribute("termino", termino);
        } else {
            jugadores = jugadorService.obtenerTodosLosJugadores();
        }

        model.addAttribute("jugadores", jugadores);
        model.addAttribute("totalJugadores", jugadores.size());
        return "admin/listaJugadores";
    }


}
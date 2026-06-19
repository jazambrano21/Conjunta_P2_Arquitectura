package ec.edu.espe.andesfin.controller;

import ec.edu.espe.andesfin.dto.UsuarioDTO;
import ec.edu.espe.andesfin.services.UsuarioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller delgado: delega al service y solo trabaja con DTOs.
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** GET /usuarios -> lista todos los usuarios. */
    @GetMapping
    public List<UsuarioDTO> listar() {
        return usuarioService.findAll();
    }

    /** GET /usuarios/{id} -> un usuario por id. */
    @GetMapping("/{id}")
    public UsuarioDTO obtener(@PathVariable UUID id) {
        return usuarioService.findById(id);
    }
}

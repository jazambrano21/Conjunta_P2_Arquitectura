package ec.edu.espe.andesfin.controller;

import ec.edu.espe.andesfin.dto.SimulacionRequestDTO;
import ec.edu.espe.andesfin.dto.SimulacionResponseDTO;
import ec.edu.espe.andesfin.services.SimulacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller delgado de simulaciones (modulo de Persona B, adaptado al PDF).
 */
@RestController
@RequestMapping("/simulaciones")
public class SimulacionController {

    private final SimulacionService simulacionService;

    public SimulacionController(SimulacionService simulacionService) {
        this.simulacionService = simulacionService;
    }

    /** POST /simulaciones -> ejecuta y persiste una simulacion de inversion. */
    @PostMapping
    public ResponseEntity<SimulacionResponseDTO> crear(@Valid @RequestBody SimulacionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(simulacionService.crearSimulacion(request));
    }

    /** GET /simulaciones/{usuarioId} -> simulaciones previas de un usuario. */
    @GetMapping("/{usuarioId}")
    public List<SimulacionResponseDTO> porUsuario(@PathVariable UUID usuarioId) {
        return simulacionService.obtenerPorUsuario(usuarioId);
    }
}

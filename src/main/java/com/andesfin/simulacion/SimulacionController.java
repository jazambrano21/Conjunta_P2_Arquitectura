package com.andesfin.simulacion;

import com.andesfin.simulacion.dto.SimulacionRequestDTO;
import com.andesfin.simulacion.dto.SimulacionResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/simulaciones")
@RequiredArgsConstructor
public class SimulacionController {

    private final SimulacionService simulacionService;

    @PostMapping
    public ResponseEntity<SimulacionResponseDTO> crearSimulacion(
            @Valid @RequestBody SimulacionRequestDTO request) {
        SimulacionResponseDTO response = simulacionService.crearSimulacion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<SimulacionResponseDTO>> obtenerSimulacionesPorUsuario(
            @PathVariable Long usuarioId) {
        List<SimulacionResponseDTO> simulaciones = simulacionService.obtenerSimulacionesPorUsuario(usuarioId);
        return ResponseEntity.ok(simulaciones);
    }
}

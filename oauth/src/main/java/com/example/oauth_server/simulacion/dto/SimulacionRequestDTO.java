package com.example.oauth_server.simulacion.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulacionRequestDTO {

    @NotNull(message = "El usuarioId es requerido")
    private Long usuarioId;

    @NotNull(message = "El capital disponible es requerido")
    @DecimalMin(value = "0.01", message = "El capital debe ser mayor a 0")
    private BigDecimal capitalDisponible;

    private List<Long> productosIds;
}

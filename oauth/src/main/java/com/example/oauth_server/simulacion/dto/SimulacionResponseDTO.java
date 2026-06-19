package com.example.oauth_server.simulacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulacionResponseDTO {

    private Long id;
    private Long usuarioId;
    private BigDecimal capitalDisponible;
    private BigDecimal gananciaTotal;
    private BigDecimal capitalInvertido;
    private String tipoResultado;
    private LocalDateTime fechaCreacion;
    private List<ProductoSimulacionDTO> productos;
}

package com.andesfin.simulacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoSimulacionDTO {

    private Long productoId;
    private String nombreProducto;
    private BigDecimal montoInvertido;
    private BigDecimal tasaRetorno;
    private BigDecimal gananciaEstimada;
    private String riesgo;
}

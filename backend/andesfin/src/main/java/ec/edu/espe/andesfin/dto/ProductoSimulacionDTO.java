package ec.edu.espe.andesfin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Producto seleccionado en el resultado de una simulacion.
 * JSON: nombre, precio, porcentaje_ganancia, ganancia_esperada.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoSimulacionDTO {

    private String nombre;

    private BigDecimal precio;

    @JsonProperty("porcentaje_ganancia")
    private BigDecimal porcentajeGanancia;

    @JsonProperty("ganancia_esperada")
    private BigDecimal gananciaEsperada;
}

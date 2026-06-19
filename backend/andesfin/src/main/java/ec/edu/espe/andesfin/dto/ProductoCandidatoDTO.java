package ec.edu.espe.andesfin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Producto candidato enviado dinamicamente en el POST /simulaciones.
 * JSON: nombre, precio, porcentaje_ganancia.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoCandidatoDTO {

    @NotBlank
    private String nombre;

    @NotNull
    @PositiveOrZero
    private BigDecimal precio;

    @NotNull
    @PositiveOrZero
    @JsonProperty("porcentaje_ganancia")
    private BigDecimal porcentajeGanancia;
}

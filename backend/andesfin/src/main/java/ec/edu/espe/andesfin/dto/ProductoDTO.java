package ec.edu.espe.andesfin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de ProductoFinanciero.
 * JSON: id, nombre, descripcion, costo, porcentaje_retorno, activo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDTO {

    private UUID id;

    @NotBlank
    private String nombre;

    private String descripcion;

    @PositiveOrZero
    private BigDecimal costo;

    @PositiveOrZero
    @JsonProperty("porcentaje_retorno")
    private BigDecimal porcentajeRetorno;

    private boolean activo;
}

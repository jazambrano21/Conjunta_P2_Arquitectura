package ec.edu.espe.andesfin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Solicitud de simulacion (POST /simulaciones).
 * JSON: usuario_id, capital_disponible, productos:[{nombre, precio, porcentaje_ganancia}].
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulacionRequestDTO {

    @NotNull(message = "usuario_id es requerido")
    @JsonProperty("usuario_id")
    private UUID usuarioId;

    @NotNull(message = "capital_disponible es requerido")
    @DecimalMin(value = "0.00", message = "El capital no puede ser negativo")
    @JsonProperty("capital_disponible")
    private BigDecimal capitalDisponible;

    @NotEmpty(message = "Debe enviar al menos un producto candidato")
    @Valid
    private List<ProductoCandidatoDTO> productos;
}

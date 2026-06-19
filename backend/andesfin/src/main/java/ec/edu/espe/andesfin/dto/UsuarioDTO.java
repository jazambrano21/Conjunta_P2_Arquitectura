package ec.edu.espe.andesfin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de Usuario. JSON: id, nombre, email, capital_disponible.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {

    private UUID id;

    @NotBlank
    private String nombre;

    @NotBlank
    @Email
    private String email;

    @PositiveOrZero
    @JsonProperty("capital_disponible")
    private BigDecimal capitalDisponible;
}

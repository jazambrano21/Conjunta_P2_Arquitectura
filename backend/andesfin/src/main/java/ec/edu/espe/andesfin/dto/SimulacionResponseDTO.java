package ec.edu.espe.andesfin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Respuesta de una simulacion (POST /simulaciones y GET /simulaciones/{usuarioId}).
 * JSON: id, usuario_id, fecha_simulacion, capital_disponible,
 * productos_seleccionados, costo_total, capital_restante, ganancia_total,
 * retorno_total_porcentaje, eficiencia_capital, cantidad_productos, mensaje.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimulacionResponseDTO {

    private UUID id;

    @JsonProperty("usuario_id")
    private UUID usuarioId;

    @JsonProperty("fecha_simulacion")
    private LocalDateTime fechaSimulacion;

    @JsonProperty("capital_disponible")
    private BigDecimal capitalDisponible;

    @JsonProperty("productos_seleccionados")
    private List<ProductoSimulacionDTO> productosSeleccionados;

    @JsonProperty("costo_total")
    private BigDecimal costoTotal;

    @JsonProperty("capital_restante")
    private BigDecimal capitalRestante;

    @JsonProperty("ganancia_total")
    private BigDecimal gananciaTotal;

    @JsonProperty("retorno_total_porcentaje")
    private BigDecimal retornoTotalPorcentaje;

    @JsonProperty("eficiencia_capital")
    private BigDecimal eficienciaCapital;

    @JsonProperty("cantidad_productos")
    private Integer cantidadProductos;

    private String mensaje;
}

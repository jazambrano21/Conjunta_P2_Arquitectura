package ec.edu.espe.andesfin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Detalle de un producto seleccionado dentro de una {@link Simulacion}.
 * Guarda el "snapshot" de los parametros dinamicos enviados por el cliente
 * (nombre, precio, porcentaje_ganancia) mas la ganancia estimada calculada.
 */
@Entity
@Table(name = "simulacion_productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulacionProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulacion_id", nullable = false)
    private Simulacion simulacion;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "porcentaje_ganancia", precision = 5, scale = 2)
    private BigDecimal porcentajeGanancia;

    @Column(name = "ganancia_esperada", precision = 10, scale = 2)
    private BigDecimal gananciaEsperada;
}

package ec.edu.espe.andesfin.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Simulacion (Persona B, adaptada a UUID y al contrato del PDF por Persona A).
 *
 * <p>Campos segun PDF: id (UUID), usuario_id (UUID FK), fecha_simulacion,
 * capital_disponible (10,2), ganancia_total (10,2), y los productos
 * seleccionados como relacion 1:N ({@link SimulacionProducto}).
 */
@Entity
@Table(name = "simulaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Simulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "fecha_simulacion", nullable = false)
    private LocalDateTime fechaSimulacion;

    @Column(name = "capital_disponible", nullable = false, precision = 10, scale = 2)
    private BigDecimal capitalDisponible;

    @Column(name = "ganancia_total", precision = 10, scale = 2)
    private BigDecimal gananciaTotal;

    @OneToMany(mappedBy = "simulacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SimulacionProducto> productosSeleccionados = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (fechaSimulacion == null) {
            fechaSimulacion = LocalDateTime.now();
        }
    }

    /** Helper para mantener la relacion bidireccional consistente. */
    public void addProducto(SimulacionProducto producto) {
        producto.setSimulacion(this);
        this.productosSeleccionados.add(producto);
    }
}

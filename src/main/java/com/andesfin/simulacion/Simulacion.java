package com.andesfin.simulacion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "simulaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Simulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "capital_disponible", nullable = false, precision = 15, scale = 2)
    private BigDecimal capitalDisponible;

    @Column(name = "ganancia_total", precision = 15, scale = 2)
    private BigDecimal gananciaTotal;

    @Column(name = "capital_invertido", precision = 15, scale = 2)
    private BigDecimal capitalInvertido;

    @Column(name = "tipo_resultado", length = 50)
    private String tipoResultado; // OPTIMO, MINIMO, FONDOS_INSUFICIENTES, CAPITAL_COMPLETO

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "simulacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SimulacionProducto> productos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}

package com.example.oauth_server.simulacion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "simulacion_productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulacionProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulacion_id", nullable = false)
    private Simulacion simulacion;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "nombre_producto", length = 200)
    private String nombreProducto;

    @Column(name = "monto_invertido", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoInvertido;

    @Column(name = "tasa_retorno", precision = 5, scale = 2)
    private BigDecimal tasaRetorno;

    @Column(name = "ganancia_estimada", precision = 15, scale = 2)
    private BigDecimal gananciaEstimada;

    @Column(name = "riesgo", length = 50)
    private String riesgo;
}

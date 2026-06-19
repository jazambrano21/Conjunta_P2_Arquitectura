package ec.edu.espe.andesfin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * ProductoFinanciero (Persona A). Campos segun PDF: id (UUID), nombre,
 * descripcion (texto), costo (10,2), porcentaje_retorno (5,2), activo (boolean).
 */
@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoFinanciero {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "costo", precision = 10, scale = 2)
    private BigDecimal costo;

    @Column(name = "porcentaje_retorno", precision = 5, scale = 2)
    private BigDecimal porcentajeRetorno;

    @Column(name = "activo", nullable = false)
    private boolean activo;
}

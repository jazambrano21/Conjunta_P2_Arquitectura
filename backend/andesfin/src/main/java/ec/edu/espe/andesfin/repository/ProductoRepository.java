package ec.edu.espe.andesfin.repository;

import ec.edu.espe.andesfin.entity.ProductoFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository Pattern via Spring Data JPA.
 */
@Repository
public interface ProductoRepository extends JpaRepository<ProductoFinanciero, UUID> {

    /** Solo productos activos (activo = true) -> GET /productos. */
    List<ProductoFinanciero> findByActivoTrue();
}

package ec.edu.espe.andesfin.repository;

import ec.edu.espe.andesfin.entity.Simulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository Pattern via Spring Data JPA.
 */
@Repository
public interface SimulacionRepository extends JpaRepository<Simulacion, UUID> {

    List<Simulacion> findByUsuarioIdOrderByFechaSimulacionDesc(UUID usuarioId);
}

package ec.edu.espe.andesfin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio AndesFin (Evaluacion Conjunta 2P - Arquitectura de Software).
 *
 * <p>Una sola aplicacion Spring Boot que expone usuarios, productos de inversion
 * y simulaciones. Estructura por capas: controller / services / repository /
 * entity / dto / config.
 */
@SpringBootApplication
public class AndesfinApplication {

    public static void main(String[] args) {
        SpringApplication.run(AndesfinApplication.class, args);
    }
}

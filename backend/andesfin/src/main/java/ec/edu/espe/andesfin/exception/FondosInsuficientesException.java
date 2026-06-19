package ec.edu.espe.andesfin.exception;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Se lanza cuando el capital disponible no alcanza ni para el producto mas barato.
 * El {@link GlobalExceptionHandler} la traduce al body de error del PDF.
 */
@Getter
public class FondosInsuficientesException extends RuntimeException {

    private final BigDecimal capitalDisponible;
    private final BigDecimal productoMasBarato;
    private final BigDecimal diferenciaNecesaria;

    public FondosInsuficientesException(BigDecimal capitalDisponible,
                                        BigDecimal productoMasBarato,
                                        BigDecimal diferenciaNecesaria) {
        super("Fondos insuficientes");
        this.capitalDisponible = capitalDisponible;
        this.productoMasBarato = productoMasBarato;
        this.diferenciaNecesaria = diferenciaNecesaria;
    }
}

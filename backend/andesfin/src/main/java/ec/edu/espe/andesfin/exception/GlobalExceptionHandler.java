package ec.edu.espe.andesfin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Traduce excepciones de negocio a respuestas JSON con el formato del PDF.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FondosInsuficientesException.class)
    public ResponseEntity<Map<String, Object>> handleFondosInsuficientes(FondosInsuficientesException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Fondos insuficientes");
        body.put("detalle", "El capital disponible ($" + ex.getCapitalDisponible()
                + ") es insuficiente para adquirir cualquier producto de la lista.");
        body.put("capital_disponible", ex.getCapitalDisponible());
        body.put("producto_mas_barato", ex.getProductoMasBarato());
        body.put("diferencia_necesaria", ex.getDiferenciaNecesaria());
        body.put("recomendacion",
                "Aumente su capital o consulte productos con menor inversion minima.");
        return ResponseEntity.badRequest().body(body);
    }
}

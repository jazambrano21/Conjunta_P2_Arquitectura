package ec.edu.espe.andesfin.controller;

import ec.edu.espe.andesfin.dto.ProductoDTO;
import ec.edu.espe.andesfin.services.ProductoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller delgado: delega al service y solo trabaja con DTOs.
 */
@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /** GET /productos -> lista solo los productos activos. */
    @GetMapping
    public List<ProductoDTO> listar() {
        return productoService.findActivos();
    }

    /** GET /productos/{id} -> un producto por id. */
    @GetMapping("/{id}")
    public ProductoDTO obtener(@PathVariable UUID id) {
        return productoService.findById(id);
    }
}

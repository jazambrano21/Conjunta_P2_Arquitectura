package ec.edu.espe.andesfin.services;

import ec.edu.espe.andesfin.dto.ProductoDTO;
import ec.edu.espe.andesfin.entity.ProductoFinanciero;
import ec.edu.espe.andesfin.repository.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * Service Pattern: logica de negocio de productos. CRUD completo; el controller
 * solo expone los GET requeridos.
 */
@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /** Solo productos activos -> GET /productos. */
    @Transactional(readOnly = true)
    public List<ProductoDTO> findActivos() {
        return productoRepository.findByActivoTrue().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findAll() {
        return productoRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public ProductoDTO findById(UUID id) {
        return productoRepository.findById(id).map(this::toDTO).orElseThrow(() -> notFound(id));
    }

    public ProductoDTO create(ProductoDTO dto) {
        ProductoFinanciero p = ProductoFinanciero.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .costo(dto.getCosto())
                .porcentajeRetorno(dto.getPorcentajeRetorno())
                .activo(dto.isActivo())
                .build();
        return toDTO(productoRepository.save(p));
    }

    public ProductoDTO update(UUID id, ProductoDTO dto) {
        ProductoFinanciero p = productoRepository.findById(id).orElseThrow(() -> notFound(id));
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setCosto(dto.getCosto());
        p.setPorcentajeRetorno(dto.getPorcentajeRetorno());
        p.setActivo(dto.isActivo());
        return toDTO(productoRepository.save(p));
    }

    public void delete(UUID id) {
        if (!productoRepository.existsById(id)) {
            throw notFound(id);
        }
        productoRepository.deleteById(id);
    }

    private ProductoDTO toDTO(ProductoFinanciero p) {
        return ProductoDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .costo(p.getCosto())
                .porcentajeRetorno(p.getPorcentajeRetorno())
                .activo(p.isActivo())
                .build();
    }

    private ResponseStatusException notFound(UUID id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + id);
    }
}

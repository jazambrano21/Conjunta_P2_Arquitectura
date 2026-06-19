package ec.edu.espe.andesfin.services;

import ec.edu.espe.andesfin.dto.ProductoCandidatoDTO;
import ec.edu.espe.andesfin.dto.ProductoSimulacionDTO;
import ec.edu.espe.andesfin.dto.SimulacionRequestDTO;
import ec.edu.espe.andesfin.dto.SimulacionResponseDTO;
import ec.edu.espe.andesfin.entity.Simulacion;
import ec.edu.espe.andesfin.entity.SimulacionProducto;
import ec.edu.espe.andesfin.exception.FondosInsuficientesException;
import ec.edu.espe.andesfin.repository.SimulacionRepository;
import ec.edu.espe.andesfin.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Service Pattern: optimizacion de inversion (Persona B, adaptado al PDF + UUID).
 *
 * <p>Recibe productos candidatos dinamicos (nombre, precio, porcentaje_ganancia),
 * selecciona greedy por mayor % de retorno respetando el capital, persiste la
 * simulacion con su detalle y devuelve el resultado segun el contrato del PDF.
 */
@Service
@Transactional
public class SimulacionService {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private final SimulacionRepository simulacionRepository;
    private final UsuarioRepository usuarioRepository;

    public SimulacionService(SimulacionRepository simulacionRepository,
                             UsuarioRepository usuarioRepository) {
        this.simulacionRepository = simulacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public SimulacionResponseDTO crearSimulacion(SimulacionRequestDTO request) {
        if (!usuarioRepository.existsById(request.getUsuarioId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Usuario no encontrado: " + request.getUsuarioId());
        }

        BigDecimal capital = scale(request.getCapitalDisponible());
        List<ProductoCandidatoDTO> candidatos = request.getProductos();

        // Caso "fondos insuficientes": ni el producto mas barato cabe en el capital.
        BigDecimal precioMasBarato = candidatos.stream()
                .map(ProductoCandidatoDTO::getPrecio)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        if (capital.compareTo(precioMasBarato) < 0) {
            throw new FondosInsuficientesException(
                    capital, scale(precioMasBarato), scale(precioMasBarato.subtract(capital)));
        }

        // Seleccion greedy por mayor porcentaje de ganancia que quepa en el capital restante.
        List<ProductoCandidatoDTO> ordenados = new ArrayList<>(candidatos);
        ordenados.sort(Comparator.comparing(ProductoCandidatoDTO::getPorcentajeGanancia,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        Simulacion simulacion = Simulacion.builder()
                .usuarioId(request.getUsuarioId())
                .capitalDisponible(capital)
                .productosSeleccionados(new ArrayList<>())
                .build();

        BigDecimal capitalRestante = capital;
        BigDecimal gananciaTotal = BigDecimal.ZERO;

        for (ProductoCandidatoDTO c : ordenados) {
            if (c.getPrecio() != null && c.getPrecio().compareTo(capitalRestante) <= 0) {
                BigDecimal ganancia = scale(c.getPrecio()
                        .multiply(nz(c.getPorcentajeGanancia()))
                        .divide(CIEN, 4, RoundingMode.HALF_UP));

                SimulacionProducto detalle = SimulacionProducto.builder()
                        .nombre(c.getNombre())
                        .precio(scale(c.getPrecio()))
                        .porcentajeGanancia(c.getPorcentajeGanancia())
                        .gananciaEsperada(ganancia)
                        .build();
                simulacion.addProducto(detalle);

                gananciaTotal = gananciaTotal.add(ganancia);
                capitalRestante = capitalRestante.subtract(c.getPrecio());
            }
        }

        simulacion.setGananciaTotal(scale(gananciaTotal));
        Simulacion guardada = simulacionRepository.save(simulacion);

        return buildResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<SimulacionResponseDTO> obtenerPorUsuario(UUID usuarioId) {
        return simulacionRepository.findByUsuarioIdOrderByFechaSimulacionDesc(usuarioId).stream()
                .map(this::buildResponse)
                .toList();
    }

    // --- helpers ---

    private SimulacionResponseDTO buildResponse(Simulacion s) {
        List<ProductoSimulacionDTO> productos = s.getProductosSeleccionados().stream()
                .map(p -> new ProductoSimulacionDTO(
                        p.getNombre(), p.getPrecio(), p.getPorcentajeGanancia(), p.getGananciaEsperada()))
                .toList();

        BigDecimal costoTotal = s.getProductosSeleccionados().stream()
                .map(SimulacionProducto::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal capital = nz(s.getCapitalDisponible());
        BigDecimal capitalRestante = capital.subtract(costoTotal);
        BigDecimal ganancia = nz(s.getGananciaTotal());

        BigDecimal retorno = costoTotal.signum() > 0
                ? scale(ganancia.multiply(CIEN).divide(costoTotal, 4, RoundingMode.HALF_UP))
                : BigDecimal.ZERO;
        BigDecimal eficiencia = capital.signum() > 0
                ? scale(costoTotal.multiply(CIEN).divide(capital, 4, RoundingMode.HALF_UP))
                : BigDecimal.ZERO;
        int cantidad = productos.size();

        return SimulacionResponseDTO.builder()
                .id(s.getId())
                .usuarioId(s.getUsuarioId())
                .fechaSimulacion(s.getFechaSimulacion())
                .capitalDisponible(scale(capital))
                .productosSeleccionados(productos)
                .costoTotal(scale(costoTotal))
                .capitalRestante(scale(capitalRestante))
                .gananciaTotal(scale(ganancia))
                .retornoTotalPorcentaje(retorno)
                .eficienciaCapital(eficiencia)
                .cantidadProductos(cantidad)
                .mensaje(mensaje(cantidad, eficiencia))
                .build();
    }

    private String mensaje(int cantidad, BigDecimal eficiencia) {
        if (cantidad <= 1) {
            return "Simulacion con ganancias minimas. Considere aumentar capital para mejores opciones.";
        }
        if (eficiencia.compareTo(new BigDecimal("90")) >= 0) {
            return "Simulacion optima con alta eficiencia de capital (" + eficiencia + "% utilizado).";
        }
        return "Simulacion exitosa con ganancias optimas.";
    }

    private static BigDecimal scale(BigDecimal value) {
        return nz(value).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}

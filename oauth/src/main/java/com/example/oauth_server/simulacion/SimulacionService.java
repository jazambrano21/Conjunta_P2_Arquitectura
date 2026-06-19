package com.example.oauth_server.simulacion;

import com.example.oauth_server.simulacion.dto.ProductoSimulacionDTO;
import com.example.oauth_server.simulacion.dto.SimulacionRequestDTO;
import com.example.oauth_server.simulacion.dto.SimulacionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SimulacionService {

    private final SimulacionRepository simulacionRepository;

    // Datos de ejemplo de productos financieros (en producción vendrían de ProductoRepository)
    private static final List<ProductoFinanciero> PRODUCTOS_EJEMPLO = List.of(
        new ProductoFinanciero(1L, "CDT Básico", new BigDecimal("1000.00"), new BigDecimal("8.50"), "BAJO"),
        new ProductoFinanciero(2L, "Fondo Inversión", new BigDecimal("5000.00"), new BigDecimal("12.00"), "MEDIO"),
        new ProductoFinanciero(3L, "Acciones Tech", new BigDecimal("2000.00"), new BigDecimal("15.50"), "ALTO"),
        new ProductoFinanciero(4L, "Bonos Gobierno", new BigDecimal("10000.00"), new BigDecimal("6.00"), "BAJO"),
        new ProductoFinanciero(5L, "Crypto ETF", new BigDecimal("3000.00"), new BigDecimal("20.00"), "ALTO")
    );

    @Transactional
    public SimulacionResponseDTO crearSimulacion(SimulacionRequestDTO request) {
        BigDecimal capitalDisponible = request.getCapitalDisponible();
        List<ProductoFinanciero> productosDisponibles = obtenerProductosDisponibles(request.getProductosIds());

        // Evaluar los 4 casos
        ResultadoOptimizacion resultado = optimizarInversion(capitalDisponible, productosDisponibles);

        // Crear entidad Simulacion
        Simulacion simulacion = new Simulacion();
        simulacion.setUsuarioId(request.getUsuarioId());
        simulacion.setCapitalDisponible(capitalDisponible);
        simulacion.setGananciaTotal(resultado.getGananciaTotal());
        simulacion.setCapitalInvertido(resultado.getCapitalInvertido());
        simulacion.setTipoResultado(resultado.getTipoResultado());

        // Crear detalles de productos
        for (ProductoSimulacionDTO prodDTO : resultado.getProductos()) {
            SimulacionProducto sp = new SimulacionProducto();
            sp.setSimulacion(simulacion);
            sp.setProductoId(prodDTO.getProductoId());
            sp.setNombreProducto(prodDTO.getNombreProducto());
            sp.setMontoInvertido(prodDTO.getMontoInvertido());
            sp.setTasaRetorno(prodDTO.getTasaRetorno());
            sp.setGananciaEstimada(prodDTO.getGananciaEstimada());
            sp.setRiesgo(prodDTO.getRiesgo());
            simulacion.getProductos().add(sp);
        }

        simulacion = simulacionRepository.save(simulacion);

        return convertirAResponseDTO(simulacion);
    }

    @Transactional(readOnly = true)
    public List<SimulacionResponseDTO> obtenerSimulacionesPorUsuario(Long usuarioId) {
        return simulacionRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    private ResultadoOptimizacion optimizarInversion(BigDecimal capital, List<ProductoFinanciero> productos) {
        BigDecimal montoMinimoInversion = productos.stream()
                .map(ProductoFinanciero::getMontoMinimo)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // Caso 1: Fondos insuficientes
        if (capital.compareTo(montoMinimoInversion) < 0) {
            return new ResultadoOptimizacion(
                    "FONDOS_INSUFICIENTES",
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    new ArrayList<>()
            );
        }

        // Caso 2: Capital completo (puede invertir en todos los productos)
        BigDecimal capitalTotalRequerido = productos.stream()
                .map(ProductoFinanciero::getMontoMinimo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (capital.compareTo(capitalTotalRequerido) >= 0) {
            List<ProductoSimulacionDTO> productosDTO = new ArrayList<>();
            BigDecimal gananciaTotal = BigDecimal.ZERO;
            BigDecimal capitalInvertido = BigDecimal.ZERO;

            for (ProductoFinanciero pf : productos) {
                BigDecimal montoInvertido = pf.getMontoMinimo();
                BigDecimal ganancia = montoInvertido.multiply(pf.getTasaRetorno())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                
                productosDTO.add(new ProductoSimulacionDTO(
                        pf.getId(),
                        pf.getNombre(),
                        montoInvertido,
                        pf.getTasaRetorno(),
                        ganancia,
                        pf.getRiesgo()
                ));
                
                gananciaTotal = gananciaTotal.add(ganancia);
                capitalInvertido = capitalInvertido.add(montoInvertido);
            }

            return new ResultadoOptimizacion(
                    "CAPITAL_COMPLETO",
                    gananciaTotal,
                    capitalInvertido,
                    productosDTO
            );
        }

        // Caso 3 y 4: Optimización para maximizar ganancia
        // Ordenar productos por tasa de retorno descendente
        List<ProductoFinanciero> productosOrdenados = new ArrayList<>(productos);
        productosOrdenados.sort(Comparator.comparing(ProductoFinanciero::getTasaRetorno).reversed());

        List<ProductoSimulacionDTO> productosSeleccionados = new ArrayList<>();
        BigDecimal capitalRestante = capital;
        BigDecimal gananciaTotal = BigDecimal.ZERO;
        BigDecimal capitalInvertido = BigDecimal.ZERO;

        for (ProductoFinanciero pf : productosOrdenados) {
            if (capitalRestante.compareTo(pf.getMontoMinimo()) >= 0) {
                BigDecimal montoInvertido = pf.getMontoMinimo();
                BigDecimal ganancia = montoInvertido.multiply(pf.getTasaRetorno())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                
                productosSeleccionados.add(new ProductoSimulacionDTO(
                        pf.getId(),
                        pf.getNombre(),
                        montoInvertido,
                        pf.getTasaRetorno(),
                        ganancia,
                        pf.getRiesgo()
                ));
                
                gananciaTotal = gananciaTotal.add(ganancia);
                capitalInvertido = capitalInvertido.add(montoInvertido);
                capitalRestante = capitalRestante.subtract(montoInvertido);
            }
        }

        // Caso 4: Mínimo (solo un producto)
        if (productosSeleccionados.size() == 1) {
            return new ResultadoOptimizacion(
                    "MINIMO",
                    gananciaTotal,
                    capitalInvertido,
                    productosSeleccionados
            );
        }

        // Caso 3: Óptimo (múltiples productos seleccionados)
        return new ResultadoOptimizacion(
                "OPTIMO",
                gananciaTotal,
                capitalInvertido,
                productosSeleccionados
        );
    }

    private List<ProductoFinanciero> obtenerProductosDisponibles(List<Long> productosIds) {
        if (productosIds == null || productosIds.isEmpty()) {
            return PRODUCTOS_EJEMPLO;
        }
        return PRODUCTOS_EJEMPLO.stream()
                .filter(p -> productosIds.contains(p.getId()))
                .toList();
    }

    private SimulacionResponseDTO convertirAResponseDTO(Simulacion simulacion) {
        List<ProductoSimulacionDTO> productosDTO = simulacion.getProductos().stream()
                .map(sp -> new ProductoSimulacionDTO(
                        sp.getProductoId(),
                        sp.getNombreProducto(),
                        sp.getMontoInvertido(),
                        sp.getTasaRetorno(),
                        sp.getGananciaEstimada(),
                        sp.getRiesgo()
                ))
                .toList();

        return new SimulacionResponseDTO(
                simulacion.getId(),
                simulacion.getUsuarioId(),
                simulacion.getCapitalDisponible(),
                simulacion.getGananciaTotal(),
                simulacion.getCapitalInvertido(),
                simulacion.getTipoResultado(),
                simulacion.getFechaCreacion(),
                productosDTO
        );
    }

    // Clases auxiliares
    private static class ProductoFinanciero {
        private final Long id;
        private final String nombre;
        private final BigDecimal montoMinimo;
        private final BigDecimal tasaRetorno;
        private final String riesgo;

        public ProductoFinanciero(Long id, String nombre, BigDecimal montoMinimo, BigDecimal tasaRetorno, String riesgo) {
            this.id = id;
            this.nombre = nombre;
            this.montoMinimo = montoMinimo;
            this.tasaRetorno = tasaRetorno;
            this.riesgo = riesgo;
        }

        public Long getId() { return id; }
        public String getNombre() { return nombre; }
        public BigDecimal getMontoMinimo() { return montoMinimo; }
        public BigDecimal getTasaRetorno() { return tasaRetorno; }
        public String getRiesgo() { return riesgo; }
    }

    private static class ResultadoOptimizacion {
        private final String tipoResultado;
        private final BigDecimal gananciaTotal;
        private final BigDecimal capitalInvertido;
        private final List<ProductoSimulacionDTO> productos;

        public ResultadoOptimizacion(String tipoResultado, BigDecimal gananciaTotal, 
                                     BigDecimal capitalInvertido, List<ProductoSimulacionDTO> productos) {
            this.tipoResultado = tipoResultado;
            this.gananciaTotal = gananciaTotal;
            this.capitalInvertido = capitalInvertido;
            this.productos = productos;
        }

        public String getTipoResultado() { return tipoResultado; }
        public BigDecimal getGananciaTotal() { return gananciaTotal; }
        public BigDecimal getCapitalInvertido() { return capitalInvertido; }
        public List<ProductoSimulacionDTO> getProductos() { return productos; }
    }
}

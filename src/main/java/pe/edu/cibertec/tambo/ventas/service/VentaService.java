package pe.edu.cibertec.tambo.ventas.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.tambo.ventas.client.CatalogoClient;
import pe.edu.cibertec.tambo.ventas.dto.DetalleVentaResponse;
import pe.edu.cibertec.tambo.ventas.dto.ProductoCatalogo;
import pe.edu.cibertec.tambo.ventas.dto.VentaRequest;
import pe.edu.cibertec.tambo.ventas.dto.VentaResponse;
import pe.edu.cibertec.tambo.ventas.entity.Cliente;
import pe.edu.cibertec.tambo.ventas.entity.DetalleVenta;
import pe.edu.cibertec.tambo.ventas.entity.EstadoVenta;
import pe.edu.cibertec.tambo.ventas.entity.Venta;
import pe.edu.cibertec.tambo.ventas.exception.RecursoNoEncontradoException;
import pe.edu.cibertec.tambo.ventas.exception.StockInsuficienteException;
import pe.edu.cibertec.tambo.ventas.repository.ClienteRepository;
import pe.edu.cibertec.tambo.ventas.repository.VentaRepository;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final CatalogoClient catalogoClient;

    public VentaResponse registrar(VentaRequest request, String username, String token) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        request.items().forEach(i -> cantidades.merge(i.productoId(), i.cantidad(), Integer::sum));

        Venta venta = Venta.builder()
                .fechaHora(LocalDateTime.now())
                .estado(EstadoVenta.REGISTRADA)
                .cliente(cliente)
                .cajero(username)
                .build();

        cantidades.forEach((productoId, cantidad) -> {
            ProductoCatalogo producto = catalogoClient.obtenerProducto(productoId, token);
            if (!Boolean.TRUE.equals(producto.activo())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "El producto " + producto.nombre() + " no está disponible");
            }
            if (cantidad > producto.stock()) {
                throw new StockInsuficienteException("Stock insuficiente para " + producto.nombre()
                        + " (disponible: " + producto.stock() + ")");
            }
            venta.getDetalles().add(DetalleVenta.builder()
                    .venta(venta)
                    .productoId(producto.id())
                    .productoNombre(producto.nombre())
                    .cantidad(cantidad)
                    .precioUnitario(producto.precio())
                    .subtotal(producto.precio().multiply(BigDecimal.valueOf(cantidad)))
                    .build());
        });

        venta.setTotal(venta.getDetalles().stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return aResponse(ventaRepository.save(venta));
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> listar() {
        return ventaRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream().map(this::aResponse).toList();
    }

    @Transactional(readOnly = true)
    public VentaResponse obtener(Long id) {
        return aResponse(buscar(id));
    }

    @Transactional
    public void marcarProcesada(Long ventaId) {
        buscar(ventaId).setEstado(EstadoVenta.PROCESADA);
    }

    private Venta buscar(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada"));
    }

    private VentaResponse aResponse(Venta v) {
        List<DetalleVentaResponse> detalles = v.getDetalles().stream()
                .map(d -> new DetalleVentaResponse(d.getProductoId(), d.getProductoNombre(), d.getCantidad(),
                        d.getPrecioUnitario(), d.getSubtotal()))
                .toList();
        return new VentaResponse(v.getId(), v.getFechaHora(), v.getCliente().getNombres(), v.getCajero(),
                v.getTotal(), v.getEstado().name(), detalles);
    }
}

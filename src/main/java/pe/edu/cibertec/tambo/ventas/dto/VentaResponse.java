package pe.edu.cibertec.tambo.ventas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResponse(
        Long id,
        LocalDateTime fechaHora,
        String clienteNombres,
        String cajero,
        BigDecimal total,
        String estado,
        List<DetalleVentaResponse> detalles) {
}

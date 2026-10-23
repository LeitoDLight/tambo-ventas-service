package pe.edu.cibertec.tambo.ventas.dto;

import java.math.BigDecimal;

public record DetalleVentaResponse(
        Long productoId,
        String productoNombre,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal) {
}

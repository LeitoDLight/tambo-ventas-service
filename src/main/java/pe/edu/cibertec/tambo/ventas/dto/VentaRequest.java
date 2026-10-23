package pe.edu.cibertec.tambo.ventas.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record VentaRequest(
        @NotNull(message = "El cliente es obligatorio") Long clienteId,
        @NotEmpty(message = "La venta debe tener al menos un producto") @Valid List<ItemVentaRequest> items) {
}

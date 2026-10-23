package pe.edu.cibertec.tambo.ventas.dto;

import java.math.BigDecimal;

public record ProductoCatalogo(Long id, String nombre, BigDecimal precio, Integer stock, Boolean activo) {
}

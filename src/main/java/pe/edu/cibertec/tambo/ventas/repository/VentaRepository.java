package pe.edu.cibertec.tambo.ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.tambo.ventas.entity.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {
}

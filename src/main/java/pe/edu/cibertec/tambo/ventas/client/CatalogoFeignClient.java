package pe.edu.cibertec.tambo.ventas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import pe.edu.cibertec.tambo.ventas.dto.ProductoCatalogo;

/**
 * Cliente Feign hacia catalogo-service (comunicación SÍNCRONA entre microservicios).
 * Solo se declara la interfaz: Feign genera el código que hace la petición HTTP.
 * - name: nombre lógico del cliente (se usa también en la configuración de timeouts).
 * - url: dirección del servicio, tomada de application.yml (en Docker llega por variable de entorno).
 * - configuration: reglas propias de este cliente (reintentos).
 */
@FeignClient(name = "catalogo-service",
        url = "${app.catalogo.url}",
        configuration = CatalogoFeignConfig.class)
public interface CatalogoFeignClient {

    // Equivale a: GET http://catalogo-service:8082/api/productos/{id} con el token JWT del cajero
    @GetMapping("/api/productos/{id}")
    ProductoCatalogo obtenerProducto(@PathVariable("id") Long id,
                                     @RequestHeader("Authorization") String authorization);
}
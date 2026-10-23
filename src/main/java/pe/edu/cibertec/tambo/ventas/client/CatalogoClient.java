package pe.edu.cibertec.tambo.ventas.client;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.tambo.ventas.dto.ProductoCatalogo;
import pe.edu.cibertec.tambo.ventas.exception.RecursoNoEncontradoException;

/**
 * Punto único de acceso a catalogo-service desde ventas.
 * Combina tres patrones:
 *  1) Feign Client      -> hace la llamada HTTP (CatalogoFeignClient).
 *  2) Circuit Breaker   -> si catálogo falla repetidamente, corta las llamadas (ResilienceConfig).
 *  3) Fallback          -> respuesta controlada cuando la llamada falla o el circuito está abierto.
 */
@Component
@RequiredArgsConstructor
public class CatalogoClient {

    private final CatalogoFeignClient feign;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public ProductoCatalogo obtenerProducto(Long id, String token) {
        // Se obtiene el circuito llamado "catalogo" (configurado en ResilienceConfig)
        CircuitBreaker circuito = circuitBreakerFactory.create("catalogo");
        // run(llamada normal, qué hacer si falla)
        return circuito.run(
                () -> llamar(id, token),
                error -> fallback(id, error));
    }

    // Llamada real a catálogo a través de Feign
    private ProductoCatalogo llamar(Long id, String token) {
        try {
            return feign.obtenerProducto(id, "Bearer " + token);
        } catch (FeignException.NotFound e) {
            // 404 de catálogo: el producto no existe (error de negocio, no falla del servicio)
            throw new RecursoNoEncontradoException("El producto " + id + " no existe");
        }
    }

    // FALLBACK: se ejecuta cuando la llamada falló (caída, timeout) o el circuito está ABIERTO
    private ProductoCatalogo fallback(Long id, Throwable error) {
        // Los errores de negocio (producto inexistente) se devuelven tal cual, sin convertirlos en 503
        for (Throwable t = error; t != null; t = t.getCause()) {
            if (t instanceof RecursoNoEncontradoException negocio) {
                throw negocio;
            }
        }
        // Circuito abierto: se rechaza al instante, sin siquiera intentar llamar
        if (error instanceof CallNotPermittedException) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Catálogo temporalmente no disponible (circuito abierto). Intente en unos segundos");
        }
        // Cualquier otra falla: respuesta controlada en lugar de un error genérico
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Catálogo no disponible");
    }
}
package pe.edu.cibertec.tambo.ventas.config;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import pe.edu.cibertec.tambo.ventas.exception.RecursoNoEncontradoException;

/**
 * Configuración del CIRCUIT BREAKER (Resilience4J) para las llamadas a catálogo.

 * Estados del circuito:
 *  - CERRADO: todo normal, las llamadas pasan.
 *  - ABIERTO: catálogo está fallando; se rechazan las llamadas al instante (sin esperar)
 *             para no saturar el sistema ni dejar al cajero esperando.
 *  - SEMIABIERTO: pasado un tiempo se dejan pasar pocas llamadas de prueba; si funcionan, el circuito se cierra.
 */
@Configuration
public class ResilienceConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> catalogoCircuitBreaker() {
        return factory -> factory.configure(builder -> builder
                        .circuitBreakerConfig(CircuitBreakerConfig.custom()
                                // Se evalúan las últimas 6 llamadas...
                                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                                .slidingWindowSize(6)
                                // ...pero solo se decide cuando ya hubo al menos 4
                                .minimumNumberOfCalls(4)
                                // Si fallan 50% o más, el circuito se ABRE
                                .failureRateThreshold(50)
                                // Permanece abierto 10 segundos antes de probar otra vez
                                .waitDurationInOpenState(Duration.ofSeconds(10))
                                // En SEMIABIERTO se permiten 2 llamadas de prueba
                                .permittedNumberOfCallsInHalfOpenState(2)
                                // Un producto que no existe (404) es un error del cliente, NO una falla del servicio:
                                // no debe contar para abrir el circuito
                                .ignoreExceptions(RecursoNoEncontradoException.class)
                                .build())
                        // TIMEOUT: si catálogo no responde en 4 segundos, se considera fallo
                        .timeLimiterConfig(TimeLimiterConfig.custom()
                                .timeoutDuration(Duration.ofSeconds(4))
                                .build()),
                "catalogo");
    }
}
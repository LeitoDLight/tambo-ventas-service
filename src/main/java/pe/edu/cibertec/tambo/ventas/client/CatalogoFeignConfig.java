package pe.edu.cibertec.tambo.ventas.client;

import org.springframework.context.annotation.Bean;

import feign.Retryer;

/**
 * Configuración propia del cliente Feign de catálogo.
 * No lleva @Configuration a propósito: solo se aplica al cliente que la referencia.
 */
public class CatalogoFeignConfig {

    /**
     * REINTENTOS: si la llamada falla por un problema de red (por ejemplo, el servicio aún está arrancando),
     * Feign vuelve a intentar hasta 3 veces en total, esperando 200 ms la primera vez
     * y aumentando la espera en cada intento (máximo 1 segundo).
     */
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(200, 1000, 3);
    }
}
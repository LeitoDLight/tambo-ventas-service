package pe.edu.cibertec.tambo.ventas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
// Activa Feign: Spring busca las interfaces marcadas con @FeignClient y crea su implementación automáticamente
@EnableFeignClients
public class VentasServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VentasServiceApplication.class, args);
    }

}
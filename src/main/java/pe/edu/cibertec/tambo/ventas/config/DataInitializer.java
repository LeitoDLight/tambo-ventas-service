package pe.edu.cibertec.tambo.ventas.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.tambo.ventas.entity.Cliente;
import pe.edu.cibertec.tambo.ventas.repository.ClienteRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ClienteRepository clienteRepository;

    @Override
    public void run(String... args) {
        if (clienteRepository.count() > 0) {
            return;
        }
        clienteRepository.save(Cliente.builder()
                .dni("00000000")
                .nombres("CLIENTE VARIOS")
                .build());
    }
}

package pe.edu.cibertec.tambo.ventas.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.tambo.ventas.dto.ClienteRequest;
import pe.edu.cibertec.tambo.ventas.dto.ClienteResponse;
import pe.edu.cibertec.tambo.ventas.entity.Cliente;
import pe.edu.cibertec.tambo.ventas.exception.RecursoNoEncontradoException;
import pe.edu.cibertec.tambo.ventas.repository.ClienteRepository;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll(Sort.by("nombres")).stream().map(this::aResponse).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorDni(String dni) {
        return clienteRepository.findByDni(dni)
                .map(this::aResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un cliente con DNI " + dni));
    }

    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        if (clienteRepository.existsByDni(request.dni())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un cliente con DNI " + request.dni());
        }
        Cliente cliente = Cliente.builder()
                .dni(request.dni())
                .nombres(request.nombres().trim())
                .telefono(request.telefono())
                .build();
        return aResponse(clienteRepository.save(cliente));
    }

    private ClienteResponse aResponse(Cliente c) {
        return new ClienteResponse(c.getId(), c.getDni(), c.getNombres(), c.getTelefono());
    }
}

package pe.edu.cibertec.tambo.ventas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.tambo.ventas.dto.VentaRequest;
import pe.edu.cibertec.tambo.ventas.dto.VentaResponse;
import pe.edu.cibertec.tambo.ventas.messaging.VentaProducer;
import pe.edu.cibertec.tambo.ventas.service.VentaService;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;
    private final VentaProducer ventaProducer;

    @PostMapping
    public ResponseEntity<VentaResponse> registrar(@Valid @RequestBody VentaRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        VentaResponse venta = ventaService.registrar(request, jwt.getSubject(), jwt.getTokenValue());
        ventaProducer.enviar(venta);
        return ResponseEntity.status(HttpStatus.CREATED).body(venta);
    }

    @GetMapping
    public ResponseEntity<List<VentaResponse>> listar() {
        return ResponseEntity.ok(ventaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.obtener(id));
    }
}

package pe.edu.cibertec.tambo.ventas.messaging;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.tambo.ventas.config.RabbitConfig;
import pe.edu.cibertec.tambo.ventas.dto.VentaResponse;

@Component
@RequiredArgsConstructor
public class VentaProducer {

    private static final Logger log = LoggerFactory.getLogger(VentaProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public void enviar(VentaResponse venta) {
        String items = venta.detalles().stream()
                .map(d -> d.productoId() + ":" + d.cantidad())
                .collect(Collectors.joining(","));
        String mensaje = venta.id() + "|" + items;
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.RK_REGISTRADA, mensaje);
        log.info("Publicado {}: {}", RabbitConfig.RK_REGISTRADA, mensaje);
    }
}

package pe.edu.cibertec.tambo.ventas.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.tambo.ventas.config.RabbitConfig;
import pe.edu.cibertec.tambo.ventas.service.VentaService;

@Component
@RequiredArgsConstructor
public class VentaProcesadaListener {

    private static final Logger log = LoggerFactory.getLogger(VentaProcesadaListener.class);

    private final VentaService ventaService;

    @RabbitListener(queues = RabbitConfig.Q_PROCESADA)
    public void procesar(String mensaje) {
        log.info("Recibido {}: {}", RabbitConfig.RK_PROCESADA, mensaje);
        ventaService.marcarProcesada(Long.valueOf(mensaje.trim()));
    }
}

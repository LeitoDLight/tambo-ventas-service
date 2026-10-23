package pe.edu.cibertec.tambo.ventas.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "tambo.exchange";
    public static final String Q_REGISTRADA = "catalogo.venta.registrada";
    public static final String Q_PROCESADA = "ventas.venta.procesada";
    public static final String Q_STOCK_BAJO = "alertas.stock.bajo";
    public static final String RK_REGISTRADA = "venta.registrada";
    public static final String RK_PROCESADA = "venta.procesada";
    public static final String RK_STOCK_BAJO = "stock.bajo";

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue qRegistrada() {
        return new Queue(Q_REGISTRADA, true);
    }

    @Bean
    Queue qProcesada() {
        return new Queue(Q_PROCESADA, true);
    }

    @Bean
    Queue qStockBajo() {
        return new Queue(Q_STOCK_BAJO, true);
    }

    @Bean
    Binding bRegistrada() {
        return BindingBuilder.bind(qRegistrada()).to(exchange()).with(RK_REGISTRADA);
    }

    @Bean
    Binding bProcesada() {
        return BindingBuilder.bind(qProcesada()).to(exchange()).with(RK_PROCESADA);
    }

    @Bean
    Binding bStockBajo() {
        return BindingBuilder.bind(qStockBajo()).to(exchange()).with(RK_STOCK_BAJO);
    }
}

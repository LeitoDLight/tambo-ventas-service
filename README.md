# tambo-ventas-service

Microservicio de **clientes y ventas** de Tambo Market.

| Dato | Valor |
|---|---|
| Puerto | 8083 |
| Tecnología | Java 21, Spring Boot 4.1.1, Maven, PostgreSQL 17, Spring Data JPA, OpenFeign, Resilience4J, RabbitMQ |
| Responsable | Alberto Muzaurieta Hidalgo (integración Feign/Resilience4J: Leonardo Dorregaray) |

## API REST

| Método y ruta | Acceso |
|---|---|
| `GET /api/clientes`, `GET /api/clientes/dni/{dni}`, `POST /api/clientes` | Usuario autenticado |
| `POST /api/ventas`, `GET /api/ventas`, `GET /api/ventas/{id}` | Usuario autenticado |

El cliente `CLIENTE VARIOS` (DNI `00000000`) se crea automáticamente para ventas sin identificar.

## Comunicación entre servicios

**Síncrona (Feign + circuit breaker).** Antes de registrar una venta, consulta el producto a `catalogo-service` con la interfaz declarativa `CatalogoFeignClient`, con tiempos de espera y reintentos. Si catálogo no responde, el circuit breaker (Resilience4J) abre el circuito y la venta falla rápido con un `503` claro; el circuito se recupera solo.

**Asíncrona (RabbitMQ).**

1. Valida cliente, producto y stock, guarda la venta como `REGISTRADA` y publica `venta.registrada`. Responde `201` sin esperar al resto.
2. Cuando catálogo termina de descontar stock, llega `venta.procesada` y la venta pasa a `PROCESADA`.

## Estructura

```
client/       CatalogoClient, CatalogoFeignClient, CatalogoFeignConfig
config/       RabbitConfig, ResilienceConfig, SecurityConfig, DataInitializer
controller/   ClienteController, VentaController
entity/       Cliente, Venta, DetalleVenta, EstadoVenta
messaging/    VentaProducer, VentaProcesadaListener
service/      ClienteService, VentaService
exception/    manejo global de errores
```

## Proyecto completo

Este repositorio es **un microservicio de Tambo Market**. El proyecto completo (todos los servicios, `docker-compose.yml`, colección de Postman, informe y el historial de trabajo del equipo) está en el repositorio de integración, que es privado y tiene acceso concedido al docente:

https://github.com/LeitoDLight/tambo-market

Para levantar todo el sistema:

```
git clone https://github.com/LeitoDLight/tambo-market.git
cd tambo-market
docker compose up -d --build
```

Luego abrir http://localhost (usuario `admin` / `Admin123`).

## Equipo

Leonardo Fabricio Dorregaray Guevara · José Miguel Yataco Quispe · Alberto Muzaurieta Hidalgo · Joselin Carhuapoma Poma

Curso: Desarrollo de Aplicaciones Web II — Cibertec.
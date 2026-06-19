# AndesFin · Microservicio de Microinversiones

Evaluación Conjunta del Segundo Parcial — Arquitectura de Software (ESPE).
Plataforma de microinversiones que permite consultar **usuarios**, consultar
**productos** de inversión, **simular** inversiones óptimas y consultar
simulaciones previas por usuario.

## Reparto del trabajo

| Persona | Módulo |
|---------|--------|
| **A** | Usuarios + Productos |
| **B** (Josue Zambrano) | Simulación (lógica de optimización) |

Un único microservicio Spring Boot con estructura **por capas**.

## Tecnologías

- Java 17 · Spring Boot 3.5.9
- Spring Web · Spring Data JPA (ORM Hibernate) · Bean Validation · Lombok
- PostgreSQL 16 · UUID como identificador primario
- Docker / docker-compose

## Estructura

```
backend/andesfin/
├── Dockerfile
├── db/init.sql                 # esquema + datos (5 usuarios, 8 productos)
├── pom.xml
└── src/main/java/ec/edu/espe/andesfin/
    ├── AndesfinApplication.java
    ├── config/        CorsConfig
    ├── controller/    UsuarioController · ProductoController · SimulacionController
    ├── dto/           UsuarioDTO · ProductoDTO · Simulacion{Request,Response}DTO · ...
    ├── entity/        Usuario · ProductoFinanciero · Simulacion · SimulacionProducto
    ├── repository/    UsuarioRepository · ProductoRepository · SimulacionRepository
    ├── services/      UsuarioService · ProductoService · SimulacionService
    └── exception/     FondosInsuficientesException · GlobalExceptionHandler
docker-compose.yml              # postgres + app (en la raíz)
```

## Cómo ejecutar

### Opción A — todo con Docker (recomendado)
```bash
docker compose up --build
```
Levanta PostgreSQL (cargando `db/init.sql`) y la app en `http://localhost:8080`.

### Opción B — BD en Docker, app local
```bash
docker compose up -d postgres
cd backend/andesfin
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

## Configuración (variables de entorno)

| Variable | Default |
|----------|---------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/andesfin` |
| `SPRING_DATASOURCE_USERNAME` | `andesfin` |
| `SPRING_DATASOURCE_PASSWORD` | `andesfin123` |
| `SERVER_PORT` | `8080` |

`ddl-auto: validate` → Hibernate **no** crea tablas; valida contra `init.sql`.

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/usuarios` | Lista todos los usuarios |
| GET | `/productos` | Lista solo productos **activos** |
| POST | `/simulaciones` | Ejecuta y persiste una simulación |
| GET | `/simulaciones/{usuarioId}` | Simulaciones previas de un usuario |

### `GET /usuarios`
```json
[{ "id": "uuid", "nombre": "Ana Maria Perez", "email": "ana.perez@andesfin.com", "capital_disponible": 3000.00 }]
```

### `GET /productos`
```json
[{ "id": "uuid", "nombre": "ETF Global", "descripcion": "...", "costo": 1500.00, "porcentaje_retorno": 12.00, "activo": true }]
```

### `POST /simulaciones`
Request (productos dinámicos enviados por el cliente):
```json
{
  "usuario_id": "uuid-del-usuario",
  "capital_disponible": 3000.00,
  "productos": [
    { "nombre": "Fondo Acciones Tech", "precio": 1000.00, "porcentaje_ganancia": 8.50 },
    { "nombre": "ETF Global",          "precio": 1500.00, "porcentaje_ganancia": 12.00 },
    { "nombre": "Bonos Corporativos AAA", "precio": 500.00, "porcentaje_ganancia": 5.25 }
  ]
}
```

Proceso de cálculo (ganancia = precio × % / 100; selección *greedy* por mayor retorno que quepa en el capital):

| Producto | Precio | % Ganancia | Ganancia |
|----------|-------:|-----------:|---------:|
| ETF Global | 1500.00 | 12.00 | 180.00 |
| Fondo Acciones Tech | 1000.00 | 8.50 | 85.00 |
| Bonos Corporativos AAA | 500.00 | 5.25 | 26.25 |

Con capital 3000 entran ETF + Tech (2500). Response:
```json
{
  "id": "uuid",
  "usuario_id": "uuid",
  "fecha_simulacion": "2026-06-19T10:30:00",
  "capital_disponible": 3000.00,
  "productos_seleccionados": [
    { "nombre": "ETF Global", "precio": 1500.00, "porcentaje_ganancia": 12.00, "ganancia_esperada": 180.00 },
    { "nombre": "Fondo Acciones Tech", "precio": 1000.00, "porcentaje_ganancia": 8.50, "ganancia_esperada": 85.00 }
  ],
  "costo_total": 2500.00,
  "capital_restante": 500.00,
  "ganancia_total": 265.00,
  "retorno_total_porcentaje": 10.60,
  "eficiencia_capital": 83.33,
  "cantidad_productos": 2,
  "mensaje": "Simulacion exitosa con ganancias optimas."
}
```

Caso **fondos insuficientes** (capital < producto más barato) → HTTP 400:
```json
{
  "error": "Fondos insuficientes",
  "detalle": "El capital disponible ($500.00) es insuficiente para adquirir cualquier producto de la lista.",
  "capital_disponible": 500.00,
  "producto_mas_barato": 1200.00,
  "diferencia_necesaria": 700.00,
  "recomendacion": "Aumente su capital o consulte productos con menor inversion minima."
}
```

### `GET /simulaciones/{usuarioId}`
Devuelve la lista de simulaciones del usuario (mismas métricas que la respuesta del POST).

## Patrones aplicados

- **DTO Pattern** — los controllers exponen DTOs, nunca entidades.
- **Repository Pattern** — Spring Data JPA.
- **Service Pattern** — lógica de negocio en los services, controllers delgados.

## Notas

- `init.sql` corre **solo en el primer arranque** del contenedor. Para recargar:
  `docker compose down -v && docker compose up --build`.
- El test `contextLoads` requiere PostgreSQL levantado (por `ddl-auto: validate`).

# Andesfin - Plataforma de Simulación Financiera

Plataforma de simulación financiera para optimización de inversiones.

## Tecnologías

- Java 17
- Spring Boot 3.5.9
- PostgreSQL 15
- Maven
- Docker

## Estructura del Proyecto

```
oauth/src/main/java/com/example/oauth_server/
├── OauthServerApplication.java       ← Aplicación principal
├── config/                           ← Configuraciones
│   └── CorsConfig.java
├── usuario/          ← PERSONA A
│   ├── Usuario.java                  (entidad)
│   ├── UsuarioRepository.java
│   ├── UsuarioService.java
│   ├── UsuarioController.java
│   └── dto/UsuarioDTO.java
├── producto/         ← PERSONA A
│   ├── ProductoFinanciero.java       (entidad)
│   ├── ProductoRepository.java
│   ├── ProductoService.java
│   ├── ProductoController.java
│   └── dto/ProductoDTO.java
└── simulacion/       ← PERSONA B
    ├── Simulacion.java               (entidad)
    ├── SimulacionProducto.java       (detalle)
    ├── SimulacionRepository.java
    ├── SimulacionService.java        (lógica de optimización)
    ├── SimulacionController.java
    └── dto/
        ├── SimulacionRequestDTO.java
        ├── ProductoSimulacionDTO.java
        └── SimulacionResponseDTO.java
```

## Instalación y Ejecución

### Prerrequisitos

- Docker y Docker Compose instalados
- Java 17 (para desarrollo local)

### Ejecución con Docker

```bash
cd oauth
docker-compose up -d
```

La aplicación estará disponible en `http://localhost:8080`

### Ejecución Local

```bash
cd oauth
mvn spring-boot:run
```

## Módulo de Simulación (Persona B)

### Descripción

El módulo de simulación permite optimizar inversiones evaluando combinaciones de productos financieros para maximizar la ganancia respetando el capital disponible.

### Endpoints

#### POST /simulaciones

Crea una nueva simulación de inversión optimizada.

**Request Body:**
```json
{
  "usuarioId": 1,
  "capitalDisponible": 10000.00,
  "productosIds": [1, 2, 3, 4, 5]
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "usuarioId": 1,
  "capitalDisponible": 10000.00,
  "gananciaTotal": 1850.00,
  "capitalInvertido": 10000.00,
  "tipoResultado": "CAPITAL_COMPLETO",
  "fechaCreacion": "2024-01-15T10:30:00",
  "productos": [
    {
      "productoId": 5,
      "nombreProducto": "Crypto ETF",
      "montoInvertido": 3000.00,
      "tasaRetorno": 20.00,
      "gananciaEstimada": 600.00,
      "riesgo": "ALTO"
    },
    {
      "productoId": 3,
      "nombreProducto": "Acciones Tech",
      "montoInvertido": 2000.00,
      "tasaRetorno": 15.50,
      "gananciaEstimada": 310.00,
      "riesgo": "ALTO"
    },
    {
      "productoId": 2,
      "nombreProducto": "Fondo Inversión",
      "montoInvertido": 5000.00,
      "tasaRetorno": 12.00,
      "gananciaEstimada": 600.00,
      "riesgo": "MEDIO"
    }
  ]
}
```

#### GET /simulaciones/{usuarioId}

Obtiene todas las simulaciones de un usuario específico.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "usuarioId": 1,
    "capitalDisponible": 10000.00,
    "gananciaTotal": 1850.00,
    "capitalInvertido": 10000.00,
    "tipoResultado": "CAPITAL_COMPLETO",
    "fechaCreacion": "2024-01-15T10:30:00",
    "productos": [...]
  },
  {
    "id": 2,
    "usuarioId": 1,
    "capitalDisponible": 5000.00,
    "gananciaTotal": 600.00,
    "capitalInvertido": 5000.00,
    "tipoResultado": "OPTIMO",
    "fechaCreacion": "2024-01-15T11:00:00",
    "productos": [...]
  }
]
```

### Tipos de Resultado

El algoritmo de optimización maneja 4 casos:

#### 1. OPTIMO
Se seleccionan múltiples productos ordenados por tasa de retorno descendente, maximizando la ganancia con el capital disponible.

**Ejemplo:**
- Capital disponible: $8,000
- Productos seleccionados: Crypto ETF ($3,000), Acciones Tech ($2,000), Fondo Inversión ($5,000) - Solo $3,000 disponibles para Fondo
- Resultado: Inversión en Crypto ETF + Acciones Tech + parcial de Fondo

#### 2. MINIMO
Solo se puede invertir en un producto debido al capital limitado.

**Ejemplo:**
- Capital disponible: $1,500
- Producto seleccionado: CDT Básico ($1,000)
- Resultado: Inversión en un solo producto

#### 3. FONDOS_INSUFICIENTES
El capital disponible es menor al monto mínimo de cualquier producto.

**Ejemplo:**
- Capital disponible: $500
- Monto mínimo más bajo: $1,000 (CDT Básico)
- Resultado: No se puede realizar inversión

#### 4. CAPITAL_COMPLETO
El capital es suficiente para invertir en todos los productos disponibles.

**Ejemplo:**
- Capital disponible: $25,000
- Total requerido: $21,000 (suma de todos los mínimos)
- Resultado: Inversión en todos los productos

### Tablas de Cálculo

#### Productos Financieros Disponibles

| ID | Nombre | Monto Mínimo | Tasa Retorno | Riesgo |
|----|--------|--------------|--------------|--------|
| 1 | CDT Básico | $1,000 | 8.50% | BAJO |
| 2 | Fondo Inversión | $5,000 | 12.00% | MEDIO |
| 3 | Acciones Tech | $2,000 | 15.50% | ALTO |
| 4 | Bonos Gobierno | $10,000 | 6.00% | BAJO |
| 5 | Crypto ETF | $3,000 | 20.00% | ALTO |

#### Ejemplos de Cálculo

**Caso 1: Capital $8,000 (OPTIMO)**
- Crypto ETF: $3,000 × 20% = $600 ganancia
- Acciones Tech: $2,000 × 15.5% = $310 ganancia
- Fondo Inversión: $3,000 × 12% = $360 ganancia
- **Total invertido: $8,000**
- **Ganancia total: $1,270**

**Caso 2: Capital $1,500 (MINIMO)**
- CDT Básico: $1,000 × 8.5% = $85 ganancia
- **Total invertido: $1,000**
- **Ganancia total: $85**

**Caso 3: Capital $500 (FONDOS_INSUFICIENTES)**
- No se puede invertir en ningún producto
- **Total invertido: $0**
- **Ganancia total: $0**

**Caso 4: Capital $25,000 (CAPITAL_COMPLETO)**
- Todos los productos: $21,000 inversión
- Ganancia: $1,850
- **Total invertido: $21,000**
- **Ganancia total: $1,850**

### Algoritmo de Optimización

El servicio de simulación utiliza un algoritmo greedy que:

1. Ordena los productos por tasa de retorno descendente
2. Selecciona productos mientras haya capital disponible
3. Calcula la ganancia estimada para cada producto
4. Determina el tipo de resultado según el capital utilizado
5. Retorna la combinación óptima de productos

## Base de Datos

### Tablas

- `usuarios`: Información de usuarios (Persona A)
- `productos_financieros`: Catálogo de productos (Persona A)
- `simulaciones`: Historial de simulaciones (Persona B)
- `simulacion_productos`: Detalles de productos en simulación (Persona B)

### Configuración

- Host: localhost:5432
- Base de datos: andesfin
- Usuario: andesfin
- Contraseña: andesfin123

## Contribución

Este proyecto es desarrollado por:
- **Kennet Cortez**: Módulos de Usuario y Producto
- **Josue Zambrano**: Módulo de Simulación (optimización financiera)

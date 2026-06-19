-- =====================================================================
-- AndesFin - Script de inicializacion (PostgreSQL / Docker)
-- Se ejecuta automaticamente en el primer arranque del contenedor.
--
-- IMPORTANTE: la app corre con ddl-auto: validate, asi que este esquema
-- DEBE coincidir EXACTAMENTE con las entidades JPA. UUID como PK (PDF 3.6.1).
-- =====================================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;  -- gen_random_uuid()

-- ---------------------------------------------------------------------
-- Usuario (Persona A)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id                 UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre             VARCHAR(255)    NOT NULL,
    email              VARCHAR(255)    NOT NULL UNIQUE,
    capital_disponible NUMERIC(10, 2)
);

-- ---------------------------------------------------------------------
-- ProductoFinanciero (Persona A)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS productos (
    id                 UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre             VARCHAR(255)    NOT NULL,
    descripcion        TEXT,
    costo              NUMERIC(10, 2),
    porcentaje_retorno NUMERIC(5, 2),
    activo             BOOLEAN         NOT NULL
);

-- ---------------------------------------------------------------------
-- Simulacion (Persona B) - cabecera
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS simulaciones (
    id                 UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id         UUID            NOT NULL,
    fecha_simulacion   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    capital_disponible NUMERIC(10, 2)  NOT NULL,
    ganancia_total     NUMERIC(10, 2),
    CONSTRAINT fk_simulacion_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- ---------------------------------------------------------------------
-- Simulacion (Persona B) - detalle (productos seleccionados, snapshot)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS simulacion_productos (
    id                  UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    simulacion_id       UUID           NOT NULL,
    nombre              VARCHAR(255)   NOT NULL,
    precio              NUMERIC(10, 2) NOT NULL,
    porcentaje_ganancia NUMERIC(5, 2),
    ganancia_esperada   NUMERIC(10, 2),
    CONSTRAINT fk_simprod_simulacion FOREIGN KEY (simulacion_id)
        REFERENCES simulaciones(id) ON DELETE CASCADE
);

-- =====================================================================
-- Seed: 5 usuarios (con capital_disponible)
-- =====================================================================
INSERT INTO usuarios (nombre, email, capital_disponible) VALUES
    ('Ana Maria Perez',   'ana.perez@andesfin.com',     3000.00),
    ('Bruno Carrasco',    'bruno.carrasco@andesfin.com', 1000.00),
    ('Carla Mendoza',     'carla.mendoza@andesfin.com',  4000.00),
    ('Diego Salazar',     'diego.salazar@andesfin.com',   500.00),
    ('Elena Vaca',        'elena.vaca@andesfin.com',     8500.00)
ON CONFLICT (email) DO NOTHING;

-- =====================================================================
-- Seed: 8 productos financieros (6 activos, 2 inactivos)
-- =====================================================================
INSERT INTO productos (nombre, descripcion, costo, porcentaje_retorno, activo) VALUES
    ('Fondo Acciones Tech',     'Fondo de inversion en acciones tecnologicas.',          1000.00,  8.50, TRUE),
    ('Bonos Corporativos AAA',  'Bonos corporativos de alta calificacion.',               500.00,  5.25, TRUE),
    ('ETF Global',              'ETF diversificado de mercados globales.',               1500.00, 12.00, TRUE),
    ('Fondo de Dividendos',     'Fondo enfocado en empresas con dividendos estables.',    800.00,  6.75, TRUE),
    ('Bonos del Tesoro',        'Bonos del gobierno con bajo riesgo.',                   1200.00,  4.50, TRUE),
    ('Crypto Index Andes',      'Indice cripto de alto potencial y alto riesgo.',         400.00, 20.00, TRUE),
    ('Fondo Inmobiliario Quito','Fondo inmobiliario descontinuado, ya no admite aportes.', 900.00,  8.75, FALSE),
    ('Acciones Blue Chip',      'Cartera de acciones lideres, temporalmente cerrada.',    1200.00,  9.50, FALSE)
ON CONFLICT DO NOTHING;

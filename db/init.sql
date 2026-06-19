-- Base de datos Andesfin - Script de inicialización

-- Tabla de usuarios (Persona A)
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de productos financieros (Persona A)
CREATE TABLE IF NOT EXISTS productos_financieros (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    monto_minimo DECIMAL(15,2) NOT NULL,
    tasa_retorno DECIMAL(5,2) NOT NULL,
    riesgo VARCHAR(50) NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de simulaciones (Persona B)
CREATE TABLE IF NOT EXISTS simulaciones (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    capital_disponible DECIMAL(15,2) NOT NULL,
    ganancia_total DECIMAL(15,2),
    capital_invertido DECIMAL(15,2),
    tipo_resultado VARCHAR(50),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_simulacion_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabla de detalles de simulación (Persona B)
CREATE TABLE IF NOT EXISTS simulacion_productos (
    id BIGSERIAL PRIMARY KEY,
    simulacion_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    nombre_producto VARCHAR(200),
    monto_invertido DECIMAL(15,2) NOT NULL,
    tasa_retorno DECIMAL(5,2),
    ganancia_estimada DECIMAL(15,2),
    riesgo VARCHAR(50),
    CONSTRAINT fk_simulacion_producto FOREIGN KEY (simulacion_id) REFERENCES simulaciones(id) ON DELETE CASCADE
);

-- Seed data para usuarios (Persona A)
INSERT INTO usuarios (nombre, email) VALUES
('Juan Pérez', 'juan.perez@email.com'),
('María García', 'maria.garcia@email.com'),
('Carlos Rodríguez', 'carlos.rodriguez@email.com')
ON CONFLICT (email) DO NOTHING;

-- Seed data para productos financieros (Persona A)
INSERT INTO productos_financieros (nombre, monto_minimo, tasa_retorno, riesgo, descripcion) VALUES
('CDT Básico', 1000.00, 8.50, 'BAJO', 'Certificado de Depósito a Término con retorno garantizado'),
('Fondo Inversión', 5000.00, 12.00, 'MEDIO', 'Fondo de inversión diversificado con riesgo moderado'),
('Acciones Tech', 2000.00, 15.50, 'ALTO', 'Cartera de acciones de tecnología de alto crecimiento'),
('Bonos Gobierno', 10000.00, 6.00, 'BAJO', 'Bonos del gobierno con bajo riesgo y retorno estable'),
('Crypto ETF', 3000.00, 20.00, 'ALTO', 'ETF de criptomonedas con alto potencial de retorno')
ON CONFLICT DO NOTHING;

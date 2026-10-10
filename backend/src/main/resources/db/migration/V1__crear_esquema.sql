-- V1: esquema inicial (cliente, solicitud_tarjeta, tarjeta, movimiento_tarjeta)
-- Dinero: DECIMAL(12,2). Fechas de sistema: DATETIME2 en UTC.
-- Eliminacion logica: eliminado_en (NULL = activo). Sin cascadas.

CREATE TABLE cliente (
    id                    BIGINT IDENTITY(1,1) NOT NULL,
    nombre                NVARCHAR(100) NOT NULL,
    apellido              NVARCHAR(100) NOT NULL,
    numero_identificacion VARCHAR(30)   NOT NULL,
    fecha_nacimiento      DATE          NOT NULL,
    direccion             NVARCHAR(250) NOT NULL,
    correo_electronico    NVARCHAR(150) NOT NULL,
    telefono              VARCHAR(20)   NOT NULL,
    creado_en             DATETIME2(3)  NOT NULL CONSTRAINT df_cliente_creado_en DEFAULT SYSUTCDATETIME(),
    actualizado_en        DATETIME2(3)  NOT NULL CONSTRAINT df_cliente_actualizado_en DEFAULT SYSUTCDATETIME(),
    eliminado_en          DATETIME2(3)  NULL,
    CONSTRAINT pk_cliente PRIMARY KEY (id),
    CONSTRAINT uq_cliente_identificacion UNIQUE (numero_identificacion)
);

CREATE TABLE solicitud_tarjeta (
    id                        BIGINT IDENTITY(1,1) NOT NULL,
    cliente_id                BIGINT        NOT NULL,
    tipo_tarjeta              VARCHAR(10)   NOT NULL,
    ingreso_mensual_declarado DECIMAL(12,2) NOT NULL,
    fecha_solicitud           DATETIME2(3)  NOT NULL,
    estado                    VARCHAR(10)   NOT NULL CONSTRAINT df_solicitud_estado DEFAULT 'PENDIENTE',
    fecha_decision            DATETIME2(3)  NULL,
    comentario_decision       NVARCHAR(500) NULL,
    monto_aprobado            DECIMAL(12,2) NULL,
    eliminado_en              DATETIME2(3)  NULL,
    CONSTRAINT pk_solicitud_tarjeta PRIMARY KEY (id),
    CONSTRAINT fk_solicitud_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id),
    CONSTRAINT ck_solicitud_tipo CHECK (tipo_tarjeta IN ('CLASICA', 'ORO', 'PLATINO')),
    CONSTRAINT ck_solicitud_estado CHECK (estado IN ('PENDIENTE', 'APROBADA', 'RECHAZADA')),
    CONSTRAINT ck_solicitud_ingreso CHECK (ingreso_mensual_declarado > 0),
    CONSTRAINT ck_solicitud_decision CHECK (
        (estado = 'PENDIENTE'
            AND fecha_decision IS NULL AND monto_aprobado IS NULL AND comentario_decision IS NULL)
        OR
        (estado = 'APROBADA'
            AND fecha_decision IS NOT NULL AND monto_aprobado IS NOT NULL AND monto_aprobado > 0)
        OR
        (estado = 'RECHAZADA'
            AND fecha_decision IS NOT NULL AND monto_aprobado IS NULL
            AND comentario_decision IS NOT NULL AND LEN(LTRIM(RTRIM(comentario_decision))) > 0)
    ),
    CONSTRAINT uq_solicitud_id_cliente_tipo UNIQUE (id, cliente_id, tipo_tarjeta)
);

CREATE TABLE tarjeta (
    id             BIGINT IDENTITY(1,1) NOT NULL,
    numero_tarjeta CHAR(16)      NOT NULL,
    cliente_id     BIGINT        NOT NULL,
    solicitud_id   BIGINT        NOT NULL,
    tipo_tarjeta   VARCHAR(10)   NOT NULL,
    limite_credito DECIMAL(12,2) NOT NULL,
    saldo_actual   DECIMAL(12,2) NOT NULL CONSTRAINT df_tarjeta_saldo DEFAULT 0,
    fecha_emision  DATETIME2(3)  NOT NULL,
    eliminado_en   DATETIME2(3)  NULL,
    CONSTRAINT pk_tarjeta PRIMARY KEY (id),
    CONSTRAINT uq_tarjeta_numero UNIQUE (numero_tarjeta),
    CONSTRAINT uq_tarjeta_solicitud UNIQUE (solicitud_id),
    CONSTRAINT fk_tarjeta_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id),
    CONSTRAINT fk_tarjeta_solicitud_coherente FOREIGN KEY (solicitud_id, cliente_id, tipo_tarjeta)
        REFERENCES solicitud_tarjeta (id, cliente_id, tipo_tarjeta),
    CONSTRAINT ck_tarjeta_numero_digitos CHECK (numero_tarjeta NOT LIKE '%[^0-9]%'),
    CONSTRAINT ck_tarjeta_tipo CHECK (tipo_tarjeta IN ('CLASICA', 'ORO', 'PLATINO')),
    CONSTRAINT ck_tarjeta_limite CHECK (limite_credito > 0),
    CONSTRAINT ck_tarjeta_saldo CHECK (saldo_actual >= 0 AND saldo_actual <= limite_credito)
);

CREATE TABLE movimiento_tarjeta (
    id               BIGINT IDENTITY(1,1) NOT NULL,
    tarjeta_id       BIGINT        NOT NULL,
    tipo_movimiento  VARCHAR(10)   NOT NULL,
    monto            DECIMAL(12,2) NOT NULL,
    fecha_movimiento DATE          NOT NULL,
    descripcion      NVARCHAR(200) NOT NULL,
    creado_en        DATETIME2(3)  NOT NULL CONSTRAINT df_movimiento_creado_en DEFAULT SYSUTCDATETIME(),
    CONSTRAINT pk_movimiento_tarjeta PRIMARY KEY (id),
    CONSTRAINT fk_movimiento_tarjeta FOREIGN KEY (tarjeta_id) REFERENCES tarjeta (id),
    CONSTRAINT ck_movimiento_tipo CHECK (tipo_movimiento IN ('CONSUMO', 'PAGO')),
    CONSTRAINT ck_movimiento_monto CHECK (monto > 0)
);

CREATE INDEX ix_solicitud_cliente ON solicitud_tarjeta (cliente_id);
CREATE INDEX ix_tarjeta_cliente ON tarjeta (cliente_id);
CREATE INDEX ix_movimiento_tarjeta ON movimiento_tarjeta (tarjeta_id, fecha_movimiento, id);
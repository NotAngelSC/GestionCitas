-- 1. Tabla: usuarios
CREATE TABLE usuarios (
    id_usuario   INT IDENTITY(1,1) PRIMARY KEY,
    nombre       VARCHAR(100)   NOT NULL,
    usuario      VARCHAR(50)    NOT NULL UNIQUE,
    contrasena   VARCHAR(255)   NOT NULL,
    rol          VARCHAR(20)    NOT NULL 
                   CONSTRAINT chk_usuarios_rol 
                   CHECK (rol IN ('cliente','gerente','administrador')),
    activo       BIT            NOT NULL DEFAULT 1,
    created_at   DATETIME2      NOT NULL DEFAULT GETDATE(),
    updated_at   DATETIME2      NOT NULL DEFAULT GETDATE()
);
GO

-- Trigger para actualizar updated_at en usuarios
CREATE TRIGGER trg_usuarios_updated_at
ON usuarios
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE u
    SET u.updated_at = GETDATE()
    FROM usuarios AS u
    INNER JOIN inserted AS i
        ON u.id_usuario = i.id_usuario;
END;
GO


-- 2. Tabla: clientes
CREATE TABLE clientes (
    id_cliente     INT IDENTITY(1,1) PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    telefono       VARCHAR(20)  NULL,
    correo         VARCHAR(100) NULL,
    fecha_registro DATE         NOT NULL DEFAULT CONVERT(DATE, GETDATE()),
    created_at     DATETIME2    NOT NULL DEFAULT GETDATE(),
    updated_at     DATETIME2    NOT NULL DEFAULT GETDATE()
);
GO

-- Trigger para actualizar updated_at en clientes
CREATE TRIGGER trg_clientes_updated_at
ON clientes
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE c
    SET c.updated_at = GETDATE()
    FROM clientes AS c
    INNER JOIN inserted AS i
        ON c.id_cliente = i.id_cliente;
END;
GO


-- 3. Tabla: servicios
CREATE TABLE servicios (
    id_servicio   INT IDENTITY(1,1) PRIMARY KEY,
    nombre        VARCHAR(100)  NOT NULL,
    duracion_min  INT           NOT NULL,
    precio        DECIMAL(10,2) NULL,
    descripcion   TEXT          NULL,
    created_at    DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at    DATETIME2     NOT NULL DEFAULT GETDATE()
);
GO

-- Trigger para actualizar updated_at en servicios
CREATE TRIGGER trg_servicios_updated_at
ON servicios
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE s
    SET s.updated_at = GETDATE()
    FROM servicios AS s
    INNER JOIN inserted AS i
        ON s.id_servicio = i.id_servicio;
END;
GO


-- 4. Tabla: series_recurrentes
CREATE TABLE series_recurrentes (
    id_serie           INT IDENTITY(1,1) PRIMARY KEY,
    tipo_recurrencia   VARCHAR(20) NOT NULL 
                         CONSTRAINT chk_series_tipo 
                         CHECK (tipo_recurrencia IN ('diaria','semanal','mensual')),
    fecha_inicio       DATE        NOT NULL,
    fecha_fin          DATE        NULL,
    repeticiones       INT         NULL,
    created_at         DATETIME2    NOT NULL DEFAULT GETDATE(),
    updated_at         DATETIME2    NOT NULL DEFAULT GETDATE()
);
GO

-- Trigger para actualizar updated_at en series_recurrentes
CREATE TRIGGER trg_series_recurrentes_updated_at
ON series_recurrentes
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE sr
    SET sr.updated_at = GETDATE()
    FROM series_recurrentes AS sr
    INNER JOIN inserted AS i
        ON sr.id_serie = i.id_serie;
END;
GO


-- 5. Tabla: configuracion_visual
CREATE TABLE configuracion_visual (
    id_config       INT IDENTITY(1,1) PRIMARY KEY,
    nombre_negocio  VARCHAR(100)   NOT NULL,
    logo_path       VARCHAR(255)   NULL,
    color_fondo     VARCHAR(7)     NOT NULL DEFAULT '#FFFFFF',
    color_texto     VARCHAR(7)     NOT NULL DEFAULT '#000000',
    color_botones   VARCHAR(7)     NOT NULL DEFAULT '#007BFF',
    created_at      DATETIME2      NOT NULL DEFAULT GETDATE(),
    updated_at      DATETIME2      NOT NULL DEFAULT GETDATE()
);
GO

-- Trigger para actualizar updated_at en configuracion_visual
CREATE TRIGGER trg_configuracion_visual_updated_at
ON configuracion_visual
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE cv
    SET cv.updated_at = GETDATE()
    FROM configuracion_visual AS cv
    INNER JOIN inserted AS i
        ON cv.id_config = i.id_config;
END;
GO


-- 6. Tabla: reportes_programados
CREATE TABLE reportes_programados (
    id_reporte     INT IDENTITY(1,1) PRIMARY KEY,
    nombre         VARCHAR(100)  NOT NULL,
    tipo_reporte   VARCHAR(50)   NOT NULL,
    parametros     NVARCHAR(MAX) NULL,  -- JSON o texto estructurado
    scheduled_time DATETIME2     NULL,
    created_at     DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at     DATETIME2     NOT NULL DEFAULT GETDATE()
);
GO

-- Trigger para actualizar updated_at en reportes_programados
CREATE TRIGGER trg_reportes_programados_updated_at
ON reportes_programados
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE rp
    SET rp.updated_at = GETDATE()
    FROM reportes_programados AS rp
    INNER JOIN inserted AS i
        ON rp.id_reporte = i.id_reporte;
END;
GO


-- 7. Tabla: citas
--    - Clave primaria compuesta: (id_cliente, id_servicio, fecha, hora_inicio)
--    - Control de concurrencia: version
-- ===============================================================
CREATE TABLE citas (
    id_cliente    INT      NOT NULL,
    id_servicio   INT      NOT NULL,
    fecha         DATE     NOT NULL,
    hora_inicio   TIME     NOT NULL,
    hora_fin      TIME     NOT NULL,
    notas         TEXT     NULL,
    id_serie      INT      NULL,
    version       INT      NOT NULL DEFAULT 1,
    created_at    DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at    DATETIME2 NOT NULL DEFAULT GETDATE(),

    CONSTRAINT pk_citas 
        PRIMARY KEY (id_cliente, id_servicio, fecha, hora_inicio),

    CONSTRAINT fk_citas_cliente 
        FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente)
        ON DELETE CASCADE,

    CONSTRAINT fk_citas_servicio 
        FOREIGN KEY (id_servicio)
        REFERENCES servicios(id_servicio)
        ON DELETE NO ACTION,

    CONSTRAINT fk_citas_serie 
        FOREIGN KEY (id_serie)
        REFERENCES series_recurrentes(id_serie)
        ON DELETE SET NULL
);
GO

-- Trigger para actualizar updated_at y manejar versión en citas
CREATE TRIGGER trg_citas_updated_at
ON citas
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE c
    SET 
        c.updated_at = GETDATE(),
        c.version = c.version + 1
    FROM citas AS c
    INNER JOIN inserted AS i
        ON c.id_cliente  = i.id_cliente
       AND c.id_servicio = i.id_servicio
       AND c.fecha       = i.fecha
       AND c.hora_inicio = i.hora_inicio;
END;
GO


-- 8. Tabla: pagos
--    - Relacionado con citas (PK compuesta)
-- ===============================================================
CREATE TABLE pagos (
    id_pago       INT          IDENTITY(1,1) PRIMARY KEY,
    id_cliente    INT          NOT NULL,
    id_servicio   INT          NOT NULL,
    fecha         DATE         NOT NULL,
    hora_inicio   TIME         NOT NULL,
    monto         DECIMAL(10,2) NOT NULL,
    fecha_pago    DATETIME2    NOT NULL DEFAULT GETDATE(),
    created_at    DATETIME2    NOT NULL DEFAULT GETDATE(),
    updated_at    DATETIME2    NOT NULL DEFAULT GETDATE(),

    CONSTRAINT fk_pagos_citas 
        FOREIGN KEY (id_cliente, id_servicio, fecha, hora_inicio)
        REFERENCES citas(id_cliente, id_servicio, fecha, hora_inicio)
        ON DELETE CASCADE
);
GO

-- Trigger para actualizar updated_at en pagos
CREATE TRIGGER trg_pagos_updated_at
ON pagos
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE p
    SET p.updated_at = GETDATE()
    FROM pagos AS p
    INNER JOIN inserted AS i
        ON p.id_pago = i.id_pago;
END;
GO


/* ==============================================
   Insertar registros por defecto
   ============================================== */

-- Insertar configuración visual por defecto
INSERT INTO configuracion_visual (nombre_negocio)
VALUES ('Mi Negocio');
GO

-- Insertar usuario administrador por defecto (contraseña: admin123 → SHA1)
INSERT INTO usuarios (nombre, usuario, contrasena, rol)
VALUES (
  'Administrador',
  'admin',
  CONVERT(VARCHAR(40), HASHBYTES('SHA1','admin123'), 2),
  'administrador'
);
GO


INSERT INTO usuarios (nombre, usuario, contrasena, rol, activo)
VALUES (
    N'Gerente',
    N'gerente',
    -- Encriptar 'gerente123' con SHA1
    CONVERT(VARCHAR(40), HASHBYTES('SHA1', N'gerente123'), 2),
    N'gerente',
    1
);
GO

INSERT INTO clientes (nombre, telefono, correo)
VALUES (
    N'Angel Soto Trejo',
    N'4495544745',
    N'21030660@itcelaya.edu.mx'
);
GO

/*
  3. Obtener ID del cliente recién insertado
     (Para asignarlo al usuario con rol 'cliente')
*/
DECLARE @IdClienteDefault INT;
SELECT @IdClienteDefault = SCOPE_IDENTITY();
-- SCOPE_IDENTITY() recupera el último IDENTITY insertado en esta sesión

/*
  4. Insertar usuario CLIENTE por defecto
     - Nombre: Cliente Default
     - Usuario: cliente
     - Contraseña: cliente123 (encriptada con SHA1)
     - Rol: cliente
*/
INSERT INTO usuarios (nombre, usuario, contrasena, rol, activo)
VALUES (
    N'Angel Soto Trejo',
    N'asoto',
    -- Encriptar 'cliente123' con SHA1
    CONVERT(VARCHAR(40), HASHBYTES('SHA1', N'cliente123'), 2),
    N'cliente',
    1
);
GO
-- init.sql optimizado para PostgreSQL
CREATE TABLE Cliente (
    id INT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    ciudad VARCHAR(100) NOT NULL
);

CREATE TABLE Sucursal (
    id INT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ciudad VARCHAR(100) NOT NULL
);

CREATE TABLE Producto (
    id INT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipoProducto VARCHAR(100) NOT NULL
);

CREATE TABLE Inscripcion (
    idProducto INT REFERENCES Producto(id),
    idCliente INT REFERENCES Cliente(id),
    PRIMARY KEY (idProducto, idCliente)
);

CREATE TABLE Disponibilidad (
    idProducto INT REFERENCES Producto(id),
    idSucursal INT REFERENCES Sucursal(id),
    PRIMARY KEY (idProducto, idSucursal)
);

CREATE TABLE Visitan (
    idSucursal INT REFERENCES Sucursal(id),
    idCliente INT REFERENCES Cliente(id),
    fechaVisita DATE NOT NULL,
    PRIMARY KEY (idSucursal, idCliente, fechaVisita)
);

-- DATOS DE PRUEBA
INSERT INTO Cliente VALUES (1, 'Daniel', 'Hernandez', 'Bogota'), (2, 'Maria', 'Perez', 'Medellin');
INSERT INTO Sucursal VALUES (10, 'Sucursal Norte', 'Bogota'), (20, 'Sucursal Sur', 'Bogota');
INSERT INTO Producto VALUES (100, 'Fondo Acciones', 'Inversion'), (200, 'Fondo Deuda', 'Ahorro');

-- Caso Daniel: Producto 100 disponible SOLO en Sucursal 10, y Daniel visita SOLO la 10.
INSERT INTO Disponibilidad VALUES (100, 10);
INSERT INTO Inscripcion VALUES (100, 1);
INSERT INTO Visitan VALUES (10, 1, '2024-03-20');

-- Caso Maria: Producto 200 disponible en 10 y 20, pero Maria solo visita la 20
INSERT INTO Disponibilidad VALUES (200, 10), (200, 20);
INSERT INTO Inscripcion VALUES (200, 2);
INSERT INTO Visitan VALUES (20, 2, '2024-03-20');
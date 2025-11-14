CREATE DATABASE tiendaRopa;
USE tiendaRopa;

CREATE TABLE productos (
	idProducto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50),
    categoria VARCHAR(50),
    marca VARCHAR(50),
    precio DECIMAL(10,2),
    stock INT
);

CREATE TABLE usuarios (
	idUsuario INT AUTO_INCREMENT PRIMARY KEY,
	nombreUsuario VARCHAR(100),
    apellidoUsuario VARCHAR(100),
    rut VARCHAR(10) UNIQUE,
    correo VARCHAR(100),
    rol VARCHAR(10)
);

CREATE TABLE clientes (
	idCliente INT AUTO_INCREMENT PRIMARY KEY,
    nombreCliente VARCHAR(100),
    apellidoCliente VARCHAR(100),
    rut VARCHAR(10) UNIQUE,
    correo VARCHAR(100),
    telefono VARCHAR(15),
    direccion VARCHAR(50)
);

CREATE TABLE ventas (
	idVenta INT  AUTO_INCREMENT PRIMARY KEY,
    idCliente INT,
    idUsuario INT,
    fecha DATETIME DEFAULT NOW(),
    totalVenta DECIMAL(10,2),
    FOREIGN KEY (idCliente) REFERENCES clientes(idCliente),
    FOREIGN KEY (idUsuario) REFERENCES usuarios(idUsuario)
);

CREATE TABLE detalleVenta (
	idDetalle INT AUTO_INCREMENT PRIMARY KEY,
    idVenta INT,
    idProducto INT,
    cantidad INT,
    subTotal DECIMAL(10,2),
    FOREIGN KEY (idVenta) REFERENCES ventas(idVenta),
    FOREIGN KEY (idProducto) REFERENCES Productos(idProducto)
);

INSERT INTO productos (nombre, categoria, marca, precio, stock)
VALUES
	('Jeans Slim Fit', 'Pantalones', 'Levis', 45000.00, 50),
	('Camiseta Algodón Estampada', 'Poleras', 'Nike', 15990.00, 120),
	('Chaqueta Puffer', 'Abrigos', 'The North Face', 89990.00, 30),
	('Vestido Midi Flores', 'Vestidos', 'Zara', 32500.00, 45),
	('Zapatillas Urbanas', 'Calzado', 'Adidas', 59990.00, 60),
	('Polerón con Capucha', 'Polerones', 'Under Armour', 29990.00, 75),
	('Falda Plisada Negra', 'Faldas', 'H&M', 18990.00, 40),
	('Corbata de Seda', 'Accesorios', 'Hugo Boss', 25000.00, 25),
	('Calcetines Deportivos (3-pack)', 'Accesorios', 'Puma', 5990.00, 150),
	('Blusa de Lino Blanca', 'Camisas', 'Mango', 28000.00, 55),
	('Bikini Top', 'Trajes de Baño', 'Roxy', 19990.00, 90),
	('Short Deportivo', 'Pantalones', 'Reebok', 12500.00, 80),
	('Gorro de Lana', 'Accesorios', 'Columbia', 9990.00, 65),
	('Cartera de Cuero', 'Accesorios', 'Guess', 49990.00, 20),
	('Pijama de Invierno', 'Ropa Interior', 'Oysho', 21500.00, 35);

INSERT INTO usuarios (nombreUsuario, apellidoUsuario, rut, correo, rol)
VALUES
	('Javier', 'Soto', '17123456-7', 'javier.s@tiendaropa.cl', 'Admin'),
	('María', 'Pérez', '16876543-2', 'maria.p@tiendaropa.cl', 'Admin'),
	('Carlos', 'López', '18001002-3', 'carlos.l@tiendaropa.cl', 'Vendedor'),
	('Ana', 'Gómez', '19550330-4', 'ana.g@tiendaropa.cl', 'Vendedor'),
	('Pedro', 'Muñoz', '20100200-5', 'pedro.m@tiendaropa.cl', 'Vendedor');

INSERT INTO clientes (nombreCliente, apellidoCliente, rut, correo, telefono, direccion)
VALUES
	('Elena', 'Rojas', '21098765-4', 'elena.r@email.com', '987654321', 'Av. Central 500'),
	('Fernando', 'Tapia', '15234567-8', 'fernando.t@email.com', '912345678', 'Calle Las Flores 123'),
	('Isabel', 'Vargas', '17345678-9', 'isabel.v@email.com', '955554444', 'Diagonal Sur 88'),
	('Ricardo', 'Díaz', '18456789-0', 'ricardo.d@email.com', '966663333', 'Los Alerces 77B'),
	('Sofía', 'Castro', '16567890-1', 'sofia.c@email.com', '944442222', 'Pasaje Uno 99');

INSERT INTO ventas (idCliente, idUsuario, fecha, totalVenta)
VALUES
	(1, 3, '2025-10-25 10:30:00', 45000.00),
	(2, 4, '2025-10-26 14:15:00', 75980.00),
	(3, 5, '2025-10-27 11:00:00', 69980.00),
	(4, 3, '2025-10-27 17:45:00', 129990.00),
	(5, 4, '2025-10-28 09:00:00', 45990.00),
	(1, 5, '2025-10-29 16:20:00', 50990.00),
	(2, 3, '2025-10-30 13:05:00', 31980.00),
	(3, 4, '2025-10-31 18:30:00', 59990.00),
	(4, 5, '2025-11-01 12:40:00', 25000.00),
	(5, 3, '2025-11-02 15:50:00', 40800.00);

INSERT INTO detalleVenta (idVenta, idProducto, cantidad, subTotal)
VALUES
	(1, 1, 1, 45000.00),
	(2, 3, 1, 89990.00),
	(3, 4, 1, 32500.00),
	(3, 6, 1, 29990.00),
	(4, 5, 2, 119980.00),
	(4, 9, 2, 11980.00),
	(5, 1, 1, 45000.00),
	(6, 14, 1, 49990.00),
	(7, 2, 2, 31980.00),
	(8, 5, 1, 59990.00),
	(9, 8, 1, 25000.00),
	(10, 10, 1, 28000.00),
	(10, 13, 1, 9990.00),
	(10, 9, 1, 5990.00);
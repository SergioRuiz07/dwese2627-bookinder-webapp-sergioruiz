-- Primero se borra la tabla hija (libro) y después la padre (categoria)
DROP TABLE IF EXISTS libro;
DROP TABLE IF EXISTS categoria;

-- Tabla CATEGORIA (lado "1" de la relación)
CREATE TABLE categoria
(
    id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

-- Tabla LIBRO (lado "N" de la relación)
-- Cada libro tiene obligatoriamente UNA categoría (categoria_id NOT NULL).
-- ON DELETE RESTRICT: no deja borrar una categoría si todavía tiene libros.
CREATE TABLE libro
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn             VARCHAR(20)  NOT NULL UNIQUE,
    titulo           VARCHAR(255) NOT NULL,
    autor            VARCHAR(150) NOT NULL,
    editorial        VARCHAR(150),
    anio_publicacion INT,
    sinopsis         VARCHAR(1000),
    url_portada      VARCHAR(500),
    categoria_id     BIGINT       NOT NULL,
    CONSTRAINT fk_libro_categoria
        FOREIGN KEY (categoria_id) REFERENCES categoria (id)
            ON DELETE RESTRICT
);

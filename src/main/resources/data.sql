-- Categorías
INSERT INTO categoria (nombre) VALUES ('Novela');
INSERT INTO categoria (nombre) VALUES ('Fantasía');
INSERT INTO categoria (nombre) VALUES ('Ciencia ficción');
INSERT INTO categoria (nombre) VALUES ('Programación');
INSERT INTO categoria (nombre) VALUES ('Matemáticas');

-- Libros (cada uno con UNA categoría)
INSERT INTO libro (isbn, titulo, autor, editorial, anio_publicacion, sinopsis, url_portada, categoria_id)
VALUES ('978-84-0000-001-1', 'Don Quijote de la Mancha', 'Miguel de Cervantes', 'Alfaguara', 1605,
        'Las aventuras de un hidalgo que se cree caballero andante.', NULL, 1);

INSERT INTO libro (isbn, titulo, autor, editorial, anio_publicacion, sinopsis, url_portada, categoria_id)
VALUES ('978-84-0000-002-8', 'La sombra del viento', 'Carlos Ruiz Zafón', 'Planeta', 2001,
        'Un niño descubre un libro misterioso en el Cementerio de los Libros Olvidados.', NULL, 1);

INSERT INTO libro (isbn, titulo, autor, editorial, anio_publicacion, sinopsis, url_portada, categoria_id)
VALUES ('978-84-0000-003-5', 'El Hobbit', 'J. R. R. Tolkien', 'Minotauro', 1937,
        'Bilbo Bolsón emprende un viaje junto a trece enanos.', NULL, 2);

INSERT INTO libro (isbn, titulo, autor, editorial, anio_publicacion, sinopsis, url_portada, categoria_id)
VALUES ('978-84-0000-004-2', 'El nombre del viento', 'Patrick Rothfuss', 'Plaza & Janés', 2007,
        'Kvothe cuenta la historia de su vida.', NULL, 2);

INSERT INTO libro (isbn, titulo, autor, editorial, anio_publicacion, sinopsis, url_portada, categoria_id)
VALUES ('978-84-0000-005-9', 'Dune', 'Frank Herbert', 'Debolsillo', 1965,
        'Paul Atreides llega al planeta desértico Arrakis.', NULL, 3);

INSERT INTO libro (isbn, titulo, autor, editorial, anio_publicacion, sinopsis, url_portada, categoria_id)
VALUES ('978-84-0000-006-6', 'Fundación', 'Isaac Asimov', 'Debolsillo', 1951,
        'Un matemático intenta salvar el conocimiento de un imperio en decadencia.', NULL, 3);

INSERT INTO libro (isbn, titulo, autor, editorial, anio_publicacion, sinopsis, url_portada, categoria_id)
VALUES ('978-84-0000-007-3', 'Clean Code', 'Robert C. Martin', 'Anaya', 2008,
        'Buenas prácticas para escribir código limpio.', NULL, 4);

INSERT INTO libro (isbn, titulo, autor, editorial, anio_publicacion, sinopsis, url_portada, categoria_id)
VALUES ('978-84-0000-008-0', 'Spring Boot en acción', 'Craig Walls', 'Anaya', 2016,
        'Introducción práctica al desarrollo con Spring Boot.', NULL, 4);

INSERT INTO libro (isbn, titulo, autor, editorial, anio_publicacion, sinopsis, url_portada, categoria_id)
VALUES ('978-84-0000-009-7', 'Cálculo', 'Michael Spivak', 'Reverté', 1967,
        'Libro clásico de cálculo para estudiantes.', NULL, 5);

INSERT INTO libro (isbn, titulo, autor, editorial, anio_publicacion, sinopsis, url_portada, categoria_id)
VALUES ('978-84-0000-010-3', 'Álgebra lineal', 'Stanley Grossman', 'McGraw-Hill', 1980,
        'Conceptos básicos de matrices y vectores.', NULL, 5);
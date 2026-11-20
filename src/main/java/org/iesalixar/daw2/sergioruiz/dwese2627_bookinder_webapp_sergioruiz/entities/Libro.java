package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa un libro.
 * Es el lado N de la relación 1:N: muchos libros pertenecen a una misma categoría.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "libro")
public class Libro {

    /**
     * Identificador del libro. Lo genera la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * ISBN del libro. Es obligatorio y no se puede repetir.
     */
    @Column(name = "isbn", nullable = false, unique = true, length = 20)
    private String isbn;

    /**
     * Título del libro.
     */
    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    /**
     * Autor del libro.
     */
    @Column(name = "autor", nullable = false, length = 150)
    private String autor;

    /**
     * Editorial del libro (opcional).
     */
    @Column(name = "editorial", length = 150)
    private String editorial;

    /**
     * Año en el que se publicó el libro (opcional).
     */
    @Column(name = "anio_publicacion")
    private Integer anioPublicacion;

    /**
     * Resumen corto del libro (opcional).
     */
    @Column(name = "sinopsis", length = 1000)
    private String sinopsis;

    /**
     * Ruta web de la imagen de la portada subida (por ejemplo /uploads/uuid.png). Es opcional.
     */
    @Column(name = "url_portada", length = 500)
    private String urlPortada;

    /**
     * Categoría a la que pertenece el libro.
     * Aquí está la clave ajena categoria_id, porque Libro es el lado N.
     * Es LAZY: la categoría solo se carga cuando se necesita o cuando se pide con JOIN FETCH.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
}

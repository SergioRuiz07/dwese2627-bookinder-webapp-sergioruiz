package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la vista de detalle de un libro. Incluye todos sus datos y su categoría.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LibroDetailDTO {

    /**
     * Id del libro.
     */
    private Long id;

    /**
     * ISBN del libro.
     */
    private String isbn;

    /**
     * Título del libro.
     */
    private String titulo;

    /**
     * Autor del libro.
     */
    private String autor;

    /**
     * Editorial del libro.
     */
    private String editorial;

    /**
     * Año de publicación.
     */
    private Integer anioPublicacion;

    /**
     * Sinopsis del libro.
     */
    private String sinopsis;

    /**
     * Ruta web de la portada.
     */
    private String urlPortada;

    /**
     * Categoría a la que pertenece el libro.
     */
    private CategoriaDTO categoria;
}

package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO básico de Libro para el listado.
 * De la categoría solo se guarda el nombre, que es lo único que se muestra en la tabla.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LibroDTO {

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
     * Nombre de la categoría del libro.
     */
    private String categoriaNombre;
}

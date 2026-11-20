package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para la vista de detalle de una categoría. Incluye la lista de sus libros.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaDetailDTO {

    /**
     * Id de la categoría.
     */
    private Long id;

    /**
     * Nombre de la categoría.
     */
    private String nombre;

    /**
     * Libros que pertenecen a la categoría.
     */
    private List<LibroDTO> libros;
}

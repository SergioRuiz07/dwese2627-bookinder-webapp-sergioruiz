package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO básico de Categoria. Se usa en el listado y en el desplegable del formulario de libros.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoriaDTO {

    /**
     * Id de la categoría.
     */
    private Long id;

    /**
     * Nombre de la categoría.
     */
    private String nombre;
}

package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con los datos del formulario para editar una categoría.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoriaUpdateDTO {

    /**
     * Id de la categoría que se está editando. Es obligatorio.
     */
    @NotNull(message = "{msg.categoria.id.notNull}")
    private Long id;

    /**
     * Nombre de la categoría: obligatorio y con 100 caracteres como máximo.
     */
    @NotBlank(message = "{msg.categoria.nombre.notEmpty}")
    @Size(max = 100, message = "{msg.categoria.nombre.size}")
    private String nombre;
}

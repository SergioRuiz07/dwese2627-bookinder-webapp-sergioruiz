package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con los datos del formulario para crear una categoría.
 * Las anotaciones comprueban los datos antes de llegar al servicio.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoriaCreateDTO {

    /**
     * Id de la categoría. Al crear siempre llega vacío (null) porque lo genera la base de datos.
     * Se deja para que el formulario sepa si está creando o editando.
     */
    private Long id;

    /**
     * Nombre de la categoría: obligatorio y con 100 caracteres como máximo.
     */
    @NotBlank(message = "{msg.categoria.nombre.notEmpty}")
    @Size(max = 100, message = "{msg.categoria.nombre.size}")
    private String nombre;
}

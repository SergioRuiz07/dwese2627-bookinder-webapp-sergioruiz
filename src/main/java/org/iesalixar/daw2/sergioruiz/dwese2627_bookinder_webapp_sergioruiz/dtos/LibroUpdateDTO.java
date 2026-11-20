package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con los datos del formulario para editar un libro.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LibroUpdateDTO {

    /**
     * Id del libro que se está editando. Es obligatorio.
     */
    @NotNull(message = "{msg.libro.id.notNull}")
    private Long id;

    /**
     * ISBN del libro: obligatorio, máximo 20 caracteres y solo números, guiones y la letra X.
     */
    @NotBlank(message = "{msg.libro.isbn.notEmpty}")
    @Size(max = 20, message = "{msg.libro.isbn.size}")
    @Pattern(regexp = "^[0-9Xx-]*$", message = "{msg.libro.isbn.pattern}")
    private String isbn;

    /**
     * Título del libro: obligatorio y con 255 caracteres como máximo.
     */
    @NotBlank(message = "{msg.libro.titulo.notEmpty}")
    @Size(max = 255, message = "{msg.libro.titulo.size}")
    private String titulo;

    /**
     * Autor del libro: obligatorio y con 150 caracteres como máximo.
     */
    @NotBlank(message = "{msg.libro.autor.notEmpty}")
    @Size(max = 150, message = "{msg.libro.autor.size}")
    private String autor;

    /**
     * Editorial del libro (opcional), con 150 caracteres como máximo.
     */
    @Size(max = 150, message = "{msg.libro.editorial.size}")
    private String editorial;

    /**
     * Año de publicación (opcional). No puede ser negativo.
     * Que no sea un año futuro se comprueba en el controlador.
     */
    @PositiveOrZero(message = "{msg.libro.anioPublicacion.positive}")
    private Integer anioPublicacion;

    /**
     * Sinopsis del libro (opcional), con 1000 caracteres como máximo.
     */
    @Size(max = 1000, message = "{msg.libro.sinopsis.size}")
    private String sinopsis;

    /**
     * Ruta de la portada actual (opcional), con 500 caracteres como máximo.
     * La imagen nueva no llega aquí, sino aparte como MultipartFile.
     */
    @Size(max = 500, message = "{msg.libro.urlPortada.size}")
    private String urlPortada;

    /**
     * Id de la categoría elegida en el desplegable. Es obligatorio.
     */
    @NotNull(message = "{msg.libro.categoriaId.notNull}")
    private Long categoriaId;
}

package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.mappers;

import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.LibroCreateDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.LibroDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.LibroDetailDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.LibroUpdateDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities.Libro;

import java.util.List;

/**
 * Mapper para pasar de la entidad Libro a sus DTOs y al revés.
 * Se hace a mano, sin usar ninguna librería de mapeo.
 * La categoría real la pone el servicio, porque el mapper no accede a la base de datos.
 */
public class LibroMapper {

    // ─────────────────────────────────────────
    // Entidad → DTO (listado básico)
    // ─────────────────────────────────────────

    /**
     * Convierte un libro en un LibroDTO para el listado.
     * De la categoría solo se copia el nombre, que es lo que se muestra en la tabla.
     *
     * @param entidad libro que se quiere convertir
     * @return el DTO del listado, o null si el libro es null
     */
    public static LibroDTO aDTO(Libro entidad) {
        if (entidad == null) return null;
        LibroDTO dto = new LibroDTO();
        dto.setId(entidad.getId());
        dto.setIsbn(entidad.getIsbn());
        dto.setTitulo(entidad.getTitulo());
        dto.setAutor(entidad.getAutor());
        dto.setCategoriaNombre(entidad.getCategoria() != null ? entidad.getCategoria().getNombre() : null);
        return dto;
    }

    /**
     * Convierte una lista de libros en una lista de LibroDTO.
     *
     * @param entidades lista de libros
     * @return lista de DTOs (vacía si la lista es null)
     */
    public static List<LibroDTO> aListaDTO(List<Libro> entidades) {
        if (entidades == null) return List.of();
        return entidades.stream().map(LibroMapper::aDTO).toList();
    }

    // ─────────────────────────────────────────
    // Entidad → DTO (detalle con su categoría)
    // ─────────────────────────────────────────

    /**
     * Convierte un libro en un LibroDetailDTO, incluyendo su categoría.
     *
     * @param entidad libro que se quiere convertir
     * @return el DTO de detalle, o null si el libro es null
     */
    public static LibroDetailDTO aDetalleDTO(Libro entidad) {
        if (entidad == null) return null;
        LibroDetailDTO dto = new LibroDetailDTO();
        dto.setId(entidad.getId());
        dto.setIsbn(entidad.getIsbn());
        dto.setTitulo(entidad.getTitulo());
        dto.setAutor(entidad.getAutor());
        dto.setEditorial(entidad.getEditorial());
        dto.setAnioPublicacion(entidad.getAnioPublicacion());
        dto.setSinopsis(entidad.getSinopsis());
        dto.setUrlPortada(entidad.getUrlPortada());
        dto.setCategoria(CategoriaMapper.aDTO(entidad.getCategoria()));
        return dto;
    }

    /**
     * Convierte un libro en un LibroUpdateDTO para rellenar el formulario de edición.
     * Así el desplegable sale con la categoría actual ya seleccionada.
     *
     * @param entidad libro que se quiere editar
     * @return el DTO de edición, o null si el libro es null
     */
    public static LibroUpdateDTO aUpdateDTO(Libro entidad) {
        if (entidad == null) return null;
        LibroUpdateDTO dto = new LibroUpdateDTO();
        dto.setId(entidad.getId());
        dto.setIsbn(entidad.getIsbn());
        dto.setTitulo(entidad.getTitulo());
        dto.setAutor(entidad.getAutor());
        dto.setEditorial(entidad.getEditorial());
        dto.setAnioPublicacion(entidad.getAnioPublicacion());
        dto.setSinopsis(entidad.getSinopsis());
        dto.setUrlPortada(entidad.getUrlPortada());
        dto.setCategoriaId(entidad.getCategoria() != null ? entidad.getCategoria().getId() : null);
        return dto;
    }

    // ─────────────────────────────────────────
    // DTO (crear / editar) → Entidad
    // ─────────────────────────────────────────

    /**
     * Crea un libro nuevo a partir del formulario de alta.
     * El id se deja vacío para que lo genere la base de datos y la categoría la pone el servicio.
     *
     * @param dto datos del formulario de alta
     * @return el libro nuevo, o null si el DTO es null
     */
    public static Libro aEntidad(LibroCreateDTO dto) {
        if (dto == null) return null;
        Libro libro = new Libro();
        libro.setIsbn(dto.getIsbn().trim());
        libro.setTitulo(dto.getTitulo().trim());
        libro.setAutor(dto.getAutor().trim());
        libro.setEditorial(vacioANull(dto.getEditorial()));
        libro.setAnioPublicacion(dto.getAnioPublicacion());
        libro.setSinopsis(vacioANull(dto.getSinopsis()));
        libro.setUrlPortada(vacioANull(dto.getUrlPortada()));
        return libro;
    }

    /**
     * Copia los datos del formulario de edición en un libro que ya existe en la base de datos.
     * No se toca el id. La categoría la pone el servicio.
     *
     * @param dto     datos del formulario de edición
     * @param entidad libro que ya existe
     */
    public static void copiarAEntidadExistente(LibroUpdateDTO dto, Libro entidad) {
        if (dto == null || entidad == null) return;
        entidad.setIsbn(dto.getIsbn().trim());
        entidad.setTitulo(dto.getTitulo().trim());
        entidad.setAutor(dto.getAutor().trim());
        entidad.setEditorial(vacioANull(dto.getEditorial()));
        entidad.setAnioPublicacion(dto.getAnioPublicacion());
        entidad.setSinopsis(vacioANull(dto.getSinopsis()));
        entidad.setUrlPortada(vacioANull(dto.getUrlPortada()));
    }

    /**
     * Los campos opcionales del formulario llegan como texto vacío si no se rellenan.
     * Este método los cambia a null para no guardar textos vacíos en la base de datos.
     *
     * @param texto texto del formulario
     * @return el texto sin espacios al principio y al final, o null si está vacío
     */
    private static String vacioANull(String texto) {
        if (texto == null || texto.isBlank()) return null;
        return texto.trim();
    }
}

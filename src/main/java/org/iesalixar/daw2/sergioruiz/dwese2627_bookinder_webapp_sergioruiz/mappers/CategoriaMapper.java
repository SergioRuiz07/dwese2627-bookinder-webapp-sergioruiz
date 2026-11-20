package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.mappers;

import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.*;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities.Categoria;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities.Libro;

import java.util.List;

/**
 * Mapper para pasar de la entidad Categoria a sus DTOs y al revés.
 * Se hace a mano, sin usar ninguna librería de mapeo.
 */
public class CategoriaMapper {

    // ─────────────────────────────────────────
    // Entidad → DTO (listado básico)
    // ─────────────────────────────────────────

    /**
     * Convierte una categoría en un CategoriaDTO.
     *
     * @param entidad categoría que se quiere convertir
     * @return el DTO con el id y el nombre, o null si la categoría es null
     */
    public static CategoriaDTO aDTO(Categoria entidad) {
        if (entidad == null) return null;
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(entidad.getId());
        dto.setNombre(entidad.getNombre());
        return dto;
    }

    /**
     * Convierte una lista de categorías en una lista de CategoriaDTO.
     *
     * @param entidades lista de categorías
     * @return lista de DTOs (vacía si la lista es null)
     */
    public static List<CategoriaDTO> aListaDTO(List<Categoria> entidades) {
        if (entidades == null) return List.of();
        return entidades.stream().map(CategoriaMapper::aDTO).toList();
    }

    // ─────────────────────────────────────────
    // Entidad → DTO (detalle con sus libros)
    // ─────────────────────────────────────────

    /**
     * Convierte una categoría en un CategoriaDetailDTO, incluyendo la lista de sus libros.
     *
     * @param entidad categoría con sus libros ya cargados
     * @return el DTO de detalle, o null si la categoría es null
     */
    public static CategoriaDetailDTO aDetalleDTO(Categoria entidad) {
        if (entidad == null) return null;
        CategoriaDetailDTO dto = new CategoriaDetailDTO();
        dto.setId(entidad.getId());
        dto.setNombre(entidad.getNombre());
        dto.setLibros(aListaLibroDTO(entidad.getLibros()));
        return dto;
    }

    /**
     * Convierte un libro en un LibroDTO sencillo para la tabla de libros del detalle de la categoría.
     *
     * @param libro libro que se quiere convertir
     * @return el DTO del libro, o null si el libro es null
     */
    public static LibroDTO aLibroDTO(Libro libro) {
        if (libro == null) return null;
        LibroDTO dto = new LibroDTO();
        dto.setId(libro.getId());
        dto.setIsbn(libro.getIsbn());
        dto.setTitulo(libro.getTitulo());
        dto.setAutor(libro.getAutor());
        return dto;
    }

    /**
     * Convierte una lista de libros en una lista de LibroDTO.
     *
     * @param libros lista de libros
     * @return lista de DTOs (vacía si la lista es null)
     */
    public static List<LibroDTO> aListaLibroDTO(List<Libro> libros) {
        if (libros == null) return List.of();
        return libros.stream().map(CategoriaMapper::aLibroDTO).toList();
    }

    /**
     * Convierte una categoría en un CategoriaUpdateDTO para rellenar el formulario de edición.
     *
     * @param entidad categoría que se quiere editar
     * @return el DTO de edición, o null si la categoría es null
     */
    public static CategoriaUpdateDTO aUpdateDTO(Categoria entidad) {
        if (entidad == null) return null;
        CategoriaUpdateDTO dto = new CategoriaUpdateDTO();
        dto.setId(entidad.getId());
        dto.setNombre(entidad.getNombre());
        return dto;
    }

    // ─────────────────────────────────────────
    // DTO (crear / editar) → Entidad
    // ─────────────────────────────────────────

    /**
     * Crea una categoría nueva a partir del formulario de alta.
     * El id se deja vacío para que lo genere la base de datos.
     *
     * @param dto datos del formulario de alta
     * @return la categoría nueva, o null si el DTO es null
     */
    public static Categoria aEntidad(CategoriaCreateDTO dto) {
        if (dto == null) return null;
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre().trim());
        return categoria;
    }

    /**
     * Copia los datos del formulario de edición en una categoría que ya existe en la base de datos.
     * No se toca el id ni la lista de libros.
     *
     * @param dto     datos del formulario de edición
     * @param entidad categoría que ya existe
     */
    public static void copiarAEntidadExistente(CategoriaUpdateDTO dto, Categoria entidad) {
        if (dto == null || entidad == null) return;
        entidad.setNombre(dto.getNombre().trim());
    }
}

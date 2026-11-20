package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.services;

import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.LibroCreateDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.LibroDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.LibroDetailDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.LibroUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio de Libro: contiene la lógica del CRUD de libros.
 * El controlador llama a este servicio y el servicio llama a los DAOs.
 */
public interface LibroService {

    /**
     * Devuelve una página de libros para el listado.
     *
     * @param pageable número de página, tamaño y ordenación
     * @return página con los libros
     */
    Page<LibroDTO> listar(Pageable pageable);

    /**
     * Devuelve los datos de un libro para rellenar el formulario de edición.
     *
     * @param id id del libro
     * @return los datos del libro
     */
    LibroUpdateDTO obtenerParaEditar(Long id);

    /**
     * Crea un libro nuevo y, si se adjunta, guarda su portada.
     *
     * @param dto         datos del formulario de alta
     * @param portadaFile imagen de la portada (opcional)
     */
    void crear(LibroCreateDTO dto, MultipartFile portadaFile);

    /**
     * Actualiza un libro que ya existe y, si se adjunta una imagen nueva, cambia su portada.
     *
     * @param dto         datos del formulario de edición
     * @param portadaFile imagen nueva de la portada (opcional)
     */
    void actualizar(LibroUpdateDTO dto, MultipartFile portadaFile);

    /**
     * Borra un libro.
     *
     * @param id id del libro que se quiere borrar
     */
    void borrar(Long id);

    /**
     * Devuelve el detalle de un libro con su categoría.
     *
     * @param id id del libro
     * @return el detalle del libro
     */
    LibroDetailDTO obtenerDetalle(Long id);
}

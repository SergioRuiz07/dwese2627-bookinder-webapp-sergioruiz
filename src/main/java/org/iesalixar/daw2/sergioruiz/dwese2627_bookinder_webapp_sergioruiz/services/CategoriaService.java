package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.services;

import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.CategoriaCreateDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.CategoriaDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.CategoriaDetailDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.CategoriaUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Servicio de Categoria: contiene la lógica del CRUD de categorías.
 * El controlador llama a este servicio y el servicio llama al DAO.
 */
public interface CategoriaService {

    /**
     * Devuelve una página de categorías para el listado.
     *
     * @param pageable número de página, tamaño y ordenación
     * @return página con las categorías
     */
    Page<CategoriaDTO> listar(Pageable pageable);

    /**
     * Devuelve todas las categorías (se usa para el desplegable del formulario de libros).
     *
     * @return lista con todas las categorías
     */
    List<CategoriaDTO> listarTodas();

    /**
     * Devuelve los datos de una categoría para rellenar el formulario de edición.
     *
     * @param id id de la categoría
     * @return los datos de la categoría
     */
    CategoriaUpdateDTO obtenerParaEditar(Long id);

    /**
     * Crea una categoría nueva.
     *
     * @param dto datos del formulario de alta
     */
    void crear(CategoriaCreateDTO dto);

    /**
     * Actualiza una categoría que ya existe.
     *
     * @param dto datos del formulario de edición
     */
    void actualizar(CategoriaUpdateDTO dto);

    /**
     * Borra una categoría.
     *
     * @param id id de la categoría que se quiere borrar
     */
    void borrar(Long id);

    /**
     * Devuelve el detalle de una categoría con sus libros.
     *
     * @param id id de la categoría
     * @return el detalle de la categoría
     */
    CategoriaDetailDTO obtenerDetalle(Long id);
}

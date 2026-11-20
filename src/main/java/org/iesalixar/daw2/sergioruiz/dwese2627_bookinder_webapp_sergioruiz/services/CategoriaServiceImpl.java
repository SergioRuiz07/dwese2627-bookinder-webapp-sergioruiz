package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.services;

import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.daos.CategoriaDAO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.CategoriaCreateDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.CategoriaDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.CategoriaDetailDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.CategoriaUpdateDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities.Categoria;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.mappers.CategoriaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de Categoria.
 * Usa el DAO para acceder a la base de datos y devuelve DTOs al controlador.
 */
@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    /**
     * DAO de categorías.
     */
    @Autowired
    private CategoriaDAO categoriaDAO;

    /**
     * Devuelve una página de categorías para el listado.
     *
     * @param pageable número de página, tamaño y ordenación
     * @return página con las categorías
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaDTO> listar(Pageable pageable) {
        // Sacamos el campo y la dirección de ordenación del Pageable
        String campoOrden = "nombre";
        String direccionOrden = "asc";
        if (pageable.getSort().isSorted()) {
            Sort.Order orden = pageable.getSort().iterator().next();
            campoOrden = orden.getProperty();
            direccionOrden = orden.getDirection().name().toLowerCase();
        }

        List<Categoria> categorias = categoriaDAO.listarCategoriasPaginadas(
                pageable.getPageNumber(), pageable.getPageSize(), campoOrden, direccionOrden);
        long total = categoriaDAO.contarCategorias();

        // Construimos el Page que espera la vista
        return new PageImpl<>(CategoriaMapper.aListaDTO(categorias), pageable, total);
    }

    /**
     * Devuelve todas las categorías para el desplegable del formulario de libros.
     *
     * @return lista con todas las categorías
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarTodas() {
        return CategoriaMapper.aListaDTO(categoriaDAO.listarCategorias());
    }

    /**
     * Devuelve los datos de una categoría para rellenar el formulario de edición.
     *
     * @param id id de la categoría
     * @return los datos de la categoría
     * @throws ResourceNotFoundException si la categoría no existe
     */
    @Override
    @Transactional(readOnly = true)
    public CategoriaUpdateDTO obtenerParaEditar(Long id) {
        Categoria categoria = categoriaDAO.obtenerCategoriaPorId(id);
        if (categoria == null) {
            throw new ResourceNotFoundException("categoria", "id", id);
        }
        return CategoriaMapper.aUpdateDTO(categoria);
    }

    /**
     * Crea una categoría nueva comprobando antes que el nombre no esté repetido.
     *
     * @param dto datos del formulario de alta
     * @throws DuplicateResourceException si ya existe una categoría con ese nombre
     */
    @Override
    public void crear(CategoriaCreateDTO dto) {
        if (categoriaDAO.existeCategoriaPorNombre(dto.getNombre().trim())) {
            throw new DuplicateResourceException("categoria", "nombre", dto.getNombre());
        }
        Categoria categoria = CategoriaMapper.aEntidad(dto);
        categoriaDAO.insertarCategoria(categoria);
    }

    /**
     * Actualiza una categoría comprobando que existe y que el nombre no lo usa otra categoría.
     *
     * @param dto datos del formulario de edición
     * @throws DuplicateResourceException si otra categoría ya tiene ese nombre
     * @throws ResourceNotFoundException  si la categoría no existe
     */
    @Override
    public void actualizar(CategoriaUpdateDTO dto) {
        if (categoriaDAO.existeCategoriaPorNombreYDistintoId(dto.getNombre().trim(), dto.getId())) {
            throw new DuplicateResourceException("categoria", "nombre", dto.getNombre());
        }

        Categoria categoria = categoriaDAO.obtenerCategoriaPorId(dto.getId());
        if (categoria == null) {
            throw new ResourceNotFoundException("categoria", "id", dto.getId());
        }

        CategoriaMapper.copiarAEntidadExistente(dto, categoria);
        categoriaDAO.actualizarCategoria(categoria);
    }

    /**
     * Borra una categoría. No se deja borrar si todavía tiene libros,
     * porque esos libros se quedarían sin categoría.
     *
     * @param id id de la categoría que se quiere borrar
     * @throws ResourceNotFoundException si la categoría no existe
     *
     * */
    @Override
    public void borrar(Long id) {
        Categoria categoria = categoriaDAO.obtenerCategoriaPorIdConLibros(id);
        if (categoria == null) {
            throw new ResourceNotFoundException("categoria", "id", id);
        }
        categoriaDAO.borrarCategoria(id);
    }

    /**
     * Devuelve el detalle de una categoría con sus libros (los trae con JOIN FETCH).
     *
     * @param id id de la categoría
     * @return el detalle de la categoría
     * @throws ResourceNotFoundException si la categoría no existe
     */
    @Override
    @Transactional(readOnly = true)
    public CategoriaDetailDTO obtenerDetalle(Long id) {
        Categoria categoria = categoriaDAO.obtenerCategoriaPorIdConLibros(id);
        if (categoria == null) {
            throw new ResourceNotFoundException("categoria", "id", id);
        }
        return CategoriaMapper.aDetalleDTO(categoria);
    }
}

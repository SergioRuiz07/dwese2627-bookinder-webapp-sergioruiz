package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.services;

import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.daos.CategoriaDAO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.daos.LibroDAO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.LibroCreateDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.LibroDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.LibroDetailDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.LibroUpdateDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities.Categoria;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities.Libro;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.exceptions.InvalidFileException;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.mappers.LibroMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Implementación del servicio de Libro.
 * Usa los DAOs para acceder a la base de datos y devuelve DTOs al controlador.
 * Desde un servicio se puede llamar a otros servicios: aquí se usa FileStorageService para las portadas.
 */
@Service
@Transactional
public class LibroServiceImpl implements LibroService {

    /**
     * Límite de tamaño máximo permitido para la portada (2 MB).
     */
    private static final long MAX_IMAGE_SIZE_BYTES = 2 * 1024 * 1024;

    /**
     * DAO de libros.
     */
    @Autowired
    private LibroDAO libroDAO;

    /**
     * DAO de categorías. Hace falta para cargar la categoría real al crear o editar un libro.
     */
    @Autowired
    private CategoriaDAO categoriaDAO;

    /**
     * Servicio para guardar y borrar las imágenes de las portadas.
     */
    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Devuelve una página de libros para el listado.
     *
     * @param pageable número de página, tamaño y ordenación
     * @return página con los libros
     */
    @Override
    @Transactional(readOnly = true)
    public Page<LibroDTO> listar(Pageable pageable) {
        // Sacamos el campo y la dirección de ordenación del Pageable
        String campoOrden = "titulo";
        String direccionOrden = "asc";
        if (pageable.getSort().isSorted()) {
            Sort.Order orden = pageable.getSort().iterator().next();
            campoOrden = orden.getProperty();
            direccionOrden = orden.getDirection().name().toLowerCase();
        }

        List<Libro> libros = libroDAO.listarLibrosPaginados(
                pageable.getPageNumber(), pageable.getPageSize(), campoOrden, direccionOrden);
        long total = libroDAO.contarLibros();

        // Construimos el Page que espera la vista
        return new PageImpl<>(LibroMapper.aListaDTO(libros), pageable, total);
    }

    /**
     * Devuelve los datos de un libro para rellenar el formulario de edición.
     *
     * @param id id del libro
     * @return los datos del libro
     * @throws ResourceNotFoundException si el libro no existe
     */
    @Override
    @Transactional(readOnly = true)
    public LibroUpdateDTO obtenerParaEditar(Long id) {
        Libro libro = libroDAO.obtenerLibroPorId(id);
        if (libro == null) {
            throw new ResourceNotFoundException("libro", "id", id);
        }
        return LibroMapper.aUpdateDTO(libro);
    }

    /**
     * Crea un libro nuevo comprobando que el ISBN no esté repetido y que la categoría exista.
     * Si se adjunta una portada, se valida (tipo y tamaño) y se guarda.
     *
     * @param dto         datos del formulario de alta
     * @param portadaFile imagen de la portada (opcional)
     * @throws DuplicateResourceException si ya existe un libro con ese ISBN
     * @throws ResourceNotFoundException  si la categoría elegida no existe
     * @throws InvalidFileException       si la imagen no es válida o no se puede guardar
     */
    @Override
    public void crear(LibroCreateDTO dto, MultipartFile portadaFile) {
        if (libroDAO.existeLibroPorIsbn(dto.getIsbn().trim())) {
            throw new DuplicateResourceException("libro", "isbn", dto.getIsbn());
        }

        // Si hay imagen, validar + guardar
        if (portadaFile != null && !portadaFile.isEmpty()) {
            // Validaciones (lanzan InvalidFileException)
            validarPortada(portadaFile);

            String newImageWebPath = fileStorageService.saveFile(portadaFile);
            if (newImageWebPath == null || newImageWebPath.isBlank()) {
                throw new InvalidFileException(
                        "libro",
                        "portadaFile",
                        portadaFile.getOriginalFilename(),
                        "No se pudo guardar la portada."
                );
            }
            dto.setUrlPortada(newImageWebPath);
        }

        Libro libro = LibroMapper.aEntidad(dto);
        libro.setCategoria(buscarCategoria(dto.getCategoriaId()));
        libroDAO.insertarLibro(libro);
    }

    /**
     * Actualiza un libro comprobando que existe, que el ISBN no lo usa otro libro
     * y que la categoría elegida existe.
     * Si se adjunta una portada nueva, se valida, se guarda y se borra la anterior.
     *
     * @param dto         datos del formulario de edición
     * @param portadaFile imagen nueva de la portada (opcional)
     * @throws DuplicateResourceException si otro libro ya tiene ese ISBN
     * @throws ResourceNotFoundException  si el libro o la categoría no existen
     * @throws InvalidFileException       si la imagen no es válida o no se puede guardar
     */
    @Override
    public void actualizar(LibroUpdateDTO dto, MultipartFile portadaFile) {
        if (libroDAO.existeLibroPorIsbnYDistintoId(dto.getIsbn().trim(), dto.getId())) {
            throw new DuplicateResourceException("libro", "isbn", dto.getIsbn());
        }

        Libro libro = libroDAO.obtenerLibroPorId(dto.getId());
        if (libro == null) {
            throw new ResourceNotFoundException("libro", "id", dto.getId());
        }

        // Si hay imagen nueva, validar + guardar + borrar la anterior
        if (portadaFile != null && !portadaFile.isEmpty()) {
            // Validaciones (lanzan InvalidFileException)
            validarPortada(portadaFile);

            String oldImagePath = dto.getUrlPortada(); // ruta actual (puede ser null o vacía)

            String newImageWebPath = fileStorageService.saveFile(portadaFile);
            if (newImageWebPath == null || newImageWebPath.isBlank()) {
                throw new InvalidFileException(
                        "libro",
                        "portadaFile",
                        portadaFile.getOriginalFilename(),
                        "No se pudo guardar la portada."
                );
            }
            dto.setUrlPortada(newImageWebPath);

            // Borrar la anterior si existía
            if (oldImagePath != null && !oldImagePath.isBlank()) {
                fileStorageService.deleteFile(oldImagePath);
            }
        }

        LibroMapper.copiarAEntidadExistente(dto, libro);
        libro.setCategoria(buscarCategoria(dto.getCategoriaId()));
        libroDAO.actualizarLibro(libro);
    }

    /**
     * Borra un libro comprobando antes que existe.
     *
     * @param id id del libro que se quiere borrar
     * @throws ResourceNotFoundException si el libro no existe
     */
    @Override
    public void borrar(Long id) {
        if (libroDAO.obtenerLibroPorId(id) == null) {
            throw new ResourceNotFoundException("libro", "id", id);
        }
        libroDAO.borrarLibro(id);
    }

    /**
     * Devuelve el detalle de un libro con su categoría.
     * Se hace dentro de la transacción para que la categoría (LAZY) se pueda cargar.
     *
     * @param id id del libro
     * @return el detalle del libro
     * @throws ResourceNotFoundException si el libro no existe
     */
    @Override
    @Transactional(readOnly = true)
    public LibroDetailDTO obtenerDetalle(Long id) {
        Libro libro = libroDAO.obtenerLibroPorId(id);
        if (libro == null) {
            throw new ResourceNotFoundException("libro", "id", id);
        }
        return LibroMapper.aDetalleDTO(libro);
    }

    /**
     * Valida la imagen de la portada:
     * - el tipo MIME tiene que empezar por image/
     * - el tamaño máximo es 2 MB
     *
     * @param file imagen subida
     * @throws InvalidFileException si no cumple las validaciones
     */
    private void validarPortada(MultipartFile file) {
        String contentType = file.getContentType();
        // Tipo no válido
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileException(
                    "libro",
                    "portadaFile",
                    contentType,
                    "Tipo de archivo no permitido"
            );
        }
        // Tamaño excedido
        if (file.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new InvalidFileException(
                    "libro",
                    "portadaFile",
                    file.getSize(),
                    "Archivo demasiado grande (máximo " + MAX_IMAGE_SIZE_BYTES + " bytes)"
            );
        }
    }

    /**
     * Busca la categoría por su id y lanza una excepción si no existe.
     *
     * @param categoriaId id de la categoría elegida en el formulario
     * @return la categoría encontrada
     * @throws ResourceNotFoundException si la categoría no existe
     */
    private Categoria buscarCategoria(Long categoriaId) {
        Categoria categoria = (categoriaId != null) ? categoriaDAO.obtenerCategoriaPorId(categoriaId) : null;
        if (categoria == null) {
            throw new ResourceNotFoundException("categoria", "id", categoriaId);
        }
        return categoria;
    }
}

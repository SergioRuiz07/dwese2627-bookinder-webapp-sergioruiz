package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.LibroCreateDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.LibroDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.LibroDetailDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.LibroUpdateDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.exceptions.InvalidFileException;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.services.CategoriaService;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.services.LibroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;
import java.util.Locale;

/**
 * Controlador que gestiona el CRUD de libros (listado, detalle, alta, edición y borrado).
 * Usa LibroService para la lógica y CategoriaService para rellenar el desplegable de categorías.
 */
@Controller
@RequestMapping("/libros")
public class LibroController {

    private static final Logger logger = LoggerFactory.getLogger(LibroController.class);

    /**
     * Sirve para leer los mensajes de los ficheros messages_es y messages_en según el idioma.
     */
    @Autowired
    private MessageSource messageSource;

    /**
     * Servicio con la lógica del CRUD de libros.
     */
    @Autowired
    private LibroService libroService;

    /**
     * Servicio de categorías, para el desplegable del formulario.
     */
    @Autowired
    private CategoriaService categoriaService;

    /**
     * Muestra el listado de libros paginado y ordenado, con el nombre de su categoría.
     *
     * @param pageable página, tamaño y ordenación (por defecto 10 por página ordenados por título)
     * @param model    modelo para pasar datos a la vista
     * @param locale   idioma actual
     * @return la vista del listado de libros
     */
    @GetMapping
    public String listarLibros(
            @PageableDefault(size = 10, sort = "titulo", direction = Sort.Direction.ASC) Pageable pageable,
            Model model,
            Locale locale) {

        logger.info("Listando libros page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        try {
            Page<LibroDTO> paginaLibros = libroService.listar(pageable);

            logger.info("Se han cargado {} libros en la página {}.",
                    paginaLibros.getNumberOfElements(), paginaLibros.getNumber());

            model.addAttribute("page", paginaLibros);

            // Para mantener la ordenación actual en los enlaces de la vista (sort=campo,asc|desc)
            String sortParam = "titulo,asc";
            if (paginaLibros.getSort().isSorted()) {
                Sort.Order orden = paginaLibros.getSort().iterator().next();
                sortParam = orden.getProperty() + "," + orden.getDirection().name().toLowerCase();
            }
            model.addAttribute("sortParam", sortParam);
        } catch (Exception e) {
            logger.error("Error al listar los libros: {}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.libro-controller.list.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/libro/libro-list";
    }

    /**
     * Muestra el formulario vacío para crear un libro, con el desplegable de categorías.
     *
     * @param model  modelo para pasar datos a la vista
     * @param locale idioma actual
     * @return la vista del formulario
     */
    @GetMapping("/new")
    public String mostrarFormularioNuevo(Model model, Locale locale) {
        logger.info("Mostrando formulario para nuevo libro.");
        try {
            model.addAttribute("libro", new LibroCreateDTO());
            model.addAttribute("listaCategorias", categoriaService.listarTodas());
        } catch (Exception e) {
            logger.error("Error al cargar las categorías para el formulario de libro: {}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.libro-controller.edit.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/libro/libro-form";
    }

    /**
     * Muestra el formulario de edición con los datos del libro y su categoría ya seleccionada.
     *
     * @param id                 id del libro que se quiere editar
     * @param model              modelo para pasar datos a la vista
     * @param redirectAttributes para enviar mensajes al redirigir
     * @param locale             idioma actual
     * @return la vista del formulario o una redirección al listado si hay error
     */
    @GetMapping("/edit")
    public String mostrarFormularioEditar(@RequestParam("id") Long id,
                                          Model model,
                                          RedirectAttributes redirectAttributes,
                                          Locale locale) {

        logger.info("Mostrando formulario de edición para el libro con ID {}", id);

        try {
            LibroUpdateDTO libroDTO = libroService.obtenerParaEditar(id);
            model.addAttribute("libro", libroDTO);
            model.addAttribute("listaCategorias", categoriaService.listarTodas());
            return "views/libro/libro-form";

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró el libro con ID {}", id);
            String msg = messageSource.getMessage("msg.libro-controller.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/libros";

        } catch (Exception e) {
            logger.error("Error al obtener el libro con ID {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.libro-controller.edit.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/libros";
        }
    }

    /**
     * Guarda un libro nuevo.
     * Primero se comprueban las validaciones del DTO, después la validación propia del año
     * y por último el servicio comprueba que el ISBN no esté repetido y que la categoría exista.
     *
     * @param libroDTO           datos del formulario
     * @param result             errores de validación del formulario
     * @param portadaFile        imagen de la portada (opcional)
     * @param redirectAttributes para enviar mensajes al redirigir
     * @param model              modelo para pasar datos a la vista
     * @param locale             idioma actual
     * @return redirección al listado o la vista del formulario si hay errores
     */
    @PostMapping("/insert")
    public String insertarLibro(@Valid @ModelAttribute("libro") LibroCreateDTO libroDTO,
                                BindingResult result,
                                @RequestParam(value = "portadaFile", required = false) MultipartFile portadaFile,
                                RedirectAttributes redirectAttributes,
                                Model model,
                                Locale locale) {

        logger.info("Insertando nuevo libro con ISBN {}", libroDTO.getIsbn());

        // Validación propia: el año de publicación no puede ser posterior al año actual
        validarAnioPublicacion(libroDTO.getAnioPublicacion(), result);

        // Si hay errores, se vuelve al formulario (hay que volver a cargar las categorías del select)
        if (result.hasErrors()) {
            model.addAttribute("listaCategorias", categoriaService.listarTodas());
            return "views/libro/libro-form";
        }

        try {
            libroService.crear(libroDTO, portadaFile);
            logger.info("Libro {} insertado con éxito.", libroDTO.getIsbn());
            return "redirect:/libros";

        } catch (DuplicateResourceException ex) {
            // El ISBN ya existe: se marca el error en el propio campo del formulario
            logger.warn("El ISBN {} ya existe.", libroDTO.getIsbn());
            result.rejectValue("isbn", "msg.libro-controller.isbnExiste");
            model.addAttribute("listaCategorias", categoriaService.listarTodas());
            return "views/libro/libro-form";

        } catch (ResourceNotFoundException ex) {
            // La categoría elegida no existe (por ejemplo, porque se ha borrado mientras tanto)
            logger.warn("La categoría con ID {} no existe.", libroDTO.getCategoriaId());
            result.rejectValue("categoriaId", "msg.libro-controller.categoriaNotFound");
            model.addAttribute("listaCategorias", categoriaService.listarTodas());
            return "views/libro/libro-form";

        } catch (InvalidFileException ex) {
            logger.warn("Portada no válida: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.libro.image.invalid", null, locale));
            return "redirect:/libros/new";

        } catch (Exception e) {
            logger.error("Error al insertar el libro {}: {}", libroDTO.getIsbn(), e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.libro-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/libros/new";
        }
    }

    /**
     * Actualiza un libro que ya existe.
     *
     * @param libroDTO           datos del formulario
     * @param result             errores de validación del formulario
     * @param portadaFile        imagen nueva de la portada (opcional)
     * @param redirectAttributes para enviar mensajes al redirigir
     * @param model              modelo para pasar datos a la vista
     * @param locale             idioma actual
     * @return redirección al listado o la vista del formulario si hay errores
     */
    @PostMapping("/update")
    public String actualizarLibro(@Valid @ModelAttribute("libro") LibroUpdateDTO libroDTO,
                                  BindingResult result,
                                  @RequestParam(value = "portadaFile", required = false) MultipartFile portadaFile,
                                  RedirectAttributes redirectAttributes,
                                  Model model,
                                  Locale locale) {

        logger.info("Actualizando libro con ID {}", libroDTO.getId());

        // Validación propia: el año de publicación no puede ser posterior al año actual
        validarAnioPublicacion(libroDTO.getAnioPublicacion(), result);

        if (result.hasErrors()) {
            model.addAttribute("listaCategorias", categoriaService.listarTodas());
            return "views/libro/libro-form";
        }

        try {
            libroService.actualizar(libroDTO, portadaFile);
            logger.info("Libro con ID {} actualizado con éxito.", libroDTO.getId());
            return "redirect:/libros";

        } catch (DuplicateResourceException ex) {
            logger.warn("El ISBN {} ya lo usa otro libro.", libroDTO.getIsbn());
            result.rejectValue("isbn", "msg.libro-controller.isbnExiste");
            model.addAttribute("listaCategorias", categoriaService.listarTodas());
            return "views/libro/libro-form";

        } catch (ResourceNotFoundException ex) {
            // Puede faltar el libro o la categoría: se comprueba cuál ha sido
            if ("categoria".equals(ex.getResource())) {
                logger.warn("La categoría con ID {} no existe.", libroDTO.getCategoriaId());
                result.rejectValue("categoriaId", "msg.libro-controller.categoriaNotFound");
                model.addAttribute("listaCategorias", categoriaService.listarTodas());
                return "views/libro/libro-form";
            }
            logger.warn("No se encontró el libro con ID {}", libroDTO.getId());
            String msg = messageSource.getMessage("msg.libro-controller.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/libros";

        } catch (InvalidFileException ex) {
            logger.warn("Portada no válida: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.libro.image.invalid", null, locale));
            return "redirect:/libros/edit?id=" + libroDTO.getId();

        } catch (Exception e) {
            logger.error("Error al actualizar el libro con ID {}: {}", libroDTO.getId(), e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.libro-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/libros/edit?id=" + libroDTO.getId();
        }
    }

    /**
     * Borra un libro.
     *
     * @param id                 id del libro que se quiere borrar
     * @param redirectAttributes para enviar mensajes al redirigir
     * @param locale             idioma actual
     * @return redirección al listado
     */
    @PostMapping("/delete")
    public String borrarLibro(@RequestParam("id") Long id,
                              RedirectAttributes redirectAttributes,
                              Locale locale) {

        logger.info("Eliminando libro con ID {}", id);

        try {
            libroService.borrar(id);
            logger.info("Libro con ID {} eliminado con éxito.", id);

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró el libro con ID {}", id);
            String msg = messageSource.getMessage("msg.libro-controller.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);

        } catch (Exception e) {
            logger.error("Error al eliminar el libro con ID {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.libro-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
        }
        return "redirect:/libros";
    }

    /**
     * Muestra el detalle de un libro con los datos de su categoría.
     *
     * @param id                 id del libro
     * @param model              modelo para pasar datos a la vista
     * @param redirectAttributes para enviar mensajes al redirigir
     * @param locale             idioma actual
     * @return la vista de detalle o una redirección al listado si hay error
     */
    @GetMapping("/detail")
    public String mostrarDetalle(@RequestParam("id") Long id,
                                 Model model,
                                 RedirectAttributes redirectAttributes,
                                 Locale locale) {

        logger.info("Mostrando detalle del libro con ID {}", id);

        try {
            LibroDetailDTO libroDTO = libroService.obtenerDetalle(id);
            model.addAttribute("libro", libroDTO);
            return "views/libro/libro-detail";

        } catch (ResourceNotFoundException ex) {
            String msg = messageSource.getMessage("msg.libro-controller.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/libros";

        } catch (Exception e) {
            logger.error("Error al obtener el detalle del libro {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.libro-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/libros";
        }
    }

    /**
     * Validación propia: el año de publicación no puede ser mayor que el año actual.
     * Si no la cumple, se añade el error al campo anioPublicacion del formulario.
     *
     * @param anio   año escrito en el formulario (puede venir vacío)
     * @param result errores del formulario
     */
    private void validarAnioPublicacion(Integer anio, BindingResult result) {
        if (anio != null && anio > Year.now().getValue()) {
            result.rejectValue("anioPublicacion", "msg.libro.anioPublicacion.futuro");
        }
    }
}

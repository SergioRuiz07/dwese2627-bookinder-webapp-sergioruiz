package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.CategoriaCreateDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.CategoriaDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.CategoriaDetailDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.dtos.CategoriaUpdateDTO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.services.CategoriaService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

/**
 * Controlador que gestiona el CRUD de categorías (listado, detalle, alta, edición y borrado).
 * Usa CategoriaService para la lógica y devuelve las vistas de la carpeta views/categoria.
 */
@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaController.class);

    /**
     * Sirve para leer los mensajes de los ficheros messages_es y messages_en según el idioma.
     */
    @Autowired
    private MessageSource messageSource;

    /**
     * Servicio con la lógica del CRUD de categorías.
     */
    @Autowired
    private CategoriaService categoriaService;

    /**
     * Muestra el listado de categorías paginado y ordenado.
     *
     * @param pageable página, tamaño y ordenación (por defecto 10 por página ordenadas por nombre)
     * @param model    modelo para pasar datos a la vista
     * @param locale   idioma actual
     * @return la vista del listado de categorías
     */
    @GetMapping
    public String listarCategorias(
            @PageableDefault(size = 10, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable,
            Model model,
            Locale locale) {

        logger.info("Listando categorías page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        try {
            Page<CategoriaDTO> paginaCategorias = categoriaService.listar(pageable);

            logger.info("Se han cargado {} categorías en la página {}.",
                    paginaCategorias.getNumberOfElements(), paginaCategorias.getNumber());

            model.addAttribute("page", paginaCategorias);

            // Para mantener la ordenación actual en los enlaces de la vista (sort=campo,asc|desc)
            String sortParam = "nombre,asc";
            if (paginaCategorias.getSort().isSorted()) {
                Sort.Order orden = paginaCategorias.getSort().iterator().next();
                sortParam = orden.getProperty() + "," + orden.getDirection().name().toLowerCase();
            }
            model.addAttribute("sortParam", sortParam);
        } catch (Exception e) {
            logger.error("Error al listar las categorías: {}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.categoria-controller.list.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/categoria/categoria-list";
    }

    /**
     * Muestra el formulario vacío para crear una categoría.
     *
     * @param model modelo para pasar datos a la vista
     * @return la vista del formulario
     */
    @GetMapping("/new")
    public String mostrarFormularioNuevo(Model model) {
        logger.info("Mostrando formulario para nueva categoría.");
        model.addAttribute("categoria", new CategoriaCreateDTO());
        return "views/categoria/categoria-form";
    }

    /**
     * Muestra el formulario de edición con los datos de la categoría.
     *
     * @param id                 id de la categoría que se quiere editar
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

        logger.info("Mostrando formulario de edición para la categoría con ID {}", id);

        try {
            CategoriaUpdateDTO categoriaDTO = categoriaService.obtenerParaEditar(id);
            model.addAttribute("categoria", categoriaDTO);
            return "views/categoria/categoria-form";

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró la categoría con ID {}", id);
            String msg = messageSource.getMessage("msg.categoria-controller.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/categorias";

        } catch (Exception e) {
            logger.error("Error al obtener la categoría con ID {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.categoria-controller.edit.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/categorias";
        }
    }

    /**
     * Guarda una categoría nueva.
     * Primero se comprueban las validaciones del DTO, después la validación propia del nombre
     * y por último el servicio comprueba que el nombre no esté repetido.
     *
     * @param categoriaDTO       datos del formulario
     * @param result             errores de validación del formulario
     * @param redirectAttributes para enviar mensajes al redirigir
     * @param locale             idioma actual
     * @return redirección al listado o la vista del formulario si hay errores
     */
    @PostMapping("/insert")
    public String insertarCategoria(@Valid @ModelAttribute("categoria") CategoriaCreateDTO categoriaDTO,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    Locale locale) {

        logger.info("Insertando nueva categoría con nombre {}", categoriaDTO.getNombre());

        // Validación propia: el nombre tiene que tener al menos una letra
        validarNombre(categoriaDTO.getNombre(), result);

        // Si hay errores de validación, se vuelve al formulario para mostrarlos
        if (result.hasErrors()) {
            return "views/categoria/categoria-form";
        }

        try {
            categoriaService.crear(categoriaDTO);
            logger.info("Categoría {} insertada con éxito.", categoriaDTO.getNombre());
            return "redirect:/categorias";

        } catch (DuplicateResourceException ex) {
            // El nombre ya existe: se marca el error en el propio campo del formulario
            logger.warn("El nombre de la categoría {} ya existe.", categoriaDTO.getNombre());
            result.rejectValue("nombre", "msg.categoria-controller.nombreExiste");
            return "views/categoria/categoria-form";

        } catch (Exception e) {
            logger.error("Error al insertar la categoría {}: {}", categoriaDTO.getNombre(), e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.categoria-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/categorias/new";
        }
    }

    /**
     * Actualiza una categoría que ya existe.
     *
     * @param categoriaDTO       datos del formulario
     * @param result             errores de validación del formulario
     * @param redirectAttributes para enviar mensajes al redirigir
     * @param locale             idioma actual
     * @return redirección al listado o la vista del formulario si hay errores
     */
    @PostMapping("/update")
    public String actualizarCategoria(@Valid @ModelAttribute("categoria") CategoriaUpdateDTO categoriaDTO,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes,
                                      Locale locale) {

        logger.info("Actualizando categoría con ID {}", categoriaDTO.getId());

        // Validación propia: el nombre tiene que tener al menos una letra
        validarNombre(categoriaDTO.getNombre(), result);

        if (result.hasErrors()) {
            return "views/categoria/categoria-form";
        }

        try {
            categoriaService.actualizar(categoriaDTO);
            logger.info("Categoría con ID {} actualizada con éxito.", categoriaDTO.getId());
            return "redirect:/categorias";

        } catch (DuplicateResourceException ex) {
            logger.warn("El nombre {} ya lo usa otra categoría.", categoriaDTO.getNombre());
            result.rejectValue("nombre", "msg.categoria-controller.nombreExiste");
            return "views/categoria/categoria-form";

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró la categoría con ID {}", categoriaDTO.getId());
            String msg = messageSource.getMessage("msg.categoria-controller.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/categorias";

        } catch (Exception e) {
            logger.error("Error al actualizar la categoría con ID {}: {}", categoriaDTO.getId(), e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.categoria-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/categorias/edit?id=" + categoriaDTO.getId();
        }
    }

    /**
     * Borra una categoría. Si todavía tiene libros no se borra y se muestra un aviso.
     *
     * @param id                 id de la categoría que se quiere borrar
     * @param redirectAttributes para enviar mensajes al redirigir
     * @param locale             idioma actual
     * @return redirección al listado
     */
    @PostMapping("/delete")
    public String borrarCategoria(@RequestParam("id") Long id,
                                  RedirectAttributes redirectAttributes,
                                  Locale locale) {

        logger.info("Eliminando categoría con ID {}", id);

        try {
            categoriaService.borrar(id);
            logger.info("Categoría con ID {} eliminada con éxito.", id);

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró la categoría con ID {}", id);
            String msg = messageSource.getMessage("msg.categoria-controller.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
        } catch (Exception e) {
            logger.error("Error al eliminar la categoría con ID {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.categoria-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
        }
        return "redirect:/categorias";
    }
    /**
     * Muestra el detalle de una categoría con la lista de sus libros.
     *
     * @param id                 id de la categoría
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

        logger.info("Mostrando detalle de la categoría con ID {}", id);

        try {
            CategoriaDetailDTO categoriaDTO = categoriaService.obtenerDetalle(id);
            model.addAttribute("categoria", categoriaDTO);
            return "views/categoria/categoria-detail";

        } catch (ResourceNotFoundException ex) {
            String msg = messageSource.getMessage("msg.categoria-controller.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/categorias";

        } catch (Exception e) {
            logger.error("Error al obtener el detalle de la categoría {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.categoria-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/categorias";
        }
    }

    /**
     * Validación propia: el nombre de la categoría tiene que tener al menos una letra
     * (no se permiten nombres como "123" o "---").
     * Si no la cumple, se añade el error al campo nombre del formulario.
     *
     * @param nombre nombre escrito en el formulario
     * @param result errores del formulario
     */
    private void validarNombre(String nombre, BindingResult result) {
        if (nombre != null && !nombre.isBlank() && !nombre.matches(".*\\p{L}.*")) {
            result.rejectValue("nombre", "msg.categoria.nombre.letras");
        }
    }
}

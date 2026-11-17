package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.controllers;

import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.daos.LibroDAO;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities.Libro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/libros")
public class LibroController {
    @Autowired
    private LibroDAO libroDAO;

    private static final Logger logger = LoggerFactory.getLogger(LibroController.class);

    @Autowired
    private MessageSource messageSource;

    // Servicio para las operaciones del CRUD de Region
    @Autowired
    private LibroService libroService;
}

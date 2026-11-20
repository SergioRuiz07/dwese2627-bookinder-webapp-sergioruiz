package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador de la página de inicio de la aplicación.
 */
@Controller
public class HomeController {

    /**
     * Muestra la página de inicio.
     *
     * @return la vista index
     */
    @GetMapping("/")
    public String inicio() {
        return "index";
    }
}

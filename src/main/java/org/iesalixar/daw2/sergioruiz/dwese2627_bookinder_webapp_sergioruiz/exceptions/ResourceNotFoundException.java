package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.exceptions;

import lombok.Getter;

/**
 * Excepción que se lanza cuando se busca un dato que no existe en la base de datos
 * (por ejemplo, un libro con un id que no está guardado).
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Nombre del recurso afectado (por ejemplo "libro" o "categoria").
     */
    private final String resource;

    /**
     * Campo por el que se ha detectado el problema (por ejemplo "isbn" o "id").
     */
    private final String field;

    /**
     * Valor que ha provocado el problema.
     */
    private final Object value;

    /**
     * Crea la excepción con los datos del problema.
     *
     * @param resource nombre del recurso afectado
     * @param field    campo que da el problema
     * @param value    valor que da el problema
     */
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(resource + " no encontrado (" + field + "=" + value + ")");
        this.resource = resource;
        this.field = field;
        this.value = value;
    }
}

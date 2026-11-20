package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.daos;

import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities.Libro;

import java.util.List;

/**
 * DAO de Libro: define las operaciones de acceso a la base de datos para los libros.
 * Sigue el mismo modelo que ProvinceDAO del proyecto ticket-logger (lado N de la relación).
 */
public interface LibroDAO {

    /**
     * Devuelve todos los libros junto con su categoría.
     *
     * @return lista con todos los libros
     */
    List<Libro> listarLibros();

    /**
     * Guarda un libro nuevo en la base de datos.
     *
     * @param libro libro que se quiere guardar
     */
    void insertarLibro(Libro libro);

    /**
     * Actualiza un libro que ya existe.
     *
     * @param libro libro con los datos nuevos
     */
    void actualizarLibro(Libro libro);

    /**
     * Borra el libro con el id indicado.
     *
     * @param id id del libro que se quiere borrar
     */
    void borrarLibro(Long id);

    /**
     * Busca un libro por su id.
     *
     * @param id id del libro
     * @return el libro encontrado o null si no existe
     */
    Libro obtenerLibroPorId(Long id);

    /**
     * Comprueba si ya existe un libro con ese ISBN (se usa al crear).
     *
     * @param isbn ISBN que se quiere comprobar
     * @return true si ya existe un libro con ese ISBN
     */
    boolean existeLibroPorIsbn(String isbn);

    /**
     * Comprueba si existe otro libro con ese ISBN sin contar el que se está editando (se usa al actualizar).
     *
     * @param isbn ISBN que se quiere comprobar
     * @param id   id del libro que se está editando
     * @return true si otro libro ya usa ese ISBN
     */
    boolean existeLibroPorIsbnYDistintoId(String isbn, Long id);

    /**
     * Devuelve una página de libros (con su categoría) ordenada por el campo indicado.
     *
     * @param pagina         número de página (empieza en 0)
     * @param tamanio        número de libros por página
     * @param campoOrden     campo por el que se ordena
     * @param direccionOrden asc o desc
     * @return lista con los libros de esa página
     */
    List<Libro> listarLibrosPaginados(int pagina, int tamanio, String campoOrden, String direccionOrden);

    /**
     * Cuenta cuántos libros hay en total (para calcular el número de páginas).
     *
     * @return número total de libros
     */
    long contarLibros();
}

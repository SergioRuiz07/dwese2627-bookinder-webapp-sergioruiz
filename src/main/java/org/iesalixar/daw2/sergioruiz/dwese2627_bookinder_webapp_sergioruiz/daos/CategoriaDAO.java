package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.daos;

import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities.Categoria;

import java.util.List;

/**
 * DAO de Categoria: define las operaciones de acceso a la base de datos para las categorías.
 * Sigue el mismo modelo que RegionDAO del proyecto ticket-logger (lado 1 de la relación).
 */
public interface CategoriaDAO {

    /**
     * Devuelve todas las categorías ordenadas por nombre.
     *
     * @return lista con todas las categorías
     */
    List<Categoria> listarCategorias();

    /**
     * Guarda una categoría nueva en la base de datos.
     *
     * @param categoria categoría que se quiere guardar
     */
    void insertarCategoria(Categoria categoria);

    /**
     * Actualiza una categoría que ya existe.
     *
     * @param categoria categoría con los datos nuevos
     */
    void actualizarCategoria(Categoria categoria);

    /**
     * Borra la categoría con el id indicado.
     *
     * @param id id de la categoría que se quiere borrar
     */
    void borrarCategoria(Long id);

    /**
     * Busca una categoría por su id.
     *
     * @param id id de la categoría
     * @return la categoría encontrada o null si no existe
     */
    Categoria obtenerCategoriaPorId(Long id);

    /**
     * Busca una categoría por su id y trae también sus libros en la misma consulta (JOIN FETCH).
     *
     * @param id id de la categoría
     * @return la categoría con sus libros o null si no existe
     */
    Categoria obtenerCategoriaPorIdConLibros(Long id);

    /**
     * Comprueba si ya existe una categoría con ese nombre (se usa al crear).
     *
     * @param nombre nombre que se quiere comprobar
     * @return true si ya existe una categoría con ese nombre
     */
    boolean existeCategoriaPorNombre(String nombre);

    /**
     * Comprueba si existe otra categoría con ese nombre sin contar la que se está editando (se usa al actualizar).
     *
     * @param nombre nombre que se quiere comprobar
     * @param id     id de la categoría que se está editando
     * @return true si otra categoría ya usa ese nombre
     */
    boolean existeCategoriaPorNombreYDistintoId(String nombre, Long id);

    /**
     * Devuelve una página de categorías ordenada por el campo indicado.
     *
     * @param pagina         número de página (empieza en 0)
     * @param tamanio        número de categorías por página
     * @param campoOrden     campo por el que se ordena (id o nombre)
     * @param direccionOrden asc o desc
     * @return lista con las categorías de esa página
     */
    List<Categoria> listarCategoriasPaginadas(int pagina, int tamanio, String campoOrden, String direccionOrden);

    /**
     * Cuenta cuántas categorías hay en total (para calcular el número de páginas).
     *
     * @return número total de categorías
     */
    long contarCategorias();
}

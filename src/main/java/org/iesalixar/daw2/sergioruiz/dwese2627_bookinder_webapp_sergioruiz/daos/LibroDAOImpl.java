package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities.Categoria;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities.Libro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implementación del DAO de Libro usando JPA (EntityManager).
 */
@Repository
@Transactional
public class LibroDAOImpl implements LibroDAO {

    private static final Logger logger = LoggerFactory.getLogger(LibroDAOImpl.class);

    /**
     * EntityManager de JPA con el que se hacen las consultas a la base de datos.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Devuelve todos los libros ordenados por título.
     * Con JOIN FETCH la categoría de cada libro viene cargada en la misma consulta.
     *
     * @return lista con todos los libros
     */
    @Override
    public List<Libro> listarLibros() {
        logger.info("Listando todos los libros con su categoría.");
        String jpql = "SELECT l FROM Libro l JOIN FETCH l.categoria ORDER BY l.titulo";
        List<Libro> libros = entityManager.createQuery(jpql, Libro.class).getResultList();
        logger.info("Se han obtenido {} libros de la base de datos.", libros.size());
        return libros;
    }

    /**
     * Guarda un libro nuevo en la base de datos.
     *
     * @param libro libro que se quiere guardar
     */
    @Override
    public void insertarLibro(Libro libro) {
        logger.info("Insertando libro con ISBN {}, título {} y categoría {}",
                libro.getIsbn(),
                libro.getTitulo(),
                libro.getCategoria() != null ? libro.getCategoria().getId() : null);
        entityManager.persist(libro);
        logger.info("Libro insertado correctamente con id {}", libro.getId());
    }

    /**
     * Actualiza un libro que ya existe.
     *
     * @param libro libro con los datos nuevos
     */
    @Override
    public void actualizarLibro(Libro libro) {
        logger.info("Actualizando libro con id {}", libro.getId());
        entityManager.merge(libro);
        logger.info("Libro con id {} actualizado correctamente", libro.getId());
    }

    /**
     * Borra el libro con el id indicado. Si no existe, solo lo indica en el log.
     *
     * @param id id del libro que se quiere borrar
     */
    @Override
    public void borrarLibro(Long id) {
        logger.info("Borrando libro con id {}", id);
        Libro libro = entityManager.find(Libro.class, id);
        if (libro != null) {
            entityManager.remove(libro);
            logger.info("El libro con id {} ha sido borrado correctamente", id);
        } else {
            logger.warn("No existe ningún libro con el id {}", id);
        }
    }

    /**
     * Busca un libro por su id.
     *
     * @param id id del libro
     * @return el libro encontrado o null si no existe
     */
    @Override
    public Libro obtenerLibroPorId(Long id) {
        logger.info("Buscando libro con id {}", id);
        Libro libro = entityManager.find(Libro.class, id);
        if (libro != null) {
            logger.info("Libro encontrado: {} - {}", libro.getId(), libro.getTitulo());
        } else {
            logger.warn("No existe el libro con id {}", id);
        }
        return libro;
    }

    /**
     * Comprueba si ya existe un libro con ese ISBN (sin distinguir mayúsculas y minúsculas).
     *
     * @param isbn ISBN que se quiere comprobar
     * @return true si ya existe un libro con ese ISBN
     */
    @Override
    public boolean existeLibroPorIsbn(String isbn) {
        logger.info("Comprobando si existe un libro con ISBN {}", isbn);
        String jpql = "SELECT COUNT(l) FROM Libro l WHERE UPPER(l.isbn) = :isbn";
        Long total = entityManager.createQuery(jpql, Long.class)
                .setParameter("isbn", isbn.toUpperCase())
                .getSingleResult();
        boolean existe = total != null && total > 0;
        logger.info("¿Existe un libro con ISBN {}? {}", isbn, existe);
        return existe;
    }

    /**
     * Comprueba si existe otro libro con ese ISBN sin contar el que se está editando.
     *
     * @param isbn ISBN que se quiere comprobar
     * @param id   id del libro que se está editando
     * @return true si otro libro ya usa ese ISBN
     */
    @Override
    public boolean existeLibroPorIsbnYDistintoId(String isbn, Long id) {
        logger.info("Comprobando si existe otro libro con ISBN {} sin contar el de id {}", isbn, id);
        String jpql = "SELECT COUNT(l) FROM Libro l WHERE UPPER(l.isbn) = :isbn AND l.id <> :id";
        Long total = entityManager.createQuery(jpql, Long.class)
                .setParameter("isbn", isbn.toUpperCase())
                .setParameter("id", id)
                .getSingleResult();
        boolean existe = total != null && total > 0;
        logger.info("¿Existe otro libro con ISBN {} (sin contar el id {})? {}", isbn, id, existe);
        return existe;
    }

    /**
     * Devuelve una página de libros ordenada por el campo indicado.
     * La categoría se carga con fetch para poder mostrar su nombre en el listado,
     * y se puede ordenar también por el nombre de la categoría.
     *
     * @param pagina         número de página (empieza en 0)
     * @param tamanio        número de libros por página
     * @param campoOrden     campo por el que se ordena
     * @param direccionOrden asc o desc
     * @return lista con los libros de esa página
     */
    @Override
    public List<Libro> listarLibrosPaginados(int pagina, int tamanio, String campoOrden, String direccionOrden) {
        logger.info("Listando libros: página={}, tamaño={}, campoOrden={}, dirección={}",
                pagina, tamanio, campoOrden, direccionOrden);
        int offset = pagina * tamanio;

        // 1. Construcción de Criteria
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Libro> cq = cb.createQuery(Libro.class);
        Root<Libro> root = cq.from(Libro.class);

        // Cargamos la categoría junto con el libro (equivale al JOIN FETCH)
        root.fetch("categoria", JoinType.INNER);

        // Join aparte para poder ordenar por el nombre de la categoría
        Join<Libro, Categoria> categoriaJoin = root.join("categoria", JoinType.INNER);

        // 2. Campos por los que se permite ordenar (lista blanca)
        Path<?> rutaOrden;
        switch (campoOrden) {
            case "id" -> rutaOrden = root.get("id");
            case "isbn" -> rutaOrden = root.get("isbn");
            case "titulo" -> rutaOrden = root.get("titulo");
            case "autor" -> rutaOrden = root.get("autor");
            case "anioPublicacion" -> rutaOrden = root.get("anioPublicacion");
            case "categoriaNombre", "categoria.nombre" -> rutaOrden = categoriaJoin.get("nombre");
            default -> {
                logger.warn("Campo de ordenación '{}' no válido, se ordena por 'titulo'.", campoOrden);
                rutaOrden = root.get("titulo");
            }
        }

        // 3. Dirección de ordenación
        boolean descendente = "desc".equalsIgnoreCase(direccionOrden);
        Order orden = descendente ? cb.desc(rutaOrden) : cb.asc(rutaOrden);

        // 4. Aplicar la ordenación a la consulta
        cq.select(root).orderBy(orden);

        // 5. Crear la consulta, aplicar la paginación y ejecutarla
        List<Libro> libros = entityManager.createQuery(cq)
                .setFirstResult(offset)
                .setMaxResults(tamanio)
                .getResultList();
        logger.info("Se han obtenido {} libros en la página {}", libros.size(), pagina);
        return libros;
    }

    /**
     * Cuenta cuántos libros hay en total.
     *
     * @return número total de libros
     */
    @Override
    public long contarLibros() {
        logger.info("Contando el total de libros de la base de datos.");
        String jpql = "SELECT COUNT(l) FROM Libro l";
        Long total = entityManager.createQuery(jpql, Long.class).getSingleResult();
        logger.info("Total de libros: {}", total);
        return (total != null) ? total : 0L;
    }
}

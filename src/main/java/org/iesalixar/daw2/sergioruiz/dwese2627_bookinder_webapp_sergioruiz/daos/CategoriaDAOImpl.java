package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities.Categoria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implementación del DAO de Categoria usando JPA (EntityManager).
 */
@Repository
@Transactional
public class CategoriaDAOImpl implements CategoriaDAO {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaDAOImpl.class);

    /**
     * EntityManager de JPA con el que se hacen las consultas a la base de datos.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Devuelve todas las categorías ordenadas por nombre.
     *
     * @return lista con todas las categorías
     */
    @Override
    public List<Categoria> listarCategorias() {
        logger.info("Listando todas las categorías de la base de datos.");
        String jpql = "SELECT c FROM Categoria c ORDER BY c.nombre";
        List<Categoria> categorias = entityManager.createQuery(jpql, Categoria.class).getResultList();
        logger.info("Se han obtenido {} categorías de la base de datos.", categorias.size());
        return categorias;
    }

    /**
     * Guarda una categoría nueva en la base de datos.
     *
     * @param categoria categoría que se quiere guardar
     */
    @Override
    public void insertarCategoria(Categoria categoria) {
        logger.info("Insertando categoría con nombre {}", categoria.getNombre());
        entityManager.persist(categoria);
        logger.info("Categoría insertada correctamente con id {}", categoria.getId());
    }

    /**
     * Actualiza una categoría que ya existe.
     *
     * @param categoria categoría con los datos nuevos
     */
    @Override
    public void actualizarCategoria(Categoria categoria) {
        logger.info("Actualizando categoría con id {}", categoria.getId());
        entityManager.merge(categoria);
        logger.info("Categoría con id {} actualizada correctamente", categoria.getId());
    }

    /**
     * Borra la categoría con el id indicado. Si no existe, solo lo indica en el log.
     *
     * @param id id de la categoría que se quiere borrar
     */
    @Override
    public void borrarCategoria(Long id) {
        logger.info("Borrando categoría con id {}", id);
        Categoria categoria = entityManager.find(Categoria.class, id);
        if (categoria != null) {
            entityManager.remove(categoria);
            logger.info("La categoría con id {} ha sido borrada correctamente", id);
        } else {
            logger.warn("No existe ninguna categoría con el id {}", id);
        }
    }

    /**
     * Busca una categoría por su id.
     *
     * @param id id de la categoría
     * @return la categoría encontrada o null si no existe
     */
    @Override
    public Categoria obtenerCategoriaPorId(Long id) {
        logger.info("Buscando categoría con id {}", id);
        Categoria categoria = entityManager.find(Categoria.class, id);
        if (categoria != null) {
            logger.info("Categoría encontrada con id {}", id);
        } else {
            logger.warn("No existe la categoría con id {}", id);
        }
        return categoria;
    }

    /**
     * Busca una categoría por su id y trae también sus libros en la misma consulta.
     * Lleva DISTINCT porque al hacer JOIN con una lista (libros) la categoría saldría repetida.
     *
     * @param id id de la categoría
     * @return la categoría con sus libros o null si no existe
     */
    @Override
    public Categoria obtenerCategoriaPorIdConLibros(Long id) {
        logger.info("Buscando categoría con sus libros por id {}", id);
        String jpql = "SELECT DISTINCT c FROM Categoria c LEFT JOIN FETCH c.libros WHERE c.id = :id";
        List<Categoria> resultado = entityManager.createQuery(jpql, Categoria.class)
                .setParameter("id", id)
                .getResultList();
        if (resultado.isEmpty()) {
            logger.warn("No se ha encontrado la categoría con id {}", id);
            return null;
        }
        logger.info("Categoría con id {} encontrada con {} libros", id, resultado.get(0).getLibros().size());
        return resultado.get(0);
    }

    /**
     * Comprueba si ya existe una categoría con ese nombre (sin distinguir mayúsculas y minúsculas).
     *
     * @param nombre nombre que se quiere comprobar
     * @return true si ya existe una categoría con ese nombre
     */
    @Override
    public boolean existeCategoriaPorNombre(String nombre) {
        logger.info("Comprobando si existe una categoría con nombre {}", nombre);
        String jpql = "SELECT COUNT(c) FROM Categoria c WHERE UPPER(c.nombre) = :nombre";
        Long total = entityManager.createQuery(jpql, Long.class)
                .setParameter("nombre", nombre.toUpperCase())
                .getSingleResult();
        boolean existe = total != null && total > 0;
        logger.info("¿Existe una categoría con nombre {}? {}", nombre, existe);
        return existe;
    }

    /**
     * Comprueba si existe otra categoría con ese nombre sin contar la que se está editando.
     *
     * @param nombre nombre que se quiere comprobar
     * @param id     id de la categoría que se está editando
     * @return true si otra categoría ya usa ese nombre
     */
    @Override
    public boolean existeCategoriaPorNombreYDistintoId(String nombre, Long id) {
        logger.info("Comprobando si existe otra categoría con nombre {} sin contar la de id {}", nombre, id);
        String jpql = "SELECT COUNT(c) FROM Categoria c WHERE UPPER(c.nombre) = :nombre AND c.id <> :id";
        Long total = entityManager.createQuery(jpql, Long.class)
                .setParameter("nombre", nombre.toUpperCase())
                .setParameter("id", id)
                .getSingleResult();
        boolean existe = total != null && total > 0;
        logger.info("¿Existe otra categoría con nombre {} (sin contar el id {})? {}", nombre, id, existe);
        return existe;
    }

    /**
     * Devuelve una página de categorías ordenada por el campo indicado.
     * Solo se permite ordenar por los campos de la lista blanca (id y nombre).
     *
     * @param pagina         número de página (empieza en 0)
     * @param tamanio        número de categorías por página
     * @param campoOrden     campo por el que se ordena
     * @param direccionOrden asc o desc
     * @return lista con las categorías de esa página
     */
    @Override
    public List<Categoria> listarCategoriasPaginadas(int pagina, int tamanio, String campoOrden, String direccionOrden) {
        logger.info("Listando categorías: página={}, tamaño={}, campoOrden={}, dirección={}",
                pagina, tamanio, campoOrden, direccionOrden);
        int offset = pagina * tamanio;

        // 1. Construcción de Criteria
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Categoria> cq = cb.createQuery(Categoria.class);
        Root<Categoria> root = cq.from(Categoria.class);

        // 2. Campos por los que se permite ordenar (lista blanca)
        Path<?> rutaOrden;
        switch (campoOrden) {
            case "id" -> rutaOrden = root.get("id");
            case "nombre" -> rutaOrden = root.get("nombre");
            default -> {
                logger.warn("Campo de ordenación '{}' no válido, se ordena por 'nombre'.", campoOrden);
                rutaOrden = root.get("nombre");
            }
        }

        // 3. Dirección de ordenación
        boolean descendente = "desc".equalsIgnoreCase(direccionOrden);
        Order orden = descendente ? cb.desc(rutaOrden) : cb.asc(rutaOrden);

        // 4. Aplicar la ordenación a la consulta
        cq.select(root).orderBy(orden);

        // 5. Crear la consulta, aplicar la paginación y ejecutarla
        List<Categoria> categorias = entityManager.createQuery(cq)
                .setFirstResult(offset)
                .setMaxResults(tamanio)
                .getResultList();
        logger.info("Se han obtenido {} categorías en la página {}", categorias.size(), pagina);
        return categorias;
    }

    /**
     * Cuenta cuántas categorías hay en total.
     *
     * @return número total de categorías
     */
    @Override
    public long contarCategorias() {
        logger.info("Contando el total de categorías de la base de datos.");
        String jpql = "SELECT COUNT(c) FROM Categoria c";
        Long total = entityManager.createQuery(jpql, Long.class).getSingleResult();
        logger.info("Total de categorías: {}", total);
        return (total != null) ? total : 0L;
    }
}

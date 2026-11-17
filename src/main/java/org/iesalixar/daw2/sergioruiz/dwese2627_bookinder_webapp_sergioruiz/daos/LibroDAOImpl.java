package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.spi.LoggerRegistry;
import org.hibernate.annotations.Audited;
import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities.Libro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class LibroDAOImpl implements LibroDAO {

    private static final Logger logger = LoggerFactory.getLogger(LibroDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Libro> listarLibros() {
        logger.info("Listing all regions from the database.");
        String sql = "SELECT l FROM Libro l ORDER BY r.name";
        List<Libro> libros = entityManager.createQuery(sql, Libro.class).getResultList();
        logger.info("Retrieved {} regions from the database.", libros.size());
        return libros;
    }

    @Override
    public void insertarLibros(Libro libro) {
        logger.info("Insertando libro con id {}",libro.getId());
        entityManager.persist(libro);
        logger.info("Libro insertada correctamente");
    }

    @Override
    public void actualizarLibro(Libro libro) {
        logger.info("Actualizar libro con id {}",libro.getId());
        entityManager.merge(libro);
        logger.info("Libro actualizado correctamente");
    }

    @Override
    public void borrarLibro(Long id) {
        logger.info("Entrando al método para borrar el usuario con id{}",id);
        Libro libro = entityManager.find(Libro.class,id);
        if(libro!=null){
            entityManager.remove(libro);
            logger.info("El libro con id {} ha sido borrado correctamente");
        }else{
            logger.warn("No existe ningún libro con el id {}",id);
        }
    }

    @Override
    public Libro obtenerLibroPorId(Long id) {
        logger.info("Buscando libro con id {}",id);
        Libro libro = entityManager.find(Libro.class,id);
        if(libro==null){
            logger.info("Libro encontrado con id {}",id);
        } else {
            logger.warn("No existe el libro con id {}",id);
        }
        return libro;
    }
}
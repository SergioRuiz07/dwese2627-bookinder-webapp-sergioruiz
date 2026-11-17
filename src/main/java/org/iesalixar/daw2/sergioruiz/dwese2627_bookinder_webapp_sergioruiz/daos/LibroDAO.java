package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.daos;

import org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities.Libro;

import java.util.List;

public interface LibroDAO {
    List<Libro> listarLibros();
    void insertarLibros(Libro libro);
    void actualizarLibro(Libro libro);
    void borrarLibro(Long id);
    Libro obtenerLibroPorId(Long id);

//    Region getRegionByIdWithProvinces(Long id);
//    boolean existsRegionByCode(String code);
//    boolean existsRegionByCodeAndNotId(String code, Long id);
//    List<Region> listRegionPage(int page, int size, String sortfield, String sortdir);
//    long countRegions();
}

package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una categoría de libros (por ejemplo "Novela" o "Fantasía").
 * Es el lado 1 de la relación 1:N: una categoría puede tener muchos libros.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categoria")
public class Categoria {

    /**
     * Identificador de la categoría. Lo genera la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Nombre de la categoría. Es obligatorio y no se puede repetir.
     */
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    /**
     * Libros que pertenecen a esta categoría.
     * No lleva cascade para que al borrar una categoría no se borren sus libros.
     * Se excluye de toString, equals y hashCode para evitar bucles infinitos con Libro.
     */
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Libro> libros = new ArrayList<>();
}

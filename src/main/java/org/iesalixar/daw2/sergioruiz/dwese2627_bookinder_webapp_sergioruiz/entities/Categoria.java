package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;

    @Column(name = "nombre",nullable = false,unique = true,length = 100)
    private String nombre;

    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Libro> libros = new ArrayList<>();
}
package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.entities;

import jakarta.persistence.*;
import lombok.*;

import javax.swing.plaf.synth.Region;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "libro")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "isbn", nullable = false, unique = true, length = 17)
    private String isbn;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "autor", length = 100)
    private String autor;

    @Column(name = "editorial", length = 100)
    private String editorial;

    @Column(name = "anioPublicacion")
    private Integer anioPublicacion;

    @Column(name = "sipnosis", length = 500)
    private String sipnosis;

    @Column(name = "urlPortada")
    private String urlPortada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
}
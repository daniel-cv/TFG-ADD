package com.smartnetwork.backend.domain.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")

public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String ip;

    @ManyToOne
    @JoinColumn(name = "interfaz_id")
    private Interfaz interfaz;

    private String comentario;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;
}

package com.smartnetwork.backend.domain.Entity;

import jakarta.persistence.*;
import jdk.jfr.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "services")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String comentario;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private String tipoProtocolo;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false)
    private String destinationPort;

    @ManyToOne
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;

}

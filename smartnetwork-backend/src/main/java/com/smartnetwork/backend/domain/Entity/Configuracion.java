package com.smartnetwork.backend.domain.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "configuraciones")
public class Configuracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoConfiguracion; // VLAN, ACL, INTERFAZ...

    @Lob
    private String contenido;

    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "dispositivo_id")
    private Dispositivo dispositivo;
}


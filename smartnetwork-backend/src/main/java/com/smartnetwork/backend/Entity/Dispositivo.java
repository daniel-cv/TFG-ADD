package com.smartnetwork.backend.Entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "dispositivos")
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String tipo; // SWITCH | FIREWALL
    private String ip;
    private Integer puerto;
    private String fabricante;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToOne(mappedBy = "dispositivo", cascade = CascadeType.ALL)
    private Credencial credencial;

    @OneToMany(mappedBy = "dispositivo")
    private List<Configuracion> configuraciones;
}


package com.smartnetwork.backend.domain.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "credenciales")
public class Credencial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deviceUsername;

    @Column(nullable = false)
    private String devicePassword;

    @OneToOne
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;
}



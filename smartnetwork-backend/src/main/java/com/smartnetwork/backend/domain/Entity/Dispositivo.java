package com.smartnetwork.backend.domain.Entity;

import com.smartnetwork.backend.domain.Enum.EstadoDispositivo;
import com.smartnetwork.backend.domain.Enum.Fabricante;
import com.smartnetwork.backend.domain.Enum.TipoDispositivo;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "dispositivos")
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDispositivo tipo; // FIREWALL | SWITCH

    @Column(nullable = false, unique = true)
    private String ip;

    @Column(nullable = false)
    private Integer puerto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Fabricante fabricante;

    @Enumerated(EnumType.STRING)
    private EstadoDispositivo estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToOne(mappedBy = "dispositivo", cascade = CascadeType.ALL)
    private Credencial credencial;

    @OneToMany(mappedBy = "dispositivo", cascade = CascadeType.ALL)
    private List<Configuracion> configuraciones;

    private LocalDateTime createdAt;
}


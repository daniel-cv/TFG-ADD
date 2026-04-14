package com.smartnetwork.backend.domain.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "interfaces")
public class Interfaz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tipo;

    private String interfacePadre;

    private Integer vlanid;

    @Column(nullable = false)
    private String vdom = "root";

    private String mode;

    private String ip;

    private String allowaccess;

    private String role;

    private String description;

    @OneToMany(mappedBy = "interfaz", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<DispositivoInterfaz> dispositivoInterfaz = new ArrayList<>();

    @OneToMany(mappedBy = "interfaz", cascade = CascadeType.ALL)
    private List<Address> addresses; // Relación con addresses

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}

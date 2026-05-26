package com.smartnetwork.backend.domain.Entity.firewalls.Address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartnetwork.backend.domain.Entity.Usuario;
import com.smartnetwork.backend.domain.Entity.firewalls.Interfaz.Interfaz;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false)
    private String ipdestino;

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<DispositivoAddress> dispositivoAddresses = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

}

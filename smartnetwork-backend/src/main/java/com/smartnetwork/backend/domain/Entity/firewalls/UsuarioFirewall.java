package com.smartnetwork.backend.domain.Entity.firewalls;

import com.smartnetwork.backend.domain.Entity.Dispositivo;
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
@Table(name = "usuario_firewall")
@ToString(exclude = "dispositivo")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UsuarioFirewall {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = true)
    private String tipo;

    @Column(nullable = false)
    private String factor;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "usuarioFirewall", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<DispositivoUsuarioFirewall> dispositivoUsuarioFirewalls = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}


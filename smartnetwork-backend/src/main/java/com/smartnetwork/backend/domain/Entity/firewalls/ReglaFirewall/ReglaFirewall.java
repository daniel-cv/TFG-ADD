package com.smartnetwork.backend.domain.Entity.firewalls.ReglaFirewall;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartnetwork.backend.domain.Entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reglas_firewall")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ReglaFirewall {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "reglaFirewall", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<DispositivoReglaFirewall> dispositivoReglaFirewalls = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String origen;

    @Column(nullable = false)
    private String destino;

    @Column(nullable = false)
    private String iporigen;

    @Column(nullable = false)
    private String ipdestino;

    @Column(nullable = false)
    private String servicio;

    @Column(nullable = false)
    private String nat;

    @Column(nullable = false)
    private String action;

    private boolean habilitada;
}

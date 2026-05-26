package com.smartnetwork.backend.domain.Entity.firewalls;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reglas_firewall")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "dispositivo")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ReglaFirewall {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;

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

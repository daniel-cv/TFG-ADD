package com.smartnetwork.backend.domain.Entity;
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

    // Asociación al firewall (Dispositivo)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String origen;   // IP / red / objeto

    @Column(nullable = false)
    private String destino;  // IP / red / objeto

    @Column(nullable = false)
    private String iporigen;   // IP / red / objeto

    @Column(nullable = false)
    private String ipdestino;  // IP / red / objeto

    @Column(nullable = false)
    private String servicio; // HTTP, HTTPS, ALL...

    private boolean habilitada;
}

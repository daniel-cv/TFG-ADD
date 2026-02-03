package com.smartnetwork.backend.domain.Entity;
import com.smartnetwork.backend.domain.Enum.AccionFirewall;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "reglas_firewall")
public class ReglaFirewall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Asociación al firewall (Dispositivo)
    @ManyToOne
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

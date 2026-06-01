package com.smartnetwork.backend.domain.Entity.switches;

import com.smartnetwork.backend.domain.Entity.Dispositivo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "IpRoute",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "dispositivo_id"})
)
@Getter
@Setter
public class IpRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ipDestino;
    @Column(nullable = false)
    private String mascara;
    @Column(nullable = false)
    private String gateway;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;
}

package com.smartnetwork.backend.domain.Entity.switches;

import com.smartnetwork.backend.domain.Entity.Dispositivo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "vlan",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "dispositivo_id"})
)
@Getter
@Setter
public class Vlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Integer vlanId;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;
}

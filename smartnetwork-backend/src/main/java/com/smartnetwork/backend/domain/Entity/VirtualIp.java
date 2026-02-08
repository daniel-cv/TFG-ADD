package com.smartnetwork.backend.domain.Entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "VirtualIp")
@ToString(exclude = {"dispositivo", "interfaz"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VirtualIp {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interfaz_id", nullable = false)
    private Interfaz interfaz;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String External_ip;

    @Column(nullable = false)
    private String Internal_ip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;
}

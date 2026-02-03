package com.smartnetwork.backend.domain.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "VirtualIp")
public class VirtualIp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String comments;

    @ManyToOne
    @JoinColumn(name = "interfaz_id", nullable = false)
    private Interfaz interfaz;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String External_ip;

    @Column(nullable = false)
    private String Internal_ip;

    @ManyToOne
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;

}

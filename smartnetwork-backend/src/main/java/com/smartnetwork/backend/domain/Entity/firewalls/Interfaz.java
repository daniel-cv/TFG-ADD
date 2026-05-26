package com.smartnetwork.backend.domain.Entity.firewalls;

import com.smartnetwork.backend.domain.Entity.Dispositivo;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Interfaz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tipo;

    private String interfacePadre;

    private Integer vlanid;

    @Column(nullable = false)
    private String vdom = "root";

    private String mode;

    private String ip;

    private String allowaccess;

    private String role;

    private String description;

    @ManyToOne
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;

    @OneToMany(mappedBy = "interfaz", cascade = CascadeType.ALL)
    private List<Address> addresses; // Relación con addresses
}

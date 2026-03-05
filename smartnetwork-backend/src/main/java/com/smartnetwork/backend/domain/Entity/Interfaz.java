package com.smartnetwork.backend.domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "interfaces")
public class Interfaz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tipo; // fisica | vlan

    private String interfacePadre; // solo si VLAN

    private Integer vlanid; // solo si VLAN

    @Column(nullable = false)
    private String vdom = "root"; // VDOM, por defecto root

    private String mode; // static | dhcp

    private String ip; // solo si mode=static

    private String allowaccess; // ejemplo: "ping https ssh"

    private String role; // lan | wan | dmz

    private String description;

    @ManyToOne
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;

    @OneToMany(mappedBy = "interfaz", cascade = CascadeType.ALL)
    private List<Address> addresses; // Relación con addresses
}

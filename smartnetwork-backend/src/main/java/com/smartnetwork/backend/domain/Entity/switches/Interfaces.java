package com.smartnetwork.backend.domain.Entity.switches;

import com.smartnetwork.backend.domain.Entity.Dispositivo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(
        name = "interfaces",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "dispositivo_id"})
)
@Getter
@Setter
public class Interfaces {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String mode;

    private Integer nativeVlan;

    private String ip;

    private String estado;

    private Boolean enabled = true;

    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vlan_access_id")
    private Vlan vlanAccess;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "interface_vlans_trunk",
            joinColumns = @JoinColumn(name = "interface_id"),
            inverseJoinColumns = @JoinColumn(name = "vlan_id")
    )
    private List<Vlan> vlansTrunk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acl_in_id")
    private Acl aclIn;
    private String aclDirection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;
}
package com.smartnetwork.backend.domain.Entity.switches;

import com.smartnetwork.backend.domain.Entity.Dispositivo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "acls")
@Getter
@Setter
public class Acl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;

    @OneToMany(mappedBy = "acl", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AclRegla> reglas;
}
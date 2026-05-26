package com.smartnetwork.backend.domain.Entity.switches;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class AclRegla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accion; // permit / deny
    private String origen; // 192.168.1.0/24 o any
    private String destino; // any o red

    private Integer orden;

    @ManyToOne
    private Acl acl;
}

package com.smartnetwork.backend.domain.dtos.switches.acls;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AclReglaDTO {

    private Long id;
    private String accion;
    private String origen;
    private String destino;
    private Integer orden;
}
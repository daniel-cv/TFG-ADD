package com.smartnetwork.backend.domain.dtos.switches.acls;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearReglaDTO {
    private String accion;
    private String origen;
    private String destino;
    private Integer orden;
}
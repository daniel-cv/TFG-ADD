package com.smartnetwork.backend.domain.dtos.switches.acls;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearAclDTO {
    private String nombre;
    private Long dispositivoId;
}
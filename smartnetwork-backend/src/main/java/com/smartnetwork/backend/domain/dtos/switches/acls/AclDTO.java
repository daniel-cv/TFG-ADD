package com.smartnetwork.backend.domain.dtos.switches.acls;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AclDTO {

    private Long id;
    private String nombre;
    private List<AclReglaDTO> reglas;
}

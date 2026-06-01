package com.smartnetwork.backend.domain.dtos.switches.vlan;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearVlanDTO {

    private Integer vlanId;
    private String nombre;
    private Long dispositivoId;
}
package com.smartnetwork.backend.domain.dtos.switches.vlan;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VlanDTO {

    private Long id;
    private Integer vlanId;
    private String nombre;
    private Long dispositivoId;
}

package com.smartnetwork.backend.domain.dtos.switches.interfaz;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CrearInterfacesDTO {

    private String name;

    private String mode;

    private Integer vlanAccess;

    private List<Integer> vlansTrunk;

    private Integer nativeVlan;

    private String ip;

    private String descripcion;

    private Long switchId;

    // 🔥 NUEVO
    private Boolean enabled;
    private String aclIn;
    private String aclDirection;
    private Boolean eliminarAcl;
}
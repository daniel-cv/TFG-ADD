package com.smartnetwork.backend.domain.dtos.switches.interfaz;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InterfacesDTO {

    private Long id;

    private String name;

    private String mode;

    private Integer vlanAccess;

    private List<Integer> vlansTrunk;

    private Integer nativeVlan;

    private String ip;

    private String estado;

    private Boolean enabled;

    private String descripcion;

    private Long switchId;

    private String aclIn;
    private String aclDirection;
}
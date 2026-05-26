package com.smartnetwork.backend.domain.dtos.firewalls.interfaz;

import com.smartnetwork.backend.domain.Entity.Dispositivo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CrearInterfazDTO {

    private String name;

    private String tipo;

    private String interfacePadre;

    private Integer vlanid;

    private String vdom = "root";

    private String mode;

    private String ip;

    private String allowaccess;

    private String role;

    private String description;

    private List<Long> dispositivosId;
}

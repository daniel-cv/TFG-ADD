package com.smartnetwork.backend.domain.dtos.firewalls.interfaz;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearInterfazDTO {

    private String name;

    private String tipo; // fisica | vlan

    private String interfacePadre; // solo si VLAN

    private Integer vlanid; // solo si VLAN

    private String vdom = "root"; // default

    private String mode; // static | dhcp

    private String ip; // solo si mode=static

    private String allowaccess; // "ping https ssh"

    private String role; // lan | wan | dmz

    private String description;

    private Long dispositivoId; // Relación con dispositivo
}

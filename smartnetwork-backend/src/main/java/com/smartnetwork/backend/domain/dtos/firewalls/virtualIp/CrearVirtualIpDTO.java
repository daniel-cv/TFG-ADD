package com.smartnetwork.backend.domain.dtos.firewalls.virtualIp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearVirtualIpDTO {
    private String name;
    private String comments;
    private Long interfazId;
    private String type;        // NAT | VIP | etc.
    private String externalIp;
    private String internalIp;
    private Long dispositivoId;
}

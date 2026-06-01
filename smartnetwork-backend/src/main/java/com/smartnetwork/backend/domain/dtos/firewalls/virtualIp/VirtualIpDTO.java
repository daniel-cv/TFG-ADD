package com.smartnetwork.backend.domain.dtos.firewalls.virtualIp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VirtualIpDTO {
    private Long id;
    private String name;
    private String comments;
    private Long interfazId;
    private String type;
    private String externalIp;
    private String internalIp;
    private Long dispositivoId;
}

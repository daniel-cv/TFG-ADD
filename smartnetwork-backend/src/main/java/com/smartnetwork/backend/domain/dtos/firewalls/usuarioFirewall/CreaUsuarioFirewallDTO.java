package com.smartnetwork.backend.domain.dtos.firewalls.usuarioFirewall;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreaUsuarioFirewallDTO {

    private String name;
    private String password;
    private String email;
    private String type;
    private String twoFactor;
    private Long dispositivoId;
}

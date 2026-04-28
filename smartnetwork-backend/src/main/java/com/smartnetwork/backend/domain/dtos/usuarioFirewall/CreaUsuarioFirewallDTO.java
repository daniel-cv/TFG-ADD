package com.smartnetwork.backend.domain.dtos.usuarioFirewall;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreaUsuarioFirewallDTO {

    private String name;
    private String password;
    private String email;
    private String type;
    private String twoFactor;
    private List<Long> dispositivosId;
}

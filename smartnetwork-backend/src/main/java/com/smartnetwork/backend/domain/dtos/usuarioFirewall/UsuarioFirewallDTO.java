package com.smartnetwork.backend.domain.dtos.usuarioFirewall;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioFirewallDTO {

    private String nombre;
    private String tipo;
    private String password;
    private String email;
    private String factor;
    private Long dispositivoId;

}

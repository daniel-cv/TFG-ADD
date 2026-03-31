package com.smartnetwork.backend.domain.dtos.Policys;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearReglaFirewallDTO {


    private String nombre;
    private String origen;
    private String destino;
    private String ipOrigen;
    private String ipDestino;
    private String servicio;
    private String nat;
    private String action;
    private Long dispositivoId;
}

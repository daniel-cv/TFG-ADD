package com.smartnetwork.backend.domain.dtos.firewalls.Policys;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    private List<Long> dispositivosId;
}

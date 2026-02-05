package com.smartnetwork.backend.domain.dtos;

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
    private Long dispositivoId;
}

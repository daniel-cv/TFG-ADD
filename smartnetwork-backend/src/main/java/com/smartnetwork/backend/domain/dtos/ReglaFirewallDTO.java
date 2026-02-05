package com.smartnetwork.backend.domain.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReglaFirewallDTO {

    private Long id;
    private String nombre;
    private String origen;
    private String destino;
    private String ipOrigen;
    private String ipDestino;
    private String servicio;
    private boolean habilitada;
    private Long dispositivoId;
}

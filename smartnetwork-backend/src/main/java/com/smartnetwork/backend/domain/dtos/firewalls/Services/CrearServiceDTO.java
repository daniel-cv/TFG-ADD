package com.smartnetwork.backend.domain.dtos.firewalls.Services;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearServiceDTO {

    private String nombre;
    private String tipoProtocolo;
    private String ip;
    private String destinationPort;
    private String comentario;
    private Long dispositivoId;
}

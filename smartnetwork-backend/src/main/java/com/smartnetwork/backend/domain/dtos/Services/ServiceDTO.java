package com.smartnetwork.backend.domain.dtos.Services;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceDTO {

    private Long id;
    private String nombre;
    private String comentario;
    private String categoria;
    private String tipoProtocolo;
    private String ip;
    private String destinationPort;
    private Long dispositivoId;
}

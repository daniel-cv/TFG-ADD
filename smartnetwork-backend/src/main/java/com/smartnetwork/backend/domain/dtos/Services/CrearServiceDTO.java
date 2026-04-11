package com.smartnetwork.backend.domain.dtos.Services;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CrearServiceDTO {

    private String nombre;
    private String tipoProtocolo;
    private String ip;
    private String destinationPort;
    private String comentario;
    private List<Long> dispositivosId;
}

package com.smartnetwork.backend.domain.dtos.Services;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearServiceDTO {

    private String name;
    private String protocol;
    private String address;
    private String portRange;
    private String comentario;
    private Long dispositivoId;
}

package com.smartnetwork.backend.domain.dtos.Services;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearServiceDTO {

    private String nombre;        // ← antes era 'name'
    private String tipoProtocolo; // ← antes 'protocol'
    private String ip;            // ← antes 'address'
    private String destinationPort; // ← antes 'portRange'
    private String comentario;
    private Long dispositivoId;
}

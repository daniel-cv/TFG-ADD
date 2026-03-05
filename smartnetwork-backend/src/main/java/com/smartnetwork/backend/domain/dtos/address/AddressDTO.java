package com.smartnetwork.backend.domain.dtos.address;

import com.smartnetwork.backend.domain.Entity.Interfaz;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {

    private Long id;
    private String name;
    private String ip;
    private String ipdestino;
    private String type;
    private Long interfazId;
    private String comentario;
    private boolean habilitada;
    private Long dispositivoId;
}

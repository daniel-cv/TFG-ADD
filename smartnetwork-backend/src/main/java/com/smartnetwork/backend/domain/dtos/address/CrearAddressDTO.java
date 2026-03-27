package com.smartnetwork.backend.domain.dtos.address;


import com.smartnetwork.backend.domain.Entity.Interfaz;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearAddressDTO {

    private String name;
    private String ip;
    private String type;
    private String ipdestino;
    private Long interfazId;
    private String comentario;
    private Long dispositivoId;
}

package com.smartnetwork.backend.domain.dtos.firewalls.address;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CrearAddressDTO {

    private String name;
    private String ip;
    private String type;
    private String ipdestino;
    private Long interfazId;
    private String comentario;
    private List<Long> dispositivosIds;

}

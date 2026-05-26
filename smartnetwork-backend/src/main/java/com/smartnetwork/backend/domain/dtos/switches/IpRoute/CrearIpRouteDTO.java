package com.smartnetwork.backend.domain.dtos.switches.IpRoute;

import com.smartnetwork.backend.domain.Entity.Dispositivo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearIpRouteDTO {

    private String ipDestino;

    private String mascara;

    private String gateway;

    private Long dispositivoId;
}

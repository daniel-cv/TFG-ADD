package com.smartnetwork.backend.domain.dtos.switches.IpRoute;

import com.smartnetwork.backend.domain.Entity.Dispositivo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IpRouteDTO {
    private Long id;
    private String ipDestino;
    private String mascara;
    private String gateway;
    private Long dispositivo;
}

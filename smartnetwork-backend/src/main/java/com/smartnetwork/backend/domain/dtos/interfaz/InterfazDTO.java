package com.smartnetwork.backend.domain.dtos.interfaz;

import com.smartnetwork.backend.domain.dtos.address.AddressDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InterfazDTO {
    private Long id;

    private String name;

    private String tipo;

    private String interfacePadre;

    private Integer vlanid;

    private String vdom;

    private String mode;

    private String ip;

    private String allowaccess;

    private String role;

    private String description;

    private Long dispositivoId;

    private List<AddressDTO> addresses;
}

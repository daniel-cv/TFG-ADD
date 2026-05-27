package com.smartnetwork.backend.Controller.switches;

import com.smartnetwork.backend.Service.switches.InterfacesService;
import com.smartnetwork.backend.domain.dtos.switches.interfaz.CrearInterfacesDTO;
import com.smartnetwork.backend.domain.dtos.switches.interfaz.InterfacesDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/switch/interfaces")
public class InterfacesController {

    private final InterfacesService interfacesService;

    public InterfacesController(InterfacesService interfacesService) {
        this.interfacesService = interfacesService;
    }

    @GetMapping("/dispositivo/{id}")
    public List<InterfacesDTO> listar(
            @PathVariable Long id,
            Authentication auth
    ) {
        return interfacesService.listarPorDispositivo(id, auth.getName());
    }

    @PostMapping("/edit/{interfazId}")
    public InterfacesDTO actualizar(
            @PathVariable Long interfazId,
            @RequestBody CrearInterfacesDTO dto,
            Authentication auth
    ) {
        return interfacesService.actualizarInterfaz(interfazId, dto, auth.getName());
    }
}
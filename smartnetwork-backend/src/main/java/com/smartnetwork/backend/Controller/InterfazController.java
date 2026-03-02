package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Service.InterfazService;
import com.smartnetwork.backend.domain.dtos.firewalls.interfaz.InterfazDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.interfaz.CrearInterfazDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/firewall/interfaz")
public class InterfazController {

    private final InterfazService interfazService;

    public InterfazController(InterfazService interfazService) {
        this.interfazService = interfazService;
    }

    @PostMapping("/create")
    public InterfazDTO crear(
            @RequestBody CrearInterfazDTO dto,
            Authentication auth
    ) {
        return interfazService.crear(dto, auth.getName());
    }

    @GetMapping("/dispositivo/{id}")
    public List<InterfazDTO> listar(
            @PathVariable Long id,
            Authentication auth
    ) {
        return interfazService.listarPorDispositivo(id, auth.getName());
    }
}

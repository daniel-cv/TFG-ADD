package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Service.VirtualIpService;
import com.smartnetwork.backend.domain.dtos.firewalls.virtualIp.CrearVirtualIpDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.virtualIp.VirtualIpDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/firewalls/virtualips")
public class VirtualIpController {

    private final VirtualIpService virtualIpService;

    public VirtualIpController(VirtualIpService virtualIpService) {
        this.virtualIpService = virtualIpService;
    }

    // Crear VirtualIP
    @PostMapping("/create")
    public VirtualIpDTO crear(@RequestBody CrearVirtualIpDTO dto, Authentication auth) {
        return virtualIpService.crear(dto, auth.getName());
    }

    // Listar VirtualIPs por dispositivo
    @GetMapping("/dispositivo/{id}")
    public List<VirtualIpDTO> listar(@PathVariable Long id, Authentication auth) {
        return virtualIpService.listarPorDispositivo(id, auth.getName());
    }
}

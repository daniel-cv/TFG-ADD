package com.smartnetwork.backend.Controller.firewalls;

import com.smartnetwork.backend.Service.firewalls.VirtualIpService;
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

    @PostMapping("/create")
    public VirtualIpDTO crear(@RequestBody CrearVirtualIpDTO dto, Authentication auth) {
        return virtualIpService.crear(dto, auth.getName());
    }

    @GetMapping("/dispositivo/{id}")
    public List<VirtualIpDTO> listar(@PathVariable Long id, Authentication auth) {
        return virtualIpService.listarPorDispositivo(id, auth.getName());
    }

    @DeleteMapping("/delete/{id}")
    public void eliminar(
            @PathVariable Long id,
            Authentication auth
    ) {
        virtualIpService.eliminarVirtualIp(id, auth.getName());
    }

    @PutMapping("/edit/{id}")
    public VirtualIpDTO actualizar(
            @PathVariable Long virtualIpId,
            @RequestBody CrearVirtualIpDTO dto,
            Authentication auth
    ) {
        return virtualIpService.actualizar(virtualIpId, dto, auth.getName());
    }


}



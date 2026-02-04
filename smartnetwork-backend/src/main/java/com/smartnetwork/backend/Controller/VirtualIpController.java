package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Service.VirtualIpService;
import com.smartnetwork.backend.domain.Entity.VirtualIp;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dispositivos/{dispositivoId}/virtualips")
public class VirtualIpController {

    private final VirtualIpService virtualIpService;

    public VirtualIpController(VirtualIpService virtualIpService) {
        this.virtualIpService = virtualIpService;
    }

    // 🔹 CREAR Virtual IP
    @PostMapping
    public VirtualIp crear(
            @PathVariable Long dispositivoId,
            @RequestBody VirtualIp virtualIp,
            Authentication authentication
    ) {
        String username = authentication.getName();

        // asegurar asociación correcta
        virtualIp.getDispositivo().setId(dispositivoId);

        return virtualIpService.create(virtualIp, username);
    }

    // 🔹 LISTAR Virtual IPs del dispositivo
    @GetMapping ("/findAll")
    public List<VirtualIp> listar(
            @PathVariable Long dispositivoId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return virtualIpService.findAllByDispositivo(dispositivoId, username);
    }

    // 🔹 OBTENER una Virtual IP
    @GetMapping("/get/{virtualIpId}")
    public VirtualIp obtener(
            @PathVariable Long dispositivoId,
            @PathVariable Long virtualIpId,
            Authentication authentication
    ) {
        String username = authentication.getName();

        return virtualIpService
                .findById(virtualIpId, dispositivoId, username)
                .orElseThrow(() -> new RuntimeException("VirtualIp no encontrada"));
    }

    // 🔹 ACTUALIZAR Virtual IP
    @PutMapping("/update/{virtualIpId}")
    public VirtualIp actualizar(
            @PathVariable Long dispositivoId,
            @PathVariable Long virtualIpId,
            @RequestBody VirtualIp virtualIp,
            Authentication authentication
    ) {
        String username = authentication.getName();

        virtualIp.setId(virtualIpId);
        virtualIp.getDispositivo().setId(dispositivoId);

        return virtualIpService.update(virtualIp, username, dispositivoId);
    }

    // 🔹 ELIMINAR Virtual IP
    @DeleteMapping("/delete/{virtualIpId}")
    public void eliminar(
            @PathVariable Long dispositivoId,
            @PathVariable Long virtualIpId,
            Authentication authentication
    ) {
        String username = authentication.getName();

        virtualIpService.delete(virtualIpId, dispositivoId, username);
    }
}

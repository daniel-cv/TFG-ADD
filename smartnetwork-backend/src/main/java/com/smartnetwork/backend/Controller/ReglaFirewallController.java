package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Service.ReglaFirewallService;
import com.smartnetwork.backend.domain.Entity.ReglaFirewall;
import com.smartnetwork.backend.domain.dtos.CrearReglaFirewallDTO;
import com.smartnetwork.backend.domain.dtos.ReglaFirewallDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/firewalls/reglas")
public class ReglaFirewallController {

    private final ReglaFirewallService reglaFirewallService;

    public ReglaFirewallController(ReglaFirewallService reglaFirewallService) {
        this.reglaFirewallService = reglaFirewallService;
    }


    @PostMapping
    public ReglaFirewallDTO crear(@RequestBody CrearReglaFirewallDTO dto, Authentication auth) {

        return reglaFirewallService.crearRegla(dto, auth.getName());
    }

    @GetMapping("/dispositivo/{id}")
    public List<ReglaFirewall> listar(
            @PathVariable Long id,
            Authentication authentication) {

        return reglaFirewallService.obtenerPorDispositivo(id, authentication.getName());
    }
    private ReglaFirewallDTO toDTO(ReglaFirewall regla) {
        ReglaFirewallDTO dto = new ReglaFirewallDTO();
        dto.setId(regla.getId());
        dto.setNombre(regla.getNombre());
        dto.setOrigen(regla.getOrigen());
        dto.setDestino(regla.getDestino());
        dto.setServicio(regla.getServicio());
        dto.setHabilitada(regla.isHabilitada());
        return dto;
    }
}
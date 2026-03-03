package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Service.ReglaFirewallService;
import com.smartnetwork.backend.domain.Entity.ReglaFirewall;
import com.smartnetwork.backend.domain.dtos.Policys.CrearReglaFirewallDTO;
import com.smartnetwork.backend.domain.dtos.Policys.ReglaFirewallDTO;
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

    // 🔥 NUEVO: eliminar
    @DeleteMapping("/{reglaId}")
    public void eliminar(@PathVariable Long reglaId, Authentication auth) {
        reglaFirewallService.eliminarRegla(reglaId, auth.getName());
    }
}
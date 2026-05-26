package com.smartnetwork.backend.Controller.firewalls;

import com.smartnetwork.backend.Service.firewalls.ReglaFirewallService;
import com.smartnetwork.backend.domain.Entity.firewalls.ReglaFirewall;
import com.smartnetwork.backend.domain.dtos.firewalls.Policys.CrearReglaFirewallDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.Policys.ReglaFirewallDTO;
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

    @DeleteMapping("/delete/{id}")
    public void eliminar(@PathVariable Long id, Authentication auth) {
        reglaFirewallService.eliminarRegla(id, auth.getName());
    }

    @PutMapping("/edit/{id}")
    public ReglaFirewallDTO editar(
            @PathVariable Long id,
            @RequestBody CrearReglaFirewallDTO dto,
            Authentication auth
    ) {
        return reglaFirewallService.editarReglaFirewall(id, dto, auth.getName());
    }
}
package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Repository.ReglaFirewallRepository;
import com.smartnetwork.backend.Service.ReglaFirewallService;
import com.smartnetwork.backend.domain.Entity.ReglaFirewall;
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
    public ReglaFirewall crear(@RequestBody ReglaFirewall regla, Authentication authentication) {
        String name = authentication.getName();
        return reglaFirewallService.crearRegla(regla, name);
    }

    @GetMapping("/dispositivo/{id}")
    public List<ReglaFirewall> listar(@PathVariable Long id,
                                      Authentication authentication) {
        String name = authentication.getName();
        return reglaFirewallService.obtenerPorDispositivo(id, name);
    }
}
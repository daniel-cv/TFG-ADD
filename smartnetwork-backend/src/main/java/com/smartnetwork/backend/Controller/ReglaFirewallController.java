package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Repository.ReglaFirewallRepository;
import com.smartnetwork.backend.Service.ReglaFirewallService;
import com.smartnetwork.backend.domain.Entity.ReglaFirewall;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/firewalls/reglas")
public class ReglaFirewallController {

    private final ReglaFirewallService service;

    public ReglaFirewallController(ReglaFirewallService service) {
        this.service = service;
    }

    @PostMapping
    public ReglaFirewall crear(@RequestBody ReglaFirewall regla) {
        String name = "admin";
        return service.crearRegla(regla, name);
    }

    @GetMapping("/dispositivo/{id}")
    public List<ReglaFirewall> listar(@PathVariable Long id,
                                      String name) {
        return service.obtenerPorDispositivo(id, name);
    }
}
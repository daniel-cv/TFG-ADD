package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Service.ReglaFirewallService;
import com.smartnetwork.backend.domain.Entity.ReglaFirewall;
import com.smartnetwork.backend.domain.dtos.Policys.CrearReglaFirewallDTO;
import com.smartnetwork.backend.domain.dtos.Policys.ReglaFirewallDTO;
import com.smartnetwork.backend.domain.dtos.Services.CrearServiceDTO;
import com.smartnetwork.backend.domain.dtos.Services.ServiceDTO;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/create")
    public ReglaFirewallDTO crear(@RequestBody CrearReglaFirewallDTO dto, Authentication auth) {
        return reglaFirewallService.crearReglaFirewall(dto, auth.getName());
    }

    @GetMapping("/dispositivo/{id}")
    public List<ReglaFirewallDTO> listar(
            @PathVariable Long id,
            Authentication authentication) {

        return reglaFirewallService.listarPorDispositivo(id, authentication.getName());
    }

    @GetMapping("/usuario")
    public List<ReglaFirewallDTO> listarAll(
            Authentication authentication) {

        return reglaFirewallService.listarPorUsario(authentication.getName());
    }

    @PostMapping("/{reglaFirewall}/dispositivos")
    public ResponseEntity<Void> asignarReglaFirewall(
            @PathVariable Long reglaFirewall,
            @RequestBody List<Long> reglaFirewallId,
            Authentication auth){
        reglaFirewallService.asignarReglaFirewallADispositivos(reglaFirewall, reglaFirewallId, auth.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/full")
    public ReglaFirewallDTO crearCompleto(
            @RequestBody CrearReglaFirewallDTO dto,
            Authentication auth
    ){
        return reglaFirewallService.crear(dto, auth.getName());
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
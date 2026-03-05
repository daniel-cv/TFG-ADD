package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Service.UsuarioFirewallService;
import com.smartnetwork.backend.domain.Entity.UsuarioFirewall;
import com.smartnetwork.backend.domain.dtos.usuarioFirewall.CreaUsuarioFirewallDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/firewalls/usuarioFirewall")
public class UsuarioFirewallController {

    private final UsuarioFirewallService usuarioFirewallService;

    public UsuarioFirewallController(UsuarioFirewallService usuarioFirewallService) {
        this.usuarioFirewallService = usuarioFirewallService;
    }

    /**
     * Crear un UsuarioFirewall
     */
    @PostMapping("/create")
    public ResponseEntity<UsuarioFirewall> create(@RequestBody CreaUsuarioFirewallDTO dto, Authentication auth) {
        UsuarioFirewall created = usuarioFirewallService.create(dto, auth.getName());
        return ResponseEntity.ok(created);
    }

    /**
     * Listar todos los UsuarioFirewall de un dispositivo
     */
    @GetMapping("/dispositivo/{id}")
    public ResponseEntity<List<UsuarioFirewall>> getAllByDispositivo(@PathVariable Long id, Authentication auth) {
        List<UsuarioFirewall> usuarios = usuarioFirewallService.findAllByDispositivo(id, auth.getName());
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Obtener un UsuarioFirewall por ID
     */
    @GetMapping("/{usuarioFirewallId}/dispositivo/{dispositivoId}")
    public ResponseEntity<UsuarioFirewall> getById(
            @PathVariable Long usuarioFirewallId,
            @PathVariable Long dispositivoId,
            @RequestParam String username
    ) {
        return usuarioFirewallService.findById(username, dispositivoId, usuarioFirewallId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Actualizar un UsuarioFirewall
     */
    @PutMapping("/{usuarioFirewallId}/dispositivo/{dispositivoId}")
    public ResponseEntity<UsuarioFirewall> update(
            @PathVariable Long usuarioFirewallId,
            @PathVariable Long dispositivoId,
            @RequestParam String username,
            @RequestBody UsuarioFirewall usuarioFirewall
    ) {
        usuarioFirewall.setId(usuarioFirewallId);
        UsuarioFirewall updated = usuarioFirewallService.update(usuarioFirewall, username, dispositivoId);
        return ResponseEntity.ok(updated);
    }


    /**
     * Eliminar un UsuarioFirewall
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication auth
    ) {
        usuarioFirewallService.eliminar(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}

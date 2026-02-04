package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Service.UsuarioFirewallService;
import com.smartnetwork.backend.domain.Entity.UsuarioFirewall;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarioFirewall")
public class UsuarioFirewallController {

    private final UsuarioFirewallService usuarioFirewallService;

    public UsuarioFirewallController(UsuarioFirewallService usuarioFirewallService) {
        this.usuarioFirewallService = usuarioFirewallService;
    }

    /**
     * Crear un UsuarioFirewall
     */
    @PostMapping
    public ResponseEntity<UsuarioFirewall> create(
            @RequestBody UsuarioFirewall usuarioFirewall,
            @RequestParam String username // username del usuario autenticado
    ) {
        UsuarioFirewall created = usuarioFirewallService.create(usuarioFirewall, username);
        return ResponseEntity.ok(created);
    }

    /**
     * Listar todos los UsuarioFirewall de un dispositivo
     */
    @GetMapping("/dispositivo/{dispositivoId}")
    public ResponseEntity<List<UsuarioFirewall>> getAllByDispositivo(
            @PathVariable Long dispositivoId,
            @RequestParam String username
    ) {
        List<UsuarioFirewall> usuarios = usuarioFirewallService.findAllByDispositivo(dispositivoId, username);
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
    @DeleteMapping("/{usuarioFirewallId}/dispositivo/{dispositivoId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long usuarioFirewallId,
            @PathVariable Long dispositivoId,
            @RequestParam String username,
            @RequestBody UsuarioFirewall usuarioFirewall
    ) {
        usuarioFirewallService.delete(usuarioFirewall, username, dispositivoId);
        return ResponseEntity.noContent().build();
    }
}

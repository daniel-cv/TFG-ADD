package com.smartnetwork.backend.Controller.firewalls;

import com.smartnetwork.backend.Service.firewalls.UsuarioFirewallService;
import com.smartnetwork.backend.domain.dtos.firewalls.usuarioFirewall.CreaUsuarioFirewallDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.usuarioFirewall.UsuarioFirewallDTO;
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

    @PostMapping("/create")
    public ResponseEntity<UsuarioFirewallDTO> create(@RequestBody CreaUsuarioFirewallDTO dto, Authentication auth) {
        UsuarioFirewallDTO created = usuarioFirewallService.crear(dto, auth.getName());
        return ResponseEntity.ok(created);
    }

    @GetMapping("/dispositivo/{id}")
    public ResponseEntity<List<UsuarioFirewallDTO>> getAllByDispositivo(@PathVariable Long id, Authentication auth) {
        List<UsuarioFirewallDTO> usuarios = usuarioFirewallService.listarPorDispositivo(id, auth.getName());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/usuario")
    public List<UsuarioFirewallDTO> listarAll(
            Authentication auth
    ){
        return usuarioFirewallService.listarPorUsuario(auth.getName());
    }

    @PostMapping("/{usuarioId}/dispositivos")
    public ResponseEntity<Void> asignarUsuarios(
            @PathVariable Long usuarioId,
            @RequestBody List<Long> dispositivosIds,
            Authentication auth
    ){
        usuarioFirewallService.asignarUsuarioFirewallADispositivo(usuarioId, dispositivosIds, auth.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/full")
    public UsuarioFirewallDTO crearCompleto(
            @RequestBody CreaUsuarioFirewallDTO dto,
            Authentication auth
    ) {
        return usuarioFirewallService.crear(dto, auth.getName());
    }

    @DeleteMapping("/delete/{id}")
    public void eliminar(
            @PathVariable Long id,
            @RequestBody List<Long> dispositivosIds,
            Authentication auth
    ) {
        usuarioFirewallService.eliminarUsuarioFirewall(id, dispositivosIds, auth.getName());
    }
    @PutMapping("/edit/{id}")
    public UsuarioFirewallDTO editar(
            @PathVariable Long id,
            @RequestBody CreaUsuarioFirewallDTO dto,
            Authentication auth
    ) {
        return usuarioFirewallService.actualizar(id, dto, auth.getName());
    }

    @PutMapping("/preedit/{id}")
    public UsuarioFirewallDTO preeditar(
            @PathVariable Long id,
            @RequestBody CreaUsuarioFirewallDTO dto,
            Authentication auth
    ) {
        return usuarioFirewallService.preactualizar(id, dto, auth.getName());
    }

    @DeleteMapping("/delete/{id}")
    public void preeliminar(
            @PathVariable Long id,
            Authentication auth
    ) {
        usuarioFirewallService.preeliminarUsuarioFirewall(id, auth.getName());
    }
}

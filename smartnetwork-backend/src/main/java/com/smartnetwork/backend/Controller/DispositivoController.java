package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Service.DispositivoService;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dispositivos")
public class DispositivoController {

    private final DispositivoService dispositivoService;

    public DispositivoController(DispositivoService dispositivoService) {
        this.dispositivoService = dispositivoService;
    }

    @GetMapping("/mios")
    public List<Dispositivo> misDispositivos(Authentication authentication) {
        String username = authentication.getName();
        return dispositivoService.obtenerDispositivosDelUsuario(username);
    }
    @PostMapping("/crear")
    public Dispositivo crear(@RequestBody Dispositivo dispositivo, Authentication authentication) {
        String username = authentication.getName();
        return dispositivoService.crearDispositivo(dispositivo,username);
    }

    @PostMapping("/crearpolitica")
    public Dispositivo createPolicy(@RequestBody Dispositivo dispositivo, Authentication authentication) {
        String username = authentication.getName();
        return dispositivoService.crearDispositivo(dispositivo,username);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dispositivo> obtenerDispositivo(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();

        return dispositivoService.getDispositivo(id, username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}


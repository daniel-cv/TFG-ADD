package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Service.DispositivoService;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Enum.Fabricante;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @DeleteMapping("/delete/{id}")
    public Dispositivo eliminarDispositivo(
            @PathVariable Long id,
            Authentication auth
    ) {
        return dispositivoService.eliminarDispositivo(id, auth.getName());
    }
    @PutMapping("/{id}")
    public Dispositivo editarDispositivo(
            @PathVariable Long id,
            @RequestBody Dispositivo datos,
            Authentication auth
    ) {
        return dispositivoService.editarDispositivo(id, datos, auth.getName());
    }

    @GetMapping("/find/{fabricante}")
    public List<Dispositivo> dispositivosPorFabricante(@PathVariable Fabricante fabricante, Authentication authentication) {
        String username = authentication.getName();
        return dispositivoService.obtenerDispositivosDelUsuarioPorFabricante(username, fabricante);
    }
}


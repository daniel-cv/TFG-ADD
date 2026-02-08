package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Service.InterfazService;
import com.smartnetwork.backend.domain.Entity.Interfaz;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/firewall/interfaz")
public class InterfazController {

    private final InterfazService interfazService;

    public InterfazController(InterfazService interfazService) {
        this.interfazService = interfazService;
    }

    // 🔹 CREAR interfaz
    @PostMapping("/create")
    public Interfaz crear(
            @PathVariable Long dispositivoId,
            @RequestBody Interfaz interfaz,
            Authentication authentication
    ) {
        String username = authentication.getName();

        // asegurar asociación con el dispositivo
        interfaz.getDispositivo().setId(dispositivoId);

        return interfazService.create(interfaz, username);
    }

    // 🔹 LISTAR interfaces del dispositivo
    @GetMapping("/findById/{dispositivoId}")
    public List<Interfaz> listarPorDispositivo(
            @PathVariable Long dispositivoId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return interfazService.findAllByDispositivo(dispositivoId, username);
    }

    // 🔹 OBTENER una interfaz
    @GetMapping("/get/{interfazId}")
    public Interfaz obtener(
            @PathVariable Long dispositivoId,
            @PathVariable Long interfazId,
            Authentication authentication
    ) {
        String username = authentication.getName();

        return interfazService
                .findById(interfazId, dispositivoId, username)
                .orElseThrow(() -> new RuntimeException("Interfaz no encontrada"));
    }

    // 🔹 ACTUALIZAR interfaz
    @PutMapping("/update/{interfazId}")
    public Interfaz actualizar(
            @PathVariable Long dispositivoId,
            @PathVariable Long interfazId,
            @RequestBody Interfaz interfaz,
            Authentication authentication
    ) {
        String username = authentication.getName();

        interfaz.setId(interfazId);
        interfaz.getDispositivo().setId(dispositivoId);

        return interfazService.update(interfaz, username, dispositivoId);
    }

    // 🔹 ELIMINAR interfaz
    @DeleteMapping("/delete/{interfazId}")
    public void eliminar(
            @PathVariable Long dispositivoId,
            @PathVariable Long interfazId,
            Authentication authentication
    ) {
        String username = authentication.getName();

        interfazService.delete(interfazId, dispositivoId, username);
    }
}


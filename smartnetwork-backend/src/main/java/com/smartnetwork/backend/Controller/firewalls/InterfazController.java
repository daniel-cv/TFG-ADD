package com.smartnetwork.backend.Controller.firewalls;

import com.smartnetwork.backend.Service.firewalls.InterfazService;
import com.smartnetwork.backend.domain.dtos.firewalls.interfaz.InterfazDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.interfaz.CrearInterfazDTO;
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

    @PostMapping("/create")
    public InterfazDTO crear(
            @RequestBody CrearInterfazDTO dto,
            Authentication auth
    ) {
        return interfazService.crear(dto, auth.getName());
    }

    @PostMapping("/create/basic")
    public InterfazDTO crearSoloInterfaz(
            @RequestBody CrearInterfazDTO dto,
            Authentication auth
    ) {
        return interfazService.crearInterfaz(dto, auth.getName());
    }

    @PostMapping("/{interfazId}/asignar")
    public void asignarInterfaz(
            @PathVariable Long interfazId,
            @RequestBody List<Long> dispositivosId,
            Authentication auth
    ) {
        interfazService.asignarInterfazADispositivos(interfazId, dispositivosId, auth.getName());
    }

    @GetMapping("/dispositivo/{id}")
    public List<InterfazDTO> listarPorDispositivo(
            @PathVariable Long id,
            Authentication auth
    ) {
        return interfazService.listarPorDispositivo(id, auth.getName());
    }

    @GetMapping("/usuario")
    public List<InterfazDTO> listarPorUsuario(Authentication auth) {
        return interfazService.listarPorUsuario(auth.getName());
    }

    // 🔥 NUEVO → eliminar MULTI
    @DeleteMapping("/{id}")
    public void eliminar(
            @PathVariable Long id,
            @RequestBody List<Long> dispositivosIds,
            Authentication auth
    ) {
        interfazService.eliminarInterfaz(id, dispositivosIds, auth.getName());
    }

    // 🔥 NUEVO → editar MULTI
    @PutMapping("/edit/{interfazId}")
    public InterfazDTO actualizar(
            @PathVariable Long interfazId,
            @RequestBody CrearInterfazDTO dto,
            Authentication auth
    ) {
        return interfazService.editarInterfaz(interfazId, dto, auth.getName());
    }
}

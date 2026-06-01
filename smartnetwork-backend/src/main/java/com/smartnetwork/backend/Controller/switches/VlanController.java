package com.smartnetwork.backend.Controller.switches;

import com.smartnetwork.backend.Service.switches.VlanService;
import com.smartnetwork.backend.domain.dtos.switches.vlan.CrearVlanDTO;
import com.smartnetwork.backend.domain.dtos.switches.vlan.VlanDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/switch/vlans")
public class VlanController {

    private final VlanService vlanService;

    public VlanController(VlanService vlanService) {
        this.vlanService = vlanService;
    }

    @GetMapping("/dispositivo/{id}")
    public List<VlanDTO> listar(@PathVariable Long id) {
        return vlanService.listarPorDispositivo(id, "test");
    }

    @PostMapping("/create")
    public VlanDTO crear(
            @RequestBody CrearVlanDTO dto,
            Authentication auth
    ) {
        return vlanService.crearVlan(dto, auth.getName());
    }

    @PostMapping("/edit/{id}")
    public VlanDTO actualizar(
            @PathVariable Long id,
            @RequestBody CrearVlanDTO dto,
            Authentication auth
    ) {
        return vlanService.actualizarVlan(id, dto, auth.getName());
    }

    @DeleteMapping("/{id}")
    public void eliminar(
            @PathVariable Long id,
            Authentication auth
    ) {
        vlanService.eliminarVlan(id, auth.getName());
    }
}

package com.smartnetwork.backend.Controller.switches;

import com.smartnetwork.backend.Service.switches.AclService;
import com.smartnetwork.backend.domain.Entity.switches.Acl;
import com.smartnetwork.backend.domain.dtos.switches.acls.AclDTO;
import com.smartnetwork.backend.domain.dtos.switches.acls.CrearAclDTO;
import com.smartnetwork.backend.domain.dtos.switches.acls.CrearReglaDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/switch/acls")
public class AclController {

    private final AclService aclService;

    public AclController(AclService aclService) {
        this.aclService = aclService;
    }

    // 🔥 LISTAR
    @GetMapping("/dispositivo/{id}")
    public List<AclDTO> listar(@PathVariable Long id) {
        return aclService.listarPorDispositivo(id);
    }

    // 🔥 CREAR
    @PostMapping("/create")
    public Acl crear(
            @RequestBody CrearAclDTO dto,
            Authentication auth
    ) {
        return aclService.crearAcl(dto, auth.getName());
    }

    // 🔥 AÑADIR REGLA
    @PostMapping("/{aclId}/reglas")
    public void agregarRegla(
            @PathVariable Long aclId,
            @RequestBody CrearReglaDTO dto,
            Authentication auth
    ) {
        aclService.agregarRegla(aclId, dto, auth.getName());
    }

    // 🔥 ELIMINAR REGLA
    @DeleteMapping("/reglas/{reglaId}")
    public void eliminarRegla(
            @PathVariable Long reglaId,
            Authentication auth
    ) {
        aclService.eliminarRegla(reglaId, auth.getName());
    }

    // 🔥 ELIMINAR ACL
    @DeleteMapping("/{id}")
    public void eliminar(
            @PathVariable Long id,
            Authentication auth
    ) {
        aclService.eliminarAcl(id, auth.getName());
    }
}
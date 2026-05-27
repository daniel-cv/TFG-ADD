package com.smartnetwork.backend.Controller.switches;

import com.smartnetwork.backend.Service.switches.IpRouteService;
import com.smartnetwork.backend.domain.dtos.switches.IpRoute.CrearIpRouteDTO;
import com.smartnetwork.backend.domain.dtos.switches.IpRoute.IpRouteDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/switch/iproutes")
public class IpRouteController {

    private final IpRouteService IpRouteService;

    public IpRouteController(IpRouteService IpRouteService) {
        this.IpRouteService = IpRouteService;
    }

    @GetMapping("/dispositivo/{id}")
    public List<IpRouteDTO> listar(@PathVariable Long id) {
        return IpRouteService.listarPorDispositivo(id, "test");
    }

    @PostMapping("/create")
    public IpRouteDTO crear(
            @RequestBody CrearIpRouteDTO dto,
            Authentication auth
    ) {
        return IpRouteService.crearIpRoute(dto, auth.getName());
    }

    @PostMapping("/edit/{id}")
    public IpRouteDTO actualizar(
               @PathVariable Long id,
               @RequestBody CrearIpRouteDTO dto,
               Authentication auth
    ) {
           return IpRouteService.actualizarIpRoute(id, dto, auth.getName());
    }

    @DeleteMapping("/{id}")
    public void eliminar(
            @PathVariable Long id,
            Authentication auth
    ) {
        IpRouteService.eliminarIpRoute(id, auth.getName());
    }
}

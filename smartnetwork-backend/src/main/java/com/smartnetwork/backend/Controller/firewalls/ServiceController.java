package com.smartnetwork.backend.Controller.firewalls;

import com.smartnetwork.backend.Service.firewalls.ServiceService;
import com.smartnetwork.backend.domain.Entity.firewalls.Service.Service;
import com.smartnetwork.backend.domain.dtos.firewalls.Services.CrearServiceDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.Services.ServiceDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/firewalls/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping("/create")
    public ServiceDTO crear(
            @RequestBody CrearServiceDTO dto,
            Authentication auth
    ) {
        return serviceService.crearService(dto, auth.getName());
    }

    @PostMapping("/full")
    public void crearCompleto(
            @RequestBody CrearServiceDTO dto,
            Authentication auth
    ) {
        serviceService.crearCompleto(dto, auth.getName());
    }

    @PostMapping("/{serviceId}/dispositivos")
    public void asignar(
            @PathVariable Long serviceId,
            @RequestBody List<Long> dispositivosIds,
            Authentication auth
    ) {
        serviceService.asignarServiceADispositivos(serviceId, dispositivosIds, auth.getName());
    }

    @GetMapping("/dispositivo/{dispositivoId}")
    public List<ServiceDTO> listarPorDispositivo(
            @PathVariable Long dispositivoId,
            Authentication auth
    ) {
        return serviceService.listarPorDispositivo(dispositivoId, auth.getName());
    }

    @GetMapping("/usuario")
    public List<ServiceDTO> listarPorUsuario(Authentication auth) {
        return serviceService.listarPorUsuario(auth.getName());
    }

    @GetMapping("/entidad/dispositivo/{dispositivoId}")
    public List<Service> listarEntidadPorDispositivo(
            @PathVariable Long dispositivoId,
            Authentication auth
    ) {
        return serviceService.findAllByDispositivo(dispositivoId, auth.getName());
    }

    @GetMapping("/{serviceId}/dispositivo/{dispositivoId}")
    public Optional<Service> obtenerPorId(
            @PathVariable Long serviceId,
            @PathVariable Long dispositivoId,
            Authentication auth
    ) {
        return serviceService.findById(serviceId, dispositivoId, auth.getName());
    }


    @DeleteMapping("/delete/{serviceId}")
    public void eliminar(
            @PathVariable Long serviceId,
            @RequestBody List<Long> dispositivosIds,
            Authentication auth
    ) {
        serviceService.eliminarService(serviceId,dispositivosIds, auth.getName());
    }

    @DeleteMapping("/predelete/{serviceId}")
    public void preeliminar(
            @PathVariable Long serviceId,
            Authentication auth
    ) {
        serviceService.preeliminarService(serviceId, auth.getName());
    }
}


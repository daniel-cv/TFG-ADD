package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Service.ServiceService;
import com.smartnetwork.backend.domain.Entity.Service;
import com.smartnetwork.backend.domain.dtos.Services.CrearServiceDTO;
import com.smartnetwork.backend.domain.dtos.Services.ServiceDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.bind.annotation.*;
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

    @PutMapping("/{serviceId}/dispositivos")
    public Service actualizar(
            @PathVariable Long serviceId,
            @RequestBody List<Long> dispositivoIds,
            Authentication auth
    ) {
        Service service = new Service();
        service.setId(serviceId);
        System.out.println(dispositivoIds);
        return serviceService.update(service, auth.getName(), dispositivoIds);
    }

    @DeleteMapping("/delete/{serviceId}")
    public void eliminar(
            @PathVariable Long serviceId,
            Authentication auth
    ) {
        serviceService.eliminarService(serviceId, auth.getName());
    }
}


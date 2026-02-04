package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Service.ServiceService;
import com.smartnetwork.backend.domain.Entity.Service;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dispositivos/{dispositivoId}/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    // 🔹 CREAR service
    @PostMapping
    public Service crear(
            @PathVariable Long dispositivoId,
            @RequestBody Service service,
            Authentication authentication
    ) {
        String username = authentication.getName();

        // aseguramos asociación correcta
        service.getDispositivo().setId(dispositivoId);

        return serviceService.create(service, username, dispositivoId);
    }

    // 🔹 LISTAR services de un dispositivo
    @GetMapping("/findAll")
    public List<Service> listar(
            @PathVariable Long dispositivoId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return serviceService.findAllByDispositivo(dispositivoId, username);
    }

    // 🔹 OBTENER un service
    @GetMapping("/get/{serviceId}")
    public Service obtener(
            @PathVariable Long dispositivoId,
            @PathVariable Long serviceId,
            Authentication authentication
    ) {
        String username = authentication.getName();

        return serviceService
                .findById(serviceId, dispositivoId, username)
                .orElseThrow(() -> new RuntimeException("Service no encontrado"));
    }

    // 🔹 ACTUALIZAR
    @PutMapping("/update/{serviceId}")
    public Service actualizar(
            @PathVariable Long dispositivoId,
            @PathVariable Long serviceId,
            @RequestBody Service service,
            Authentication authentication
    ) {
        String username = authentication.getName();

        service.setId(serviceId);
        service.getDispositivo().setId(dispositivoId);

        return serviceService.update(service, username, dispositivoId);
    }

    // 🔹 ELIMINAR
    @DeleteMapping("/delete/{serviceId}")
    public void eliminar(
            @PathVariable Long dispositivoId,
            @PathVariable Long serviceId,
            Authentication authentication
    ) {
        String username = authentication.getName();

        Service service = serviceService
                .findById(serviceId, dispositivoId, username)
                .orElseThrow(() -> new RuntimeException("Service no encontrado"));

        serviceService.delete(service, dispositivoId, username);
    }
}

package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.ServiceRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final DispositivoRepository dispositivoRepository;

    public ServiceService(ServiceRepository serviceRepository,
                          DispositivoRepository dispositivoRepository) {
        this.serviceRepository = serviceRepository;
        this.dispositivoRepository = dispositivoRepository;
    }

    public com.smartnetwork.backend.domain.Entity.Service create(
            com.smartnetwork.backend.domain.Entity.Service service,
            String username,
            Long dispositivoId) {

        Dispositivo dispositivo = dispositivoRepository
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        // 🔐 Seguridad: comprobar propietario
        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        service.setDispositivo(dispositivo);
        return serviceRepository.save(service);
    }

    public com.smartnetwork.backend.domain.Entity.Service update(
            com.smartnetwork.backend.domain.Entity.Service service,
            String username,
            Long dispositivoId) {

        Dispositivo dispositivo = dispositivoRepository
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        // 🔐 Seguridad
        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return serviceRepository.save(service);
    }

    public List<com.smartnetwork.backend.domain.Entity.Service> findAllByDispositivo(
            Long dispositivoId,
            String username) {

        Dispositivo dispositivo = dispositivoRepository
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        // 🔐 Seguridad
        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return serviceRepository.findByDispositivoId(dispositivoId);
    }

    public Optional<com.smartnetwork.backend.domain.Entity.Service> findById(
            Long serviceId,
            Long dispositivoId,
            String username) {

        Dispositivo dispositivo = dispositivoRepository
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        // 🔐 Seguridad
        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return serviceRepository.findByIdAndDispositivoId(serviceId, dispositivoId);
    }

    public void delete(
            com.smartnetwork.backend.domain.Entity.Service service,
            Long dispositivoId,
            String username) {

        Dispositivo dispositivo = dispositivoRepository
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        // 🔐 Seguridad
        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        serviceRepository.delete(service);
    }
}

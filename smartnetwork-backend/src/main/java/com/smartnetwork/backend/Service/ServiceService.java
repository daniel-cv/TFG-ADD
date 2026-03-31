package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.ServiceRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.dtos.Services.CrearServiceDTO;
import com.smartnetwork.backend.domain.dtos.Services.ServiceDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final DispositivoRepository dispositivoRepository;
    private final FortiGateService fortiGateService;

    public ServiceService(ServiceRepository serviceRepository,
                          DispositivoRepository dispositivoRepository, FortiGateService fortiGateService) {
        this.serviceRepository = serviceRepository;
        this.dispositivoRepository = dispositivoRepository;
        this.fortiGateService = fortiGateService;
    }

    public com.smartnetwork.backend.domain.Entity.Service create(CrearServiceDTO dto, String username) {

        Dispositivo dispositivo = dispositivoRepository
                .findById(dto.getDispositivoId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }
        com.smartnetwork.backend.domain.Entity.Service service =
                new com.smartnetwork.backend.domain.Entity.Service();
        service.setNombre(dto.getNombre());
        service.setDispositivo(dispositivo);
        service.setTipoProtocolo(dto.getTipoProtocolo());
        service.setDestinationPort(dto.getDestinationPort());
        service.setIp(dto.getIp());
        service.setComentario(dto.getComentario());
        Map<String, Object> resultado = fortiGateService.crearServicio(dispositivo, service);

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException(
                    "Error creando service en FortiGate: " + resultado
            );
        }

        serviceRepository.save(service);

        return service;
    }

    public com.smartnetwork.backend.domain.Entity.Service update(
            com.smartnetwork.backend.domain.Entity.Service service,
            String username,
            Long dispositivoId) {

        Dispositivo dispositivo = dispositivoRepository
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

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

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return serviceRepository.findByIdAndDispositivoId(serviceId, dispositivoId);
    }
    private ServiceDTO toDTO(com.smartnetwork.backend.domain.Entity.Service service) {
        ServiceDTO dto = new ServiceDTO();
        dto.setId(service.getId());
        dto.setNombre(service.getNombre());
        dto.setTipoProtocolo(service.getTipoProtocolo());
        dto.setIp(service.getIp());
        dto.setDestinationPort(service.getDestinationPort());
        dto.setComentario(service.getComentario());
        dto.setDispositivoId(service.getDispositivo().getId());
        return dto;
    }

    public void eliminarService(Long serviceId, String username) {
        com.smartnetwork.backend.domain.Entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service no existe"));

        if (!service.getDispositivo().getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        Map<String, Object> resultado = fortiGateService.eliminarService(service.getDispositivo(), service.getNombre());

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException("Error eliminando Service en FortiGate: " + resultado);
        }

        serviceRepository.delete(service);
    }

}

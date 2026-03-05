package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.ServiceRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.dtos.Services.CrearServiceDTO;
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

        // Crear objeto Service en memoria (no guardado todavía)
        com.smartnetwork.backend.domain.Entity.Service service =
                new com.smartnetwork.backend.domain.Entity.Service();
        service.setNombre(dto.getNombre());
        service.setDispositivo(dispositivo);
        service.setTipoProtocolo(dto.getTipoProtocolo());
        service.setDestinationPort(dto.getDestinationPort());
        service.setIp(dto.getIp());
        service.setComentario(dto.getComentario());

        // 🔹 PUSH AL FORTIGATE ANTES DE GUARDAR
        Map<String, Object> resultado = fortiGateService.crearServicio(dispositivo, service);

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException(
                    "Error creando service en FortiGate: " + resultado
            );
        }

        // 🔹 Guardamos solo si FortiGate tuvo éxito
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

    public void eliminarService(Long serviceId, String username) {
        com.smartnetwork.backend.domain.Entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service no existe"));

        if (!service.getDispositivo().getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        Dispositivo dispositivo = service.getDispositivo();
        String serviceName = service.getNombre();

        // Apuntar a 'uncategorized' porque se crean así
        String url = "http://" + dispositivo.getIp()
                + "/api/v2/cmdb/firewall.service/custom/"
                + URLEncoder.encode(serviceName, StandardCharsets.UTF_8).replace("+", "%20")
                + "?vdom=root";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(dispositivo.getToken());

        HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                // Primero eliminar en FortiGate → luego BBDD
                serviceRepository.delete(service);
            } else {
                throw new RuntimeException(
                        "FortiGate respondió con estado: " + response.getStatusCode()
                );
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error eliminando Service en FortiGate (Service=" + serviceName + ")", e
            );
        }
    }
}

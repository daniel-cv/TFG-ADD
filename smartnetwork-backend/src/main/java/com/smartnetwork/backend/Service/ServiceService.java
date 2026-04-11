package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.DispositivoServiceRepository;
import com.smartnetwork.backend.Repository.ServiceRepository;
import com.smartnetwork.backend.Repository.UsuarioRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.DispositivoService;
import com.smartnetwork.backend.domain.Entity.Usuario;
import com.smartnetwork.backend.domain.dtos.Services.CrearServiceDTO;
import com.smartnetwork.backend.domain.dtos.Services.ServiceDTO;
import jakarta.transaction.Transactional;
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
    private final UsuarioRepository usuarioRepository;
    private final DispositivoServiceRepository dispositivoServiceRepository;

    public ServiceService(ServiceRepository serviceRepository,
                          DispositivoRepository dispositivoRepository, FortiGateService fortiGateService, UsuarioRepository usuarioRepository, DispositivoServiceRepository dispositivoServiceRepository) {
        this.serviceRepository = serviceRepository;
        this.dispositivoRepository = dispositivoRepository;
        this.fortiGateService = fortiGateService;
        this.usuarioRepository = usuarioRepository;
        this.dispositivoServiceRepository = dispositivoServiceRepository;
    }

    @Transactional
    public ServiceDTO crearService(CrearServiceDTO dto, String username) {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        com.smartnetwork.backend.domain.Entity.Service service = new com.smartnetwork.backend.domain.Entity.Service();
        service.setNombre(dto.getNombre());
        service.setTipoProtocolo(dto.getTipoProtocolo());
        service.setDestinationPort(dto.getDestinationPort());
        service.setIp(dto.getIp());
        service.setComentario(dto.getComentario());
        service.setUsuario(usuario);

        com.smartnetwork.backend.domain.Entity.Service saved = serviceRepository.save(service);

        return toDTO(saved);
    }

    @Transactional
    public void crearCompleto(CrearServiceDTO dto, String username) {

        ServiceDTO serviceDTO = crearService(dto, username);

        asignarServiceADispositivos(
                serviceDTO.getId(),
                dto.getDispositivosId(),
                username
        );
    }

    @Transactional
    public void asignarServiceADispositivos(Long serviceId, List<Long> dispositivosIds, String username) {

        com.smartnetwork.backend.domain.Entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service no existe"));

        if (!service.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        for (Long dispositivoId : dispositivosIds) {

            Dispositivo dispositivo = dispositivoRepository.findById(dispositivoId)
                    .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

            if (!dispositivo.getUsuario().getUsername().equals(username)) {
                throw new RuntimeException("No autorizado");
            }

            boolean yaExiste = dispositivoServiceRepository
                    .existsByDispositivoIdAndServiceId(dispositivoId, serviceId);

            if (yaExiste) continue;

            Map<String, Object> resultado = fortiGateService.crearServicio(dispositivo, service);

            if (!(Boolean) resultado.get("success")) {
                throw new RuntimeException("Error creando service en FortiGate: " + resultado);
            }

            com.smartnetwork.backend.domain.Entity.DispositivoService rel = new DispositivoService(dispositivo, service, service.getComentario());
            dispositivoServiceRepository.save(rel);
        }
    }

    public List<ServiceDTO> listarPorDispositivo(Long dispositivoId, String username) {

        Dispositivo dispositivo = dispositivoRepository.findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return dispositivoServiceRepository.findByDispositivoId(dispositivoId)
                .stream()
                .map(rel -> toDTO(rel.getService(), dispositivoId))
                .toList();
    }

    public List<ServiceDTO> listarPorUsuario(String username) {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return serviceRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::toDTO)
                .toList();
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

        return serviceRepository.findByDispositivoServices_Dispositivo_Id(dispositivoId);
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

        return serviceRepository.findByIdAndDispositivoServices_Dispositivo_Id(serviceId, dispositivoId);
    }

    public void eliminarService(Long serviceId, String username) {

        com.smartnetwork.backend.domain.Entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service no existe"));

        if (!service.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        List<DispositivoService> relaciones = dispositivoServiceRepository.findByServiceId(serviceId);

        for (DispositivoService rel : relaciones) {

            Dispositivo dispositivo = rel.getDispositivo();

            Map<String, Object> resultado = fortiGateService.eliminarService(dispositivo, service.getNombre());

            if (!(Boolean) resultado.get("success")) {
                throw new RuntimeException("Error eliminando Service en FortiGate: " + resultado);
            }

            dispositivoServiceRepository.delete(rel);
        }

        serviceRepository.delete(service);
    }

    private ServiceDTO toDTO(com.smartnetwork.backend.domain.Entity.Service service) {
        ServiceDTO dto = new ServiceDTO();
        dto.setId(service.getId());
        dto.setNombre(service.getNombre());
        dto.setTipoProtocolo(service.getTipoProtocolo());
        dto.setIp(service.getIp());
        dto.setDestinationPort(service.getDestinationPort());
        dto.setComentario(service.getComentario());
        return dto;
    }

    private ServiceDTO toDTO(com.smartnetwork.backend.domain.Entity.Service service, Long dispositivoId) {
        ServiceDTO dto = toDTO(service);
        dto.setDispositivoId(dispositivoId);
        return dto;
    }

}

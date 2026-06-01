package com.smartnetwork.backend.Service.firewalls;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.firewalls.Interfaz.InterfazRepository;
import com.smartnetwork.backend.Repository.firewalls.VirtualIpRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.firewalls.Interfaz.Interfaz;
import com.smartnetwork.backend.domain.Entity.firewalls.VirtualIp;
import com.smartnetwork.backend.domain.dtos.firewalls.virtualIp.CrearVirtualIpDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.virtualIp.VirtualIpDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class VirtualIpService {

    private final VirtualIpRepository virtualIpRepo;
    private final DispositivoRepository dispositivoRepo;
    private final InterfazRepository interfazRepo;
    private final FortiGateService fortiGateService;

    public VirtualIpService(VirtualIpRepository virtualIpRepo,
                            DispositivoRepository dispositivoRepo,
                            InterfazRepository interfazRepo,
                            FortiGateService fortiGateService) {
        this.virtualIpRepo = virtualIpRepo;
        this.dispositivoRepo = dispositivoRepo;
        this.interfazRepo = interfazRepo;
        this.fortiGateService = fortiGateService;
    }

    public VirtualIpDTO crear(CrearVirtualIpDTO dto, String username) {

        if (dto.getDispositivoId() == null)
            throw new RuntimeException("dispositivoId obligatorio");

        Dispositivo dispositivo = dispositivoRepo.findById(dto.getDispositivoId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username))
            throw new RuntimeException("No autorizado");

        VirtualIp vip = new VirtualIp();
        vip.setName(dto.getName());
        vip.setComments(dto.getComments());
        if (dto.getInterfazId() != null) {
            Interfaz interfaz = interfazRepo.findById(dto.getInterfazId())
                    .orElseThrow(() -> new RuntimeException("Interfaz no existe"));
            vip.setInterfaz(interfaz);
        }
        vip.setType(dto.getType());
        vip.setExternal_ip(dto.getExternalIp());
        vip.setInternal_ip(dto.getInternalIp());
        vip.setDispositivo(dispositivo);

        Map<String, Object> result = fortiGateService.crearVirtualIp(dispositivo, vip);

        if (!(Boolean) result.get("success")) {
            throw new RuntimeException("Error creando VirtualIP en FortiGate: " + result);
        }

        VirtualIp saved = virtualIpRepo.save(vip);

        return toDTO(saved);
    }
    public List<VirtualIpDTO> listarPorDispositivo(Long dispositivoId, String username) {

        Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username))
            throw new RuntimeException("No autorizado");

        return virtualIpRepo.findByDispositivoId(dispositivoId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private VirtualIpDTO toDTO(VirtualIp vip) {
        VirtualIpDTO dto = new VirtualIpDTO();
        dto.setId(vip.getId());
        dto.setName(vip.getName());
        dto.setComments(vip.getComments());
        dto.setInterfazId(vip.getInterfaz().getId());
        dto.setType(vip.getType());
        dto.setExternalIp(vip.getExternal_ip());
        dto.setInternalIp(vip.getInternal_ip());
        dto.setDispositivoId(vip.getDispositivo().getId());
        return dto;
    }

    public void eliminarVirtualIp(Long virtualIpId, String username) {
        VirtualIp virtualIp = virtualIpRepo.findById(virtualIpId)
                .orElseThrow(() -> new RuntimeException("VirtualIP no existe"));

        if (!virtualIp.getDispositivo().getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        Dispositivo dispositivo = virtualIp.getDispositivo();
        String virtualIpName = virtualIp.getName();

        String url = "http://" + dispositivo.getIp()
                + "/api/v2/cmdb/firewall/vip/"
                + URLEncoder.encode(virtualIpName, StandardCharsets.UTF_8)
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
                virtualIpRepo.delete(virtualIp);
            } else {
                throw new RuntimeException(
                        "FortiGate respondió con estado: " + response.getStatusCode()
                );
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error eliminando VirtualIP en FortiGate (Address=" + virtualIpName + ")", e
            );
        }
    }

    public VirtualIpDTO actualizar(Long virtualIpId, CrearVirtualIpDTO dto, String username) {

        VirtualIp virtualIp = virtualIpRepo.findById(virtualIpId)
                .orElseThrow(() -> new RuntimeException("VirtualIP no existe"));

        if (!virtualIp.getDispositivo().getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        virtualIp.setInterfaz(interfazRepo.findById(dto.getInterfazId()).orElseThrow(() ->
                new RuntimeException("Interfaz no existe")));
        virtualIp.setExternal_ip(dto.getExternalIp());
        virtualIp.setInternal_ip(dto.getInternalIp());
        virtualIp.setComments(dto.getComments());

        Map<String, Object> resultado =
                fortiGateService.editarVirtualIp(virtualIp.getDispositivo(), virtualIp, virtualIp.getName());

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException(
                    "Error actualizando VirtualIP en FortiGate: " + resultado
            );
        }

        VirtualIp saved = virtualIpRepo.save(virtualIp);
        return toDTO(saved);
    }

}

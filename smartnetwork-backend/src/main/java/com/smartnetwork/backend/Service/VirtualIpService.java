package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.InterfazRepository;
import com.smartnetwork.backend.Repository.VirtualIpRepository;
import com.smartnetwork.backend.domain.Entity.firewalls.fortinet.Dispositivo;
import com.smartnetwork.backend.domain.Entity.firewalls.fortinet.Interfaz;
import com.smartnetwork.backend.domain.Entity.firewalls.fortinet.VirtualIp;
import com.smartnetwork.backend.domain.dtos.firewalls.virtualIp.CrearVirtualIpDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.virtualIp.VirtualIpDTO;
import org.springframework.stereotype.Service;

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

        if (dto.getDispositivoId() == null) throw new RuntimeException("dispositivoId obligatorio");

        Dispositivo dispositivo = dispositivoRepo.findById(dto.getDispositivoId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username))
            throw new RuntimeException("No autorizado");



        VirtualIp vip = new VirtualIp();
        vip.setName(dto.getName());
        vip.setComments(dto.getComments());
        if(dto.getInterfazId()!=null) {
            Interfaz interfaz = interfazRepo.findById(dto.getInterfazId())
                    .orElseThrow(() -> new RuntimeException("Interfaz no existe"));
            vip.setInterfaz(interfaz);
        }

        vip.setType(dto.getType());
        vip.setExternal_ip(dto.getExternalIp());
        vip.setInternal_ip(dto.getInternalIp());
        vip.setDispositivo(dispositivo);

        VirtualIp saved = virtualIpRepo.save(vip);

        Map<String, Object> result = fortiGateService.crearVirtualIp(dispositivo, saved);

        if (!(Boolean) result.get("success")) {
            throw new RuntimeException("Error creando VirtualIP en FortiGate: " + result);
        }

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
}

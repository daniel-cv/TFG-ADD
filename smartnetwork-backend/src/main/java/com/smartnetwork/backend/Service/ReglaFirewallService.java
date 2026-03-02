package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.ReglaFirewallRepository;
import com.smartnetwork.backend.domain.Entity.firewalls.fortinet.Dispositivo;
import com.smartnetwork.backend.domain.Entity.firewalls.fortinet.ReglaFirewall;
import com.smartnetwork.backend.domain.dtos.firewalls.Policys.CrearReglaFirewallDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.Policys.ReglaFirewallDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReglaFirewallService {

    private final ReglaFirewallRepository reglaRepo;
    private final DispositivoRepository dispositivoRepo;
    private final FortiGateService fortiGateService;

    public ReglaFirewallService(ReglaFirewallRepository reglaRepo,
                                DispositivoRepository dispositivoRepo,
                                FortiGateService fortiGateService) {
        this.reglaRepo = reglaRepo;
        this.dispositivoRepo = dispositivoRepo;
        this.fortiGateService = fortiGateService;
    }

    public ReglaFirewallDTO crearRegla(CrearReglaFirewallDTO dto,
                                       String username) {

        if (dto.getDispositivoId() == null) {
            throw new RuntimeException("dispositivoId obligatorio");
        }

        Dispositivo dispositivo = dispositivoRepo
                .findById(dto.getDispositivoId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        ReglaFirewall regla = new ReglaFirewall();
        regla.setNombre(dto.getNombre());
        regla.setOrigen(dto.getOrigen());
        regla.setDestino(dto.getDestino());
        regla.setIporigen(dto.getIpOrigen());
        regla.setIpdestino(dto.getIpDestino());
        regla.setServicio(dto.getServicio());
        regla.setDispositivo(dispositivo);
        regla.setHabilitada(true);

        reglaRepo.save(regla);

        // 🔥 PUSH AL FORTIGATE
        Map<String, Object> resultado =
                fortiGateService.crearPolicy(dispositivo, regla);

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException(
                    "Error creando policy en FortiGate: " + resultado
            );
        }

        return toDTO(regla);
    }


    public List<ReglaFirewall> obtenerPorDispositivo(Long dispositivoId, String username) {

        Dispositivo dispositivo = dispositivoRepo
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return reglaRepo.findByDispositivoId(dispositivoId);
    }

    private ReglaFirewallDTO toDTO(ReglaFirewall regla) {
        ReglaFirewallDTO dto = new ReglaFirewallDTO();
        dto.setId(regla.getId());
        dto.setNombre(regla.getNombre());
        dto.setOrigen(regla.getOrigen());
        dto.setDestino(regla.getDestino());
        dto.setIpOrigen(regla.getIporigen());
        dto.setIpDestino(regla.getIpdestino());
        dto.setServicio(regla.getServicio());
        dto.setHabilitada(regla.isHabilitada());
        dto.setDispositivoId(regla.getDispositivo().getId());
        return dto;
    }
}

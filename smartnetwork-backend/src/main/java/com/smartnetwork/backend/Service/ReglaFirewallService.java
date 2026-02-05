package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Controller.DispositivoController;
import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.ReglaFirewallRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.ReglaFirewall;
import com.smartnetwork.backend.domain.dtos.CrearReglaFirewallDTO;
import com.smartnetwork.backend.domain.dtos.ReglaFirewallDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReglaFirewallService {

    private final ReglaFirewallRepository reglaRepo;
    private final DispositivoRepository dispositivoRepo;

    public ReglaFirewallService(ReglaFirewallRepository reglaRepo,
                                DispositivoRepository dispositivoRepo) {
        this.reglaRepo = reglaRepo;
        this.dispositivoRepo = dispositivoRepo;
    }

    public ReglaFirewallDTO crearRegla(CrearReglaFirewallDTO dto, String username) {

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
        regla.setIporigen(dto.getOrigen());
        regla.setIpdestino(dto.getDestino());
        regla.setServicio(dto.getServicio());
        regla.setDispositivo(dispositivo);
        regla.setHabilitada(true);

        reglaRepo.save(regla);

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

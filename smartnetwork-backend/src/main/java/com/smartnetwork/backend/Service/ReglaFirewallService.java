package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.ReglaFirewallRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.ReglaFirewall;
import com.smartnetwork.backend.domain.dtos.Policys.CrearReglaFirewallDTO;
import com.smartnetwork.backend.domain.dtos.Policys.ReglaFirewallDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
        regla.setIporigen(dto.getIpOrigen());
        regla.setIpdestino(dto.getIpDestino());
        regla.setServicio(dto.getServicio());
        regla.setDispositivo(dispositivo);
        regla.setHabilitada(true);

        Map<String, Object> resultado = fortiGateService.crearPolicy(dispositivo, regla);

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException(
                    "Error creando policy en FortiGate: " + resultado
            );
        }

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

    public void eliminarRegla(Long reglaId, String username) {
        ReglaFirewall regla = reglaRepo.findById(reglaId)
                .orElseThrow(() -> new RuntimeException("Regla no existe"));

        if (!regla.getDispositivo().getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        Map<String, Object> resultado = fortiGateService.eliminarReglaFirewall(
                regla.getDispositivo(),
                regla.getNombre()
        );

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException("Error eliminando ReglaFirewall en FortiGate: " + resultado);
        }

        reglaRepo.delete(regla);
    }


    public ReglaFirewallDTO editarReglaFirewall(Long id ,CrearReglaFirewallDTO dto, String username) {

        if (dto.getDispositivoId() == null) {
            throw new RuntimeException("dispositivoId obligatorio");
        }

        Dispositivo dispositivo = dispositivoRepo
                .findById(dto.getDispositivoId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        ReglaFirewall regla = reglaRepo.findById(id).orElseThrow(() ->
                new RuntimeException("ReglaFirewall no existe"));
        regla.setNombre(dto.getNombre());
        regla.setOrigen(dto.getOrigen());
        regla.setDestino(dto.getDestino());
        regla.setIporigen(dto.getIpOrigen());
        regla.setIpdestino(dto.getIpDestino());
        regla.setServicio(dto.getServicio());
        regla.setDispositivo(dispositivo);
        regla.setHabilitada(true);

        Map<String, Object> resultado = fortiGateService.editarPolicy(dispositivo, regla);

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException(
                    "Error editando policy en FortiGate: " + resultado
            );
        }

        reglaRepo.save(regla);

        return toDTO(regla);
    }
}

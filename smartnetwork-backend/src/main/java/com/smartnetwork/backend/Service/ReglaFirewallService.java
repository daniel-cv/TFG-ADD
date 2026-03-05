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

        // Crear objeto Regla en memoria (no guardada aún)
        ReglaFirewall regla = new ReglaFirewall();
        regla.setNombre(dto.getNombre());
        regla.setOrigen(dto.getOrigen());
        regla.setDestino(dto.getDestino());
        regla.setIporigen(dto.getIpOrigen());
        regla.setIpdestino(dto.getIpDestino());
        regla.setServicio(dto.getServicio());
        regla.setDispositivo(dispositivo);
        regla.setHabilitada(true);

        // 🔹 PUSH AL FORTIGATE ANTES DE GUARDAR
        Map<String, Object> resultado = fortiGateService.crearPolicy(dispositivo, regla);

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException(
                    "Error creando policy en FortiGate: " + resultado
            );
        }

        // 🔹 Solo guardamos si FortiGate tuvo éxito
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
        ReglaFirewall reglaFirewall = reglaRepo.findById(reglaId)
                .orElseThrow(() -> new RuntimeException("Regla no existe"));

        if (!reglaFirewall.getDispositivo().getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        Dispositivo dispositivo = reglaFirewall.getDispositivo();
        String policyName = reglaFirewall.getNombre(); // nombre de la regla en FortiGate

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(dispositivo.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);

        try {
            String getUrl = "http://" + dispositivo.getIp() + "/api/v2/cmdb/firewall/policy?vdom=root";
            ResponseEntity<Map> response = restTemplate.exchange(getUrl, HttpMethod.GET, requestEntity, Map.class);

            if (response.getStatusCode() != HttpStatus.OK || !response.hasBody()) {
                throw new RuntimeException("No se pudieron obtener las políticas de FortiGate");
            }

            List<Map<String, Object>> policies = (List<Map<String, Object>>) response.getBody().get("results");
            Integer policyId = null;

            for (Map<String, Object> policy : policies) {
                if (policyName.equals(policy.get("name"))) {
                    policyId = (Integer) policy.get("policyid");
                    break;
                }
            }

            if (policyId == null) {
                throw new RuntimeException("No se encontró la regla en FortiGate con nombre: " + policyName);
            }

            String deleteUrl = "http://" + dispositivo.getIp() + "/api/v2/cmdb/firewall/policy/" + policyId + "?vdom=root";
            ResponseEntity<String> deleteResponse = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, requestEntity, String.class);

            if (deleteResponse.getStatusCode() == HttpStatus.OK) {
                reglaRepo.delete(reglaFirewall);
            } else {
                throw new RuntimeException("FortiGate respondió con estado: " + deleteResponse.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("Error eliminando regla en FortiGate (nombre=" + policyName + ")", e);
        }
    }
}

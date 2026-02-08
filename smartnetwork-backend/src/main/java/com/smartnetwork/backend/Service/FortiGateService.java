package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.domain.Entity.Address;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.ReglaFirewall;
import com.smartnetwork.backend.domain.dtos.address.AddressDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class FortiGateService {

    private final RestTemplate restTemplate;

    public FortiGateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> crearPolicy(Dispositivo dispositivo,
                                           ReglaFirewall regla) {

        String url = "http://" + dispositivo.getIp()
                + "/api/v2/cmdb/firewall/policy?vdom=root";

        Map<String, Object> result = new HashMap<>();

        String json = """
{
    "name": "%s",
    "srcintf": [{ "name": "%s" }],
    "dstintf": [{ "name": "%s" }],
    "srcaddr": [{ "name": "%s" }],
    "dstaddr": [{ "name": "%s" }],
    "service": [{ "name": "%s" }],
    "schedule": "always",
    "action": "accept",
    "status": "%s",
    "nat": "enable"
}
""".formatted(
                regla.getNombre(),
                regla.getOrigen(),
                regla.getDestino(),
                regla.getIporigen(),
                regla.getIpdestino(),
                regla.getServicio(),
                regla.isHabilitada() ? "enable" : "disable"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(dispositivo.getToken().trim());

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getBody() != null &&
                    response.getBody().contains("\"status\":\"success\"")) {

                result.put("success", true);
            } else {
                result.put("success", false);
                result.put("error", response.getBody());
            }

            result.put("httpStatus", response.getStatusCode());

        } catch (Exception e) {
            result.put("success", false);
            result.put("exception", e.getMessage());
        }

        return result;
    }
    public Map<String, Object> crearAddress(Dispositivo dispositivo, Address address) {

        String url = "http://" + dispositivo.getIp() +
                "/api/v2/cmdb/firewall/address?vdom=root";

        Map<String, Object> result = new HashMap<>();

        String json = """
        {
          "name": "%s",
          "type": "%s",
          "subnet": "%s"
        }
        """.formatted(address.getName(), address.getType(), address.getIp());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(dispositivo.getToken().trim());

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getBody() != null &&
                    response.getBody().contains("\"status\":\"success\"")) {
                result.put("success", true);
            }else {
                result.put("success", false);
                result.put("error", response.getBody());
            }

            result.put("httpStatus", response.getStatusCode());

        }catch (Exception e) {
            result.put("success", false);
            result.put("exception", e.getMessage());
        }

        return result;
    }
    private AddressDTO toDTO(Address address) {

        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setName(address.getName());
        dto.setType(address.getType());
        dto.setIp(address.getIp());
        dto.setComentario(address.getComentario());
        dto.setDispositivoId(address.getDispositivo().getId());

        if (address.getInterfaz() != null) {
            dto.setInterfazId(address.getInterfaz().getId());
        }

        return dto;
    }
}

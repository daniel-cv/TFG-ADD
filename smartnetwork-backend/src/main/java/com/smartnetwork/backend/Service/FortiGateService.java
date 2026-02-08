package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.domain.Entity.*;
import com.smartnetwork.backend.domain.dtos.Services.ServiceJsonBuilder;
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

    public Map<String, Object> crearServicio(Dispositivo dispositivo, com.smartnetwork.backend.domain.Entity.Service service){
        String url = "http://" + dispositivo.getIp() +
                "/api/v2/cmdb/firewall.service/custom";


        String json = ServiceJsonBuilder.build(service);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(dispositivo.getToken().trim());

        HttpEntity<String> entity =
                new HttpEntity<>(json, headers);

        Map<String, Object> result = new HashMap<>();

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

    public Map<String, Object> crearInterfaz(Dispositivo dispositivo, Interfaz interfaz) {

        String url = "http://" + dispositivo.getIp()
                + "/api/v2/cmdb/system/interface?vdom=root";

        Map<String, Object> result = new HashMap<>();

        // Construcción del JSON mínimo
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        jsonBuilder.append("""
        "name": "%s",
        "vdom": "root"
    """.formatted(interfaz.getName()));

        // VLAN
        if ("vlan".equals(interfaz.getTipo())) {
            jsonBuilder.append("""
            ,
            "interface": "%s",
            "vlanid": %d
        """.formatted(
                    interfaz.getInterfacePadre(),
                    interfaz.getVlanid()
            ));
        }

        // IP
        if ("static".equals(interfaz.getMode()) && interfaz.getIp() != null) {
            jsonBuilder.append("""
            ,
            "mode": "static",
            "ip": "%s"
        """.formatted(interfaz.getIp()));
        } else {
            jsonBuilder.append("""
            ,
            "mode": "dhcp"
        """);
        }

        // allowaccess
        if (interfaz.getAllowaccess() != null && !interfaz.getAllowaccess().isBlank()) {
            jsonBuilder.append("""
            ,
            "allowaccess": "%s"
        """.formatted(interfaz.getAllowaccess()));
        }

        // role
        if (interfaz.getRole() != null) {
            jsonBuilder.append("""
            ,
            "role": "%s"
        """.formatted(interfaz.getRole()));
        }

        // description
        if (interfaz.getDescription() != null) {
            jsonBuilder.append("""
            ,
            "description": "%s"
        """.formatted(interfaz.getDescription()));
        }

        jsonBuilder.append("}");

        String json = jsonBuilder.toString();

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

    public Map<String, Object> crearUsuarioFirewall(Dispositivo dispositivo, UsuarioFirewall usuarioFirewall) {
        String url = "http://" + dispositivo.getIp()
                + "/api/v2/cmdb/user/local";

        Map<String, Object> result = new HashMap<>();
        String json = """
        {
          "name": "%s",
          "type": "%s",
          "passwd": "%s"
        }
        """.formatted(usuarioFirewall.getNombre(), usuarioFirewall.getTipo(), usuarioFirewall.getPassword());

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

}

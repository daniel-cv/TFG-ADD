package com.smartnetwork.backend.Service.firewalls;

import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.firewalls.*;
import com.smartnetwork.backend.domain.dtos.firewalls.Services.ServiceJsonBuilder;
import com.smartnetwork.backend.domain.dtos.firewalls.address.AddressJsonBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
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
            "action": "%s",
            "status": "%s",
            "nat": "%s"
        }
        """.formatted(
                regla.getNombre(),
                regla.getOrigen(),
                regla.getDestino(),
                regla.getIporigen(),
                regla.getIpdestino(),
                regla.getServicio(),
                regla.getAction(),
                regla.isHabilitada() ? "enable" : "disable",
                regla.getNat()
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

        String json = AddressJsonBuilder.build(address);
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

    public Map<String, Object> crearServicio(Dispositivo dispositivo, com.smartnetwork.backend.domain.Entity.firewalls.Service service){
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

        String url = "http://" + dispositivo.getIp() + "/api/v2/cmdb/system/interface?vdom=root";
        Map<String, Object> result = new HashMap<>();
        String json = """
    {
        "name": "%s",               
        "vdom": "root",
        "type": "vlan",
        "interface": "%s",          
        "vlanid": %s,              
        "role": "%s",               
        "mode": "dhcp",
        "allowaccess": "%s"
    }
    """.formatted(
                interfaz.getName(),
                interfaz.getInterfacePadre(),
                interfaz.getVlanid(),
                interfaz.getRole(),
                interfaz.getAllowaccess()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(dispositivo.getToken().trim());

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getBody() != null && response.getBody().contains("\"status\":\"success\"")) {
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
    public Map<String, Object> crearVirtualIp(Dispositivo dispositivo, VirtualIp vip) {

        String url = "http://" + dispositivo.getIp()
                + "/api/v2/cmdb/firewall/vip";

        Map<String, Object> result = new HashMap<>();

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        jsonBuilder.append("""
        "name": "%s",
        "type": "%s",
        "extintf": "any",
        "extip": "%s",
        "mappedip":[
        {
            "range": "%s"
        }
        ]
        
        """.formatted(
                vip.getName(),
                vip.getType(),
                vip.getExternal_ip(),
                vip.getInternal_ip()

        ));

        if (vip.getInterfaz() != null) {
            jsonBuilder.append("""
            ,
            "interface": "%s"
        """.formatted(vip.getInterfaz().getName()));
        }

        if (vip.getComments() != null && !vip.getComments().isBlank()) {
            jsonBuilder.append("""
            ,
            "comments": "%s"
        """.formatted(vip.getComments()));
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
        String factor=usuarioFirewall.getFactor();

        Map<String, Object> result = new HashMap<>();
        String json = """
            {
              "name": "%s",
              "type": "password",
              "passwd": "%s",
              "two-factor": "disable"
            }
            """.formatted(usuarioFirewall.getNombre(), usuarioFirewall.getPassword());
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

    public Map<String, Object> editarAddress(Dispositivo dispositivo, Address address, String nombreOriginal) {
        String url = "http://" + dispositivo.getIp()
                + "/api/v2/cmdb/firewall/address/"
                + nombreOriginal
                + "?vdom=root";

        Map<String, Object> resultado = new HashMap<>();
        String json = AddressJsonBuilder.build(address);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(dispositivo.getToken().trim());

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            if (response.getBody() != null && response.getBody().contains("\"status\":\"success\"")) {
                resultado.put("success", true);
            } else {
                resultado.put("success", false);
                resultado.put("error", response.getBody());
            }

            resultado.put("httpStatus", response.getStatusCode());

        } catch (Exception e) {
            resultado.put("success", false);
            resultado.put("exception", e.getMessage());
        }

        return resultado;
    }
    public Map<String, Object> eliminarAddress(Dispositivo dispositivo, String addressName) {
        String url = "http://" + dispositivo.getIp()
                + "/api/v2/cmdb/firewall/address/"
                + addressName
                + "?vdom=root";

        return eliminarEntidad(dispositivo, url);
    }

    public Map<String, Object> eliminarInterfaz(Dispositivo dispositivo, String interfazName) {
        String url = "http://" + dispositivo.getIp()
                + "/api/v2/cmdb/system/interface/"
                + URLEncoder.encode(interfazName, StandardCharsets.UTF_8)
                + "?vdom=root";

        return eliminarEntidad(dispositivo, url);
    }

    public Map<String, Object> eliminarService(Dispositivo dispositivo, String serviceName) {
        String url = "http://" + dispositivo.getIp()
                + "/api/v2/cmdb/firewall.service/custom/"
                + serviceName
                + "?vdom=root";

        return eliminarEntidad(dispositivo, url);
    }

    public Map<String, Object> eliminarUsuarioFirewall(Dispositivo dispositivo, String username) {
        String url = "http://" + dispositivo.getIp()
                + "/api/v2/cmdb/user/local/"
                + username
                + "?vdom=root";

        return eliminarEntidad(dispositivo, url);
    }

    public Map<String, Object> eliminarReglaFirewall(Dispositivo dispositivo, String policyName) {
        Map<String, Object> result = new HashMap<>();

        try {
            String getUrl = "http://" + dispositivo.getIp() + "/api/v2/cmdb/firewall/policy?vdom=root";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(dispositivo.getToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
            ResponseEntity<Map> response = restTemplate.exchange(getUrl, HttpMethod.GET, requestEntity, Map.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                result.put("success", false);
                result.put("error", "No se pudieron obtener las políticas de FortiGate");
                return result;
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
                result.put("success", false);
                result.put("error", "No se encontró la regla en FortiGate con nombre: " + policyName);
                return result;
            }

            String deleteUrl = "http://" + dispositivo.getIp() + "/api/v2/cmdb/firewall/policy/" + policyId + "?vdom=root";
            ResponseEntity<String> deleteResponse = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, requestEntity, String.class);

            result.put("success", deleteResponse.getStatusCode() == HttpStatus.OK);
            result.put("httpStatus", deleteResponse.getStatusCode());
            result.put("error", deleteResponse.getBody());

        } catch (Exception e) {
            result.put("success", false);
            result.put("exception", e.getMessage());
        }

        return result;
    }

    private Map<String, Object> eliminarEntidad(Dispositivo dispositivo, String url) {
        Map<String, Object> result = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(dispositivo.getToken());
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
            result.put("success", response.getStatusCode() == HttpStatus.OK);
            result.put("httpStatus", response.getStatusCode());
            result.put("error", response.getBody());
        } catch (Exception e) {
            result.put("success", false);
            result.put("exception", e.getMessage());
        }

        return result;
    }

    public Map<String, Object> editarInterfaz(Dispositivo dispositivo, Interfaz interfaz, String nombreOriginal) {

        String url = "http://" + dispositivo.getIp()
                + "/api/v2/cmdb/system/interface/"
                + nombreOriginal
                + "?vdom=root";

        Map<String, Object> result = new HashMap<>();

        String json = """
        {
            "vdom": "root",
            "role": "%s",
            "mode": "dhcp",
            "allowaccess": "%s",
            "description": "%s"
        }
        """.formatted(
                interfaz.getRole(),
                interfaz.getAllowaccess(),
                interfaz.getDescription()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(dispositivo.getToken().trim());

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

            if (response.getBody() != null && response.getBody().contains("\"status\":\"success\"")) {
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

    public Map<String, Object> editUsuario(Dispositivo dispositivo, UsuarioFirewall usuario, String oldName) {

        String url = "http://" + dispositivo.getIp()
                + "/api/v2/cmdb/user/local/"
                + oldName
                + "?vdom=root";

        String json = """
    {
        "type": "password",
        "passwd": "%s",
    }
    """.formatted(
                usuario.getPassword()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(dispositivo.getToken().trim());

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        Map<String, Object> result = new HashMap<>();

        try {

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            if (response.getBody() != null && response.getBody().contains("\"status\":\"success\"")) {
                result.put("success", true);
            } else {
                result.put("success", false);
                result.put("error", response.getBody());
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("exception", e.getMessage());
        }

        return result;
    }


    public Map<String, Object> editarVirtualIp(Dispositivo dispositivo,VirtualIp virtualIp, String nombreOriginal) {
        String url = "http://" + dispositivo.getIp()
                + "/api/v2/cmdb/firewall/service/custom/"
                + nombreOriginal
                + "?vdom=root";

        Map<String, Object> resultado = new HashMap<>();
        String json = """
            {
                "extintf": "any",
                "extip": "%s",
                "mappedip": [
                    {
                        "range": "%s"
                    }
                ]
            }
            """.formatted(
                virtualIp.getExternal_ip(),
                virtualIp.getInternal_ip()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(dispositivo.getToken().trim());

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.PUT, entity, String.class
            );

            if (response.getBody() != null && response.getBody().contains("\"status\":\"success\"")) {
                resultado.put("success", true);
            } else {
                resultado.put("success", false);
                resultado.put("error", response.getBody());
            }

            resultado.put("httpStatus", response.getStatusCode());

        } catch (Exception e) {
            resultado.put("success", false);
            resultado.put("exception", e.getMessage());
        }

        return resultado;
    }

    public Map<String, Object> editarPolicy(Dispositivo dispositivo, ReglaFirewall regla) {
        Map<String, Object> result = new HashMap<>();

        try {
            String getUrl = "http://" + dispositivo.getIp() + "/api/v2/cmdb/firewall/policy?vdom=root";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(dispositivo.getToken().trim());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> getRequest = new HttpEntity<>(null, headers);
            ResponseEntity<Map> getResponse = restTemplate.exchange(getUrl, HttpMethod.GET, getRequest, Map.class);

            if (getResponse.getStatusCode() != HttpStatus.OK || getResponse.getBody() == null) {
                result.put("success", false);
                result.put("error", "No se pudieron obtener las políticas para buscar el ID");
                return result;
            }

            List<Map<String, Object>> policies = (List<Map<String, Object>>) getResponse.getBody().get("results");
            Integer policyId = null;

            for (Map<String, Object> policy : policies) {
                if (regla.getNombre().equals(policy.get("name"))) {
                    policyId = (Integer) policy.get("policyid");
                    break;
                }
            }

            if (policyId == null) {
                result.put("success", false);
                result.put("error", "No se encontró la regla en FortiGate con nombre: " + regla.getNombre());
                return result;
            }

            String putUrl = "http://" + dispositivo.getIp() + "/api/v2/cmdb/firewall/policy/" + policyId + "?vdom=root";

            String json = """
        {
            "srcintf": [{ "name": "%s" }],
            "dstintf": [{ "name": "%s" }],
            "srcaddr": [{ "name": "%s" }],
            "dstaddr": [{ "name": "%s" }],
            "service": [{ "name": "%s" }],
            "schedule": "always",
            "action": "%s",
            "status": "%s",
            "nat": "%s"
        }
        """.formatted(
                    regla.getOrigen(),
                    regla.getDestino(),
                    regla.getIporigen(),
                    regla.getIpdestino(),
                    regla.getServicio(),
                    regla.getAction(),
                    regla.isHabilitada() ? "enable" : "disable",
                    regla.getNat()
            );

            HttpEntity<String> putEntity = new HttpEntity<>(json, headers);
            ResponseEntity<String> putResponse = restTemplate.exchange(putUrl, HttpMethod.PUT, putEntity, String.class);

            boolean success = putResponse.getStatusCode() == HttpStatus.OK &&
                    putResponse.getBody() != null &&
                    putResponse.getBody().contains("\"status\":\"success\"");

            result.put("success", success);
            result.put("httpStatus", putResponse.getStatusCode());
            result.put("error", putResponse.getBody());

        } catch (Exception e) {
            result.put("success", false);
            result.put("exception", e.getMessage());
        }
        return result;
    }

}
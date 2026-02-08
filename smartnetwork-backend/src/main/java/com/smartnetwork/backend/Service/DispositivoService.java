package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.UsuarioRepository;
import com.smartnetwork.backend.domain.Entity.Address;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.ReglaFirewall;
import com.smartnetwork.backend.domain.Entity.Usuario;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DispositivoService {

    private final DispositivoRepository dispositivoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public DispositivoService(
            DispositivoRepository dispositivoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.dispositivoRepository = dispositivoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Dispositivo crearDispositivo(Dispositivo dispositivo, String username) {

        String url = "http://" + dispositivo.getIp() + "/api/v2/monitor/system/status";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + dispositivo.getToken());

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

        } catch (Exception e) {
            throw new RuntimeException("No se puede conectar con el dispositivo");
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        dispositivo.setUsuario(usuario);

        return dispositivoRepository.save(dispositivo);
    }
    public List<Dispositivo> obtenerDispositivosDelUsuario(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return dispositivoRepository.findByUsuario(usuario);
    }
    public Map<String, Object> crearPolicy(Dispositivo dispositivo, ReglaFirewall regla) {

        String url = "http://" + dispositivo.getIp() + "/api/v2/cmdb/firewall/policy?vdom=root";

        Map<String, Object> result = new HashMap<>();

        // Construimos el JSON EXACTAMENTE como Postman
        String json = """
{
    "name": "%s",
    "srcintf": [
        { "name": "%s" }
    ],
    "dstintf": [
        { "name": "%s" }
    ],
    "srcaddr": [
        { "name": "%s" }
    ],
    "dstaddr": [
        { "name": "%s" }
    ],
    "service": [
        { "name": "%s" }
    ],
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
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + dispositivo.getToken().trim());

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            System.out.println("RAW RESPONSE: " + response.getBody());

            // FortiGate devuelve JSON con "status": "success" o "error"
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

    public Map<String, Object> crearAddress(Dispositivo dispositivo, Address address){
        return null;
    }
}

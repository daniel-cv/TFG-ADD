package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.UsuarioFirewallRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.UsuarioFirewall;
import com.smartnetwork.backend.domain.dtos.usuarioFirewall.CreaUsuarioFirewallDTO;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioFirewallService {

    private final UsuarioFirewallRepository usuarioFirewallRepository;
    private final DispositivoRepository dispositivoRepo;
    private final FortiGateService fortiGateService;

    public UsuarioFirewallService(UsuarioFirewallRepository usuarioFirewallRepository, DispositivoRepository dispositivoRepo, FortiGateService fortiGateService){
        this.usuarioFirewallRepository = usuarioFirewallRepository;
        this.dispositivoRepo = dispositivoRepo;
        this.fortiGateService = fortiGateService;
    }

    /**
     * Crear un UsuarioFirewall asociado a un dispositivo
     */
    public UsuarioFirewall create(CreaUsuarioFirewallDTO dto, String username) {

        Dispositivo dispositivo = dispositivoRepo
                .findById(dto.getDispositivoId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        UsuarioFirewall usuarioFirewall = new UsuarioFirewall();
        usuarioFirewall.setDispositivo(dispositivo);
        usuarioFirewall.setNombre(dto.getName());
        usuarioFirewall.setTipo(dto.getType());
        usuarioFirewall.setPassword(dto.getPassword());

        Map<String, Object> resultado =
                fortiGateService.crearUsuarioFirewall(dispositivo, usuarioFirewall);

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException(
                    "Error creando usuario en FortiGate: " + resultado
            );
        }

        usuarioFirewallRepository.save(usuarioFirewall);

        return usuarioFirewall;
    }

    /**
     * Listar todos los UsuarioFirewall de un dispositivo
     */
    public List<UsuarioFirewall> findAllByDispositivo(Long dispositivoId, String username) {
        Dispositivo dispositivo = dispositivoRepo
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return usuarioFirewallRepository.findByDispositivoId(dispositivoId);
    }

    /**
     * Buscar un UsuarioFirewall por ID y comprobar propietario
     */
    public Optional<UsuarioFirewall> findById(String username, Long dispositivoId, Long usuarioFirewallId) {
        Dispositivo dispositivo = dispositivoRepo
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return usuarioFirewallRepository.findByIdAndDispositivoId(usuarioFirewallId, dispositivoId);
    }

    /**
     * Actualizar un UsuarioFirewall
     */
    public UsuarioFirewall update(UsuarioFirewall usuarioFirewall, String username, Long dispositivoId) {
        Dispositivo dispositivo = dispositivoRepo
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        usuarioFirewall.setDispositivo(dispositivo);
        return usuarioFirewallRepository.save(usuarioFirewall);
    }

    /**
     * Eliminar un UsuarioFirewall
     */
    public void eliminar(Long usuarioFirewallId, String username) {

        UsuarioFirewall usuarioFirewall = usuarioFirewallRepository.findById(usuarioFirewallId)
                .orElseThrow(() -> new RuntimeException("UsuarioFirewall no existe"));

        Dispositivo dispositivo = usuarioFirewall.getDispositivo();

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        String url = "http://" + dispositivo.getIp()
                + "/api/v2/cmdb/user/local/"
                +  URLEncoder.encode(usuarioFirewall.getNombre(), StandardCharsets.UTF_8)
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
                usuarioFirewallRepository.delete(usuarioFirewall);
            } else {
                throw new RuntimeException("FortiGate error: " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("Error eliminando usuario en FortiGate", e);
        }
    }
}


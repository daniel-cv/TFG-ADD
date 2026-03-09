package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.UsuarioFirewallRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.UsuarioFirewall;
import com.smartnetwork.backend.domain.dtos.usuarioFirewall.CreaUsuarioFirewallDTO;
import com.smartnetwork.backend.domain.dtos.usuarioFirewall.UsuarioFirewallDTO;
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
        usuarioFirewall.setPassword(dto.getPassword());
        usuarioFirewall.setTipo("User");
        usuarioFirewall.setEmail(dto.getEmail());
        usuarioFirewall.setFactor(dto.getTwoFactor());
        Map<String, Object> resultado = fortiGateService.crearUsuarioFirewall(dispositivo, usuarioFirewall);
        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException("Error creando usuario en FortiGate: " + resultado);
        }
        return usuarioFirewallRepository.save(usuarioFirewall);
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

        if (!usuarioFirewall.getDispositivo().getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        Map<String, Object> resultado = fortiGateService.eliminarUsuarioFirewall(
                usuarioFirewall.getDispositivo(),
                usuarioFirewall.getNombre()
        );

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException("Error eliminando UsuarioFirewall en FortiGate: " + resultado);
        }

        usuarioFirewallRepository.delete(usuarioFirewall);
    }

    public UsuarioFirewallDTO editUsuario(Long usuarioFirewallId, CreaUsuarioFirewallDTO dto, String username) {

        UsuarioFirewall usuario = usuarioFirewallRepository.findById(usuarioFirewallId)
                .orElseThrow(() -> new RuntimeException("UsuarioFirewall no existe"));

        if (!usuario.getDispositivo().getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        Dispositivo dispositivo = usuario.getDispositivo();

        String oldName = usuario.getNombre();

        usuario.setNombre(dto.getName());
        usuario.setPassword(dto.getPassword());
        usuario.setEmail(dto.getEmail());
        usuario.setTipo(dto.getType());
        usuario.setFactor(dto.getTwoFactor());

        Map<String, Object> resultado = fortiGateService.editUsuario(
                dispositivo,
                usuario,
                oldName
        );

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException("Error editando Usuario en FortiGate: " + resultado);
        }

        UsuarioFirewall saved = usuarioFirewallRepository.save(usuario);

        return toDTO(saved);
    }

    private UsuarioFirewallDTO toDTO(UsuarioFirewall usuario) {

        UsuarioFirewallDTO dto = new UsuarioFirewallDTO();

        dto.setNombre(usuario.getNombre());
        dto.setTipo(usuario.getTipo());
        dto.setPassword(usuario.getPassword());
        dto.setEmail(usuario.getEmail());

        if (usuario.getDispositivo() != null) {
            dto.setDispositivoId(usuario.getDispositivo().getId());
        }

        return dto;
    }
}


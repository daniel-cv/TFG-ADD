package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.UsuarioFirewallRepository;
import com.smartnetwork.backend.domain.Entity.firewalls.fortinet.Dispositivo;
import com.smartnetwork.backend.domain.Entity.firewalls.fortinet.UsuarioFirewall;
import com.smartnetwork.backend.domain.dtos.firewalls.usuarioFirewall.CreaUsuarioFirewallDTO;
import org.springframework.stereotype.Service;

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

        // 🔐 Seguridad: comprobar propietario del dispositivo
        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        UsuarioFirewall usuarioFirewall =  new UsuarioFirewall();
        usuarioFirewall.setDispositivo(dispositivo);
        usuarioFirewall.setNombre(dto.getName());
        usuarioFirewall.setTipo(dto.getType());
        usuarioFirewall.setPassword(dto.getPassword());

        usuarioFirewallRepository.save(usuarioFirewall);

        Map<String, Object> resultado =
                fortiGateService.crearUsuarioFirewall(dispositivo, usuarioFirewall);

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException(
                    "Error creando service en FortiGate: " + resultado
            );
        }

        return usuarioFirewall;
    }

    /**
     * Listar todos los UsuarioFirewall de un dispositivo
     */
    public List<UsuarioFirewall> findAllByDispositivo(Long dispositivoId, String username) {
        Dispositivo dispositivo = dispositivoRepo
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        // 🔐 Seguridad: comprobar propietario
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
    public void delete(UsuarioFirewall usuarioFirewall, String username, Long dispositivoId) {
        Dispositivo dispositivo = dispositivoRepo
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        usuarioFirewallRepository.delete(usuarioFirewall);
    }
}


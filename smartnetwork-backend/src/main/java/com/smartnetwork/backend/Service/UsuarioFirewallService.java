package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.UsuarioFirewallRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.UsuarioFirewall;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioFirewallService {

    private final UsuarioFirewallRepository usuarioFirewallRepository;
    private final DispositivoRepository dispositivoRepo;
    private final RestTemplate restTemplate = new RestTemplate();

    public UsuarioFirewallService(UsuarioFirewallRepository usuarioFirewallRepository, DispositivoRepository dispositivoRepo){
        this.usuarioFirewallRepository = usuarioFirewallRepository;
        this.dispositivoRepo = dispositivoRepo;
    }

    /**
     * Crear un UsuarioFirewall asociado a un dispositivo
     */
    public UsuarioFirewall create(UsuarioFirewall usuarioFirewall, String username) {
        Dispositivo dispositivo = dispositivoRepo
                .findById(usuarioFirewall.getDispositivo().getId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        // 🔐 Seguridad: comprobar propietario del dispositivo
        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        usuarioFirewall.setDispositivo(dispositivo);
        return usuarioFirewallRepository.save(usuarioFirewall);
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


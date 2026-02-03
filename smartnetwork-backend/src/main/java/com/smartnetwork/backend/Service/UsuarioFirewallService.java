package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.UsuarioFirewallRepository;
import com.smartnetwork.backend.domain.Entity.UsuarioFirewall;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Optional;

public class UsuarioFirewallService {

    private final UsuarioFirewallRepository usuarioFirewallRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public UsuarioFirewallService(UsuarioFirewallRepository usuarioFirewallRepository){
        this.usuarioFirewallRepository = usuarioFirewallRepository;
    }

    public List<UsuarioFirewall> findAll() {
        return usuarioFirewallRepository.findAll();
    }

    public Optional<UsuarioFirewall> findBYId(Long id){
        return usuarioFirewallRepository.findById(id);
    }

    public UsuarioFirewall save(UsuarioFirewall usuarioFirewall){
        return usuarioFirewallRepository.save(usuarioFirewall);
    }

    public UsuarioFirewall update(UsuarioFirewall usuarioFirewall){
        return usuarioFirewallRepository.save(usuarioFirewall);
    }

    public void delete(UsuarioFirewall usuarioFirewall){
        usuarioFirewallRepository.delete(usuarioFirewall);
    }
}

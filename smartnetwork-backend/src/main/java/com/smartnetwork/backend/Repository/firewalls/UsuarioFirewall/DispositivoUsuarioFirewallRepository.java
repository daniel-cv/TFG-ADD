package com.smartnetwork.backend.Repository.firewalls.UsuarioFirewall;

import com.smartnetwork.backend.domain.Entity.firewalls.UsuarioFirewall.DispositivoUsuarioFirewall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DispositivoUsuarioFirewallRepository extends JpaRepository<DispositivoUsuarioFirewall, Long> {
    boolean existsByDispositivoIdAndUsuarioFirewallId(Long dispositivoId, Long usuarioFirewallId);
    List<DispositivoUsuarioFirewall> findByUsuarioFirewallId(Long usuarioFirewallId);
    List<DispositivoUsuarioFirewall> findByDispositivoId(Long dispositivoId);
    boolean existsByUsuarioFirewallId(Long usuarioFirewallId);
}

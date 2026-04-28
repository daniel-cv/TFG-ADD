package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.DispositivoUsuarioFirewall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DispositivoUsuarioFirewallRepository extends JpaRepository<DispositivoUsuarioFirewall, Long> {
    boolean existsByDispositivoIdAndUsuarioFirewallId(Long dispositivoId, Long usuarioFirewallId);
    List<DispositivoUsuarioFirewall> findByUsuarioFirewallId(Long usuarioFirewallId);
    List<DispositivoUsuarioFirewall> findByDispositivoId(Long dispositivoId);
}

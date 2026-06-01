package com.smartnetwork.backend.Repository.firewalls.UsuarioFirewall;

import com.smartnetwork.backend.domain.Entity.firewalls.UsuarioFirewall.UsuarioFirewall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioFirewallRepository extends JpaRepository<UsuarioFirewall, Long> {
    List<UsuarioFirewall> findByUsuarioId(Long usuarioId);
}

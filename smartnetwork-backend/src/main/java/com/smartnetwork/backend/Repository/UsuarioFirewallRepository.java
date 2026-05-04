package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.UsuarioFirewall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioFirewallRepository extends JpaRepository<UsuarioFirewall, Long> {
    List<UsuarioFirewall> findByUsuarioId(Long usuarioId);
}

package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.UsuarioFirewall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioFirewallRepository extends JpaRepository<UsuarioFirewall, Long> {
}

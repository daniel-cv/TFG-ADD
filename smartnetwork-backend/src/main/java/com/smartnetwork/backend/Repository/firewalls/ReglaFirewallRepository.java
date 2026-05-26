package com.smartnetwork.backend.Repository.firewalls;

import com.smartnetwork.backend.domain.Entity.Address;
import com.smartnetwork.backend.domain.Entity.ReglaFirewall;
import com.smartnetwork.backend.domain.Entity.firewalls.ReglaFirewall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReglaFirewallRepository
        extends JpaRepository<ReglaFirewall, Long> {

    List<ReglaFirewall> findByUsuarioId(Long id);
}

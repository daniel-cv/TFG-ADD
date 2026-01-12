package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.ReglaFirewall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReglaFirewallRepository
        extends JpaRepository<ReglaFirewall, Long> {

    List<ReglaFirewall> findByDispositivoId(Long dispositivoId);
}

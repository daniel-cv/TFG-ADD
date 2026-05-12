package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.DispositivoReglaFirewall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DispositivoReglaFirewallRepository extends JpaRepository<DispositivoReglaFirewall, Long> {
    boolean existsByDispositivoIdAndReglaFirewallId (Long dispositivoId, Long reglaFirewallId);
    List<DispositivoReglaFirewall> findByDispositivoId (Long dispositivoId);
    List<DispositivoReglaFirewall>  findByReglaFirewallId (Long reglaFirewallId);
}

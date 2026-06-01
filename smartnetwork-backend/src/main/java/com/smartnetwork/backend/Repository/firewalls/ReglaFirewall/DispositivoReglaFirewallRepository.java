package com.smartnetwork.backend.Repository.firewalls.ReglaFirewall;

import com.smartnetwork.backend.domain.Entity.firewalls.ReglaFirewall.DispositivoReglaFirewall;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispositivoReglaFirewallRepository
        extends JpaRepository<
        DispositivoReglaFirewall,
        Long
        > {

    List<DispositivoReglaFirewall>
    findByDispositivoId(
            Long dispositivoId
    );

    List<DispositivoReglaFirewall>
    findByReglaFirewallId(
            Long reglaFirewallId
    );

    List<DispositivoReglaFirewall>
    findAllByDispositivoId(
            Long dispositivoId
    );

    List<DispositivoReglaFirewall>
    findAllByReglaFirewallId(
            Long reglaFirewallId
    );

    boolean existsByDispositivoIdAndReglaFirewallId(
            Long dispositivoId,
            Long reglaFirewallId
    );

    boolean existsByReglaFirewallId(
            Long reglaFirewallId
    );

    void deleteByDispositivoIdAndReglaFirewallId(
            Long dispositivoId,
            Long reglaFirewallId
    );
}
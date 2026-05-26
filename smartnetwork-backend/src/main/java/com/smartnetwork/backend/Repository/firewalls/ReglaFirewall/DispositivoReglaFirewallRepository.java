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

    /* ===================================================== */
    /* FIND */
    /* ===================================================== */

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

    /* ===================================================== */
    /* EXISTS */
    /* ===================================================== */

    boolean existsByDispositivoIdAndReglaFirewallId(
            Long dispositivoId,
            Long reglaFirewallId
    );

    boolean existsByReglaFirewallId(
            Long reglaFirewallId
    );

    /* ===================================================== */
    /* DELETE */
    /* ===================================================== */

    void deleteByDispositivoIdAndReglaFirewallId(
            Long dispositivoId,
            Long reglaFirewallId
    );
}
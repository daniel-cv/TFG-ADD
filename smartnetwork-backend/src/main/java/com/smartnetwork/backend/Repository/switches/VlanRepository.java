package com.smartnetwork.backend.Repository.switches;

import com.smartnetwork.backend.domain.Entity.switches.Interfaces;
import com.smartnetwork.backend.domain.Entity.switches.Vlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VlanRepository extends JpaRepository<Vlan, Long> {

    List<Vlan> findByDispositivoId(Long dispositivoId);
    Optional<Vlan> findByVlanId(Integer vlanId);
    Optional<Vlan> findByVlanIdAndDispositivoId(Integer vlanId, Long dispositivoId);

}
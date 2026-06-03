package com.smartnetwork.backend.Repository.switches;

import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.switches.Interfaces;
import com.smartnetwork.backend.domain.Entity.switches.Vlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterfacesRepository extends JpaRepository<Interfaces, Long> {

    List<Interfaces> findByDispositivoId(Long dispositivoId);
    Optional<Interfaces> findByNameAndDispositivo(String name, Dispositivo dispositivo);
    List<Interfaces> findByVlanAccess(Vlan vlan);
    void deleteAllByDispositivo(Dispositivo dispositivo);
}
package com.smartnetwork.backend.Repository.switches;

import com.smartnetwork.backend.domain.Entity.switches.Acl;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AclRepository extends JpaRepository<Acl, Long> {

    // 🔹 Buscar ACLs por dispositivo
    List<Acl> findByDispositivoId(Long dispositivoId);

    // 🔹 Evitar duplicados
    Optional<Acl> findByNombreAndDispositivo(String nombre, Dispositivo dispositivo);
}
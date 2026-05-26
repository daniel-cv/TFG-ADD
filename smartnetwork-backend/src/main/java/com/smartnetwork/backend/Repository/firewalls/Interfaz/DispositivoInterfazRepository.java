package com.smartnetwork.backend.Repository.firewalls.Interfaz;

import com.smartnetwork.backend.domain.Entity.firewalls.Interfaz.DispositivoInterfaz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DispositivoInterfazRepository extends JpaRepository<DispositivoInterfaz, Long> {
    boolean existsByIdDispositivoIdAndIdInterfazId(Long dispositivoId, Long interfazId);

    List<DispositivoInterfaz> findByIdDispositivoId(Long dispositivoId);

    List<DispositivoInterfaz> findByIdInterfazId(Long interfazId);

    boolean existsByIdInterfazId(Long interfazId);
}

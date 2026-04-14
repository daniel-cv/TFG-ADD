package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.DispositivoAddress;
import com.smartnetwork.backend.domain.Entity.DispositivoInterfaz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DispositivoInterfazRepository extends JpaRepository<DispositivoInterfaz, Long> {
    boolean existsByIdDispositivoIdAndIdInterfazId(Long dispositivoId, Long interfazId);

    List<DispositivoInterfaz> findByIdDispositivoId(Long dispositivoId);

    List<DispositivoInterfaz> findByIdInterfazId(Long interfazId);
}

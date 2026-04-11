package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.DispositivoService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface DispositivoServiceRepository extends JpaRepository<DispositivoService,Long> {
    List<DispositivoService> findByDispositivoId(Long dispositivoId);

    List<DispositivoService> findByServiceId(Long serviceId);
    boolean existsByDispositivoIdAndServiceId(Long dispositivoId, Long serviceId);
}

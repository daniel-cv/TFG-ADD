package com.smartnetwork.backend.Repository.firewalls.Service;

import com.smartnetwork.backend.domain.Entity.firewalls.Service.DispositivoService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DispositivoServiceRepository extends JpaRepository<DispositivoService,Long> {
    List<DispositivoService> findByDispositivoId(Long dispositivoId);

    List<DispositivoService> findByServiceId(Long serviceId);
    boolean existsByDispositivoIdAndServiceId(Long dispositivoId, Long serviceId);
    boolean existsByServiceId(Long serviceId);
}

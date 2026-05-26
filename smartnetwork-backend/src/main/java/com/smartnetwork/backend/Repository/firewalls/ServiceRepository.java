package com.smartnetwork.backend.Repository.firewalls;

import com.smartnetwork.backend.domain.Entity.firewalls.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service,Long> {
    List<Service> findByDispositivoId(Long dispositivoId);
    Optional<Service> findByIdAndDispositivoId(Long id, Long dispositivoId);
}

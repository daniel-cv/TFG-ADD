package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.VirtualIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VirtualIpRepository extends JpaRepository<VirtualIp,Long> {
    List<VirtualIp> findByDispositivoId(Long dispositivoId);

    Optional<VirtualIp> findByIdAndDispositivoId(Long id, Long dispositivoId);

    Optional<VirtualIp> findByIdAndDispositivoUsuarioUsername(
            Long id,
            String username
    );
}

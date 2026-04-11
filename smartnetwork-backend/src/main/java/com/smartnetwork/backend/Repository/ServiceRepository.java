package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.Address;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service,Long> {
    List<Service> findByDispositivoServices_Dispositivo_Id(Long dispositivoId);
    Optional<Service> findByIdAndDispositivoServices_Dispositivo_Id(Long id, Long dispositivoId);
    List<Service> findByUsuarioId(Integer usuarioId);
}

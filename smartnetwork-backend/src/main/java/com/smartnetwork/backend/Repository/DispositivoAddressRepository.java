package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.DispositivoAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DispositivoAddressRepository extends JpaRepository<DispositivoAddress,Long> {
    List<DispositivoAddress> findByDispositivoId(Long dispositivoId);
    List<DispositivoAddress> findAllByAddressId(Long addressId);
    Optional<DispositivoAddress> findByAddressId(Long addressId);
    boolean existsByDispositivoIdAndAddressId(Long dispositivoId, Long addressId);
    boolean existsByAddressId (Long addressId);
}

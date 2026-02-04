package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {
    List<Address> findByDispositivoId(Long dispositivoId);
    Optional<Address> findByIdAndDispositivoId(Long id, Long dispositivoId);
}

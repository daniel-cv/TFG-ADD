package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.firewalls.fortinet.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {
    List<Address> findByDispositivoId(Long dispositivoId);
}

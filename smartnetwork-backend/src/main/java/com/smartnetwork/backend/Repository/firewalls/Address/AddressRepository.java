package com.smartnetwork.backend.Repository.firewalls.Address;

import com.smartnetwork.backend.domain.Entity.firewalls.Address.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {
    List<Address> findByUsuarioId(Long id);

}

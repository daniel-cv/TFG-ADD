package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.VirtualIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VirtualIpRepository extends JpaRepository<VirtualIp,Long> {
}

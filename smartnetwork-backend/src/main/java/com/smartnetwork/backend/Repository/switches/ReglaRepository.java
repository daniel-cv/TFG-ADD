package com.smartnetwork.backend.Repository.switches;

import com.smartnetwork.backend.domain.Entity.switches.AclRegla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReglaRepository extends JpaRepository<AclRegla, Long> {

    List<AclRegla> findByAclIdOrderByOrdenAsc(Long aclId);

    List<AclRegla> findByAclId(Long aclId);
}
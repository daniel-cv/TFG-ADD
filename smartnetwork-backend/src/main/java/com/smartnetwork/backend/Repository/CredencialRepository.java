package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.Entity.Credencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredencialRepository extends JpaRepository<Credencial,Long> {
}

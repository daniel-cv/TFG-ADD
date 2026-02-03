package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.Interfaz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterfazRepository extends JpaRepository<Interfaz, Long> {
}

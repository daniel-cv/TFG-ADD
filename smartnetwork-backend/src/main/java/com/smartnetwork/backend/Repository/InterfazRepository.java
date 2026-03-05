package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.Interfaz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterfazRepository extends JpaRepository<Interfaz, Long> {

    List<Interfaz> findByDispositivoId(Long dispositivoId);

    Optional<Interfaz> findByIdAndDispositivoId(Long id, Long dispositivoId);
}

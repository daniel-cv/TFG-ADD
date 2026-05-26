package com.smartnetwork.backend.Repository.firewalls;

import com.smartnetwork.backend.domain.Entity.Address;
import com.smartnetwork.backend.domain.Entity.Interfaz;
import com.smartnetwork.backend.domain.Entity.firewalls.Interfaz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterfazRepository extends JpaRepository<Interfaz, Long> {

    List<Interfaz> findByUsuarioId(Long usuarioId);

    Optional<Interfaz> findByName(String name);

}
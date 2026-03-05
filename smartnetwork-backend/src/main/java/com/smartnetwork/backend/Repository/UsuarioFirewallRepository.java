package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.UsuarioFirewall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioFirewallRepository extends JpaRepository<UsuarioFirewall, Long> {
    // Listar todos los UsuarioFirewall de un dispositivo
    List<UsuarioFirewall> findByDispositivoId(Long dispositivoId);

    // Buscar un UsuarioFirewall por id y dispositivo
    Optional<UsuarioFirewall> findByIdAndDispositivoId(Long id, Long dispositivoId);
}

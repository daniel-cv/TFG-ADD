package com.smartnetwork.backend.Repository.switches;

import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.switches.IpRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IpRouteRepository extends JpaRepository<IpRoute, Long> {

    List<IpRoute> findByDispositivoId(Long dispositivoId);
    Optional<IpRoute> findByIpDestinoAndMascaraAndGatewayAndDispositivo(
            String ipDestino,
            String mascara,
            String gateway,
            Dispositivo dispositivo
    );

}
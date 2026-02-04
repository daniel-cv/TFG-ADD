package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.VirtualIpRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.VirtualIp;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VirtualIpService {

    private final VirtualIpRepository virtualIpRepository;
    private final DispositivoRepository dispositivoRepo;

    public VirtualIpService(
            VirtualIpRepository virtualIpRepository,
            DispositivoRepository dispositivoRepo
    ) {
        this.virtualIpRepository = virtualIpRepository;
        this.dispositivoRepo = dispositivoRepo;
    }

    public VirtualIp create(VirtualIp virtualIp, String username) {

        Dispositivo dispositivo = dispositivoRepo
                .findById(virtualIp.getDispositivo().getId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        virtualIp.setDispositivo(dispositivo);
        return virtualIpRepository.save(virtualIp);
    }

    public List<VirtualIp> findAllByDispositivo(Long dispositivoId, String username) {

        Dispositivo dispositivo = dispositivoRepo
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return virtualIpRepository.findByDispositivoId(dispositivoId);
    }

    public Optional<VirtualIp> findById(
            Long virtualIpId,
            Long dispositivoId,
            String username
    ) {

        Dispositivo dispositivo = dispositivoRepo
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return virtualIpRepository.findByIdAndDispositivoId(
                virtualIpId,
                dispositivoId
        );
    }

    public VirtualIp update(VirtualIp virtualIp, String username, Long dispositivoId) {

        findById(virtualIp.getId(), dispositivoId, username)
                .orElseThrow(() -> new RuntimeException("VirtualIp no existe"));

        return virtualIpRepository.save(virtualIp);
    }

    public void delete(Long virtualIpId, Long dispositivoId, String username) {

        VirtualIp virtualIp = findById(virtualIpId, dispositivoId, username)
                .orElseThrow(() -> new RuntimeException("VirtualIp no existe"));

        virtualIpRepository.delete(virtualIp);
    }
}

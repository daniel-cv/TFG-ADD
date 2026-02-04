package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.InterfazRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.Interfaz;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InterfazService {
    private final InterfazRepository interfazRepository;
    private final DispositivoRepository dispositivoRepo;

    public InterfazService(
            InterfazRepository interfazRepository,
            DispositivoRepository dispositivoRepo
    ) {
        this.interfazRepository = interfazRepository;
        this.dispositivoRepo = dispositivoRepo;
    }

    public Interfaz create(Interfaz interfaz, String username) {

        Dispositivo dispositivo = dispositivoRepo
                .findById(interfaz.getDispositivo().getId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        // 🔐 Seguridad: comprobar propietario
        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        interfaz.setDispositivo(dispositivo);
        return interfazRepository.save(interfaz);
    }

    public List<Interfaz> findAllByDispositivo(Long dispositivoId, String username) {

        Dispositivo dispositivo = dispositivoRepo
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return interfazRepository.findByDispositivoId(dispositivoId);
    }

    public Optional<Interfaz> findById(
            Long interfazId,
            Long dispositivoId,
            String username
    ) {

        Dispositivo dispositivo = dispositivoRepo
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return interfazRepository.findByIdAndDispositivoId(
                interfazId,
                dispositivoId
        );
    }

    public Interfaz update(Interfaz interfaz, String username, Long dispositivoId) {

        findById(interfaz.getId(), dispositivoId, username)
                .orElseThrow(() -> new RuntimeException("Interfaz no existe"));

        return interfazRepository.save(interfaz);
    }

    public void delete(Long interfazId, Long dispositivoId, String username) {

        Interfaz interfaz = findById(interfazId, dispositivoId, username)
                .orElseThrow(() -> new RuntimeException("Interfaz no existe"));

        interfazRepository.delete(interfaz);
    }
}

package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Controller.DispositivoController;
import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.ReglaFirewallRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.ReglaFirewall;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReglaFirewallService {

    private final ReglaFirewallRepository reglaRepo;
    private final DispositivoRepository dispositivoRepo;
    private final DispositivoService dispositivoService;

    public ReglaFirewallService(ReglaFirewallRepository reglaRepo,
                                DispositivoRepository dispositivoRepo,DispositivoService dispositivoService) {
        this.reglaRepo = reglaRepo;
        this.dispositivoRepo = dispositivoRepo;
        this.dispositivoService = dispositivoService;
    }

    public ReglaFirewall crearRegla(ReglaFirewall regla, String username) {

        Dispositivo dispositivo = dispositivoRepo
                .findById(regla.getDispositivo().getId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        // 🔐 Seguridad básica: el dispositivo es del usuario
        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        regla.setDispositivo(dispositivo);
        regla.setHabilitada(true);
        try {
            dispositivoService.crearPolicy(dispositivo,regla);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return reglaRepo.save(regla);
    }

    public List<ReglaFirewall> obtenerPorDispositivo(Long dispositivoId, String username) {

        Dispositivo dispositivo = dispositivoRepo
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return reglaRepo.findByDispositivoId(dispositivoId);
    }
}

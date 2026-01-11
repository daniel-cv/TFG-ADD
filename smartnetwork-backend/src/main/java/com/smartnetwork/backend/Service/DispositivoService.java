package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.UsuarioRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DispositivoService {
    private final DispositivoRepository dispositivoRepository;
    private final UsuarioRepository usuarioRepository;

    public DispositivoService(
            DispositivoRepository dispositivoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.dispositivoRepository = dispositivoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Dispositivo> obtenerDispositivosDelUsuario(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return dispositivoRepository.findByUsuario(usuario);
    }

    public Dispositivo crearDispositivo(Dispositivo dispositivo,String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        dispositivo.setUsuario(usuario);
        return dispositivoRepository.save(dispositivo);
    }
}

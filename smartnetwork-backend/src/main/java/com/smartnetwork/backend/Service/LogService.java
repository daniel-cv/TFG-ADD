package com.smartnetwork.backend.Service;
import com.smartnetwork.backend.Repository.UsuarioRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.Log;
import com.smartnetwork.backend.domain.Entity.Usuario;
import com.smartnetwork.backend.domain.Enum.TipoAccion;
import com.smartnetwork.backend.Repository.LogRepository;
import com.smartnetwork.backend.domain.dtos.firewalls.address.AddressDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final UsuarioRepository usuarioRepository;

    public Log crearLog(
            Usuario usuario,
            Dispositivo dispositivo,
            TipoAccion accion,
            String mensaje
    ) {

        Log log = new Log();

        log.setFechaHora(LocalDateTime.now());
        log.setUsuario(usuario);
        log.setDispositivo(dispositivo);
        log.setAccion(accion);
        log.setMensaje(mensaje);

        return log;
    }

    public void guardarTodos(List<Log> logs) {
        logRepository.saveAll(logs);
    }
    public List<Log> listarPorDispositivo(Long dispositivoId, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if(!usuario.getUsername().equals(username)){
            throw new RuntimeException("No autorizado");
        }
        return logRepository.findByDispositivoId(dispositivoId);
    }

}
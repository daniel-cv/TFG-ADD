package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.UsuarioRepository;
import com.smartnetwork.backend.domain.Entity.Address;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.ReglaFirewall;
import com.smartnetwork.backend.domain.Entity.Usuario;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DispositivoService {

    private final DispositivoRepository dispositivoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public DispositivoService(
            DispositivoRepository dispositivoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.dispositivoRepository = dispositivoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Dispositivo crearDispositivo(Dispositivo dispositivo, String username) {

//        String url = "http://" + dispositivo.getIp() + "/api/v2/monitor/system/status";
//
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Bearer " + dispositivo.getToken());
//
//            HttpEntity<Void> entity = new HttpEntity<>(headers);
//
//            restTemplate.exchange(
//                    url,
//                    HttpMethod.GET,
//                    entity,
//                    String.class
//            );
//
//        } catch (Exception e) {
//            throw new RuntimeException("No se puede conectar con el dispositivo");
//        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        dispositivo.setUsuario(usuario);

        return dispositivoRepository.save(dispositivo);
    }
    public List<Dispositivo> obtenerDispositivosDelUsuario(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return dispositivoRepository.findByUsuario(usuario);
    }

    public Optional<Dispositivo> getDispositivo(Long id, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return dispositivoRepository.findById(id);
    }
}

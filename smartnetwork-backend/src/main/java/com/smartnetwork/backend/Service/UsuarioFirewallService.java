package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.DispositivoUsuarioFirewallRepository;
import com.smartnetwork.backend.Repository.UsuarioFirewallRepository;
import com.smartnetwork.backend.Repository.UsuarioRepository;
import com.smartnetwork.backend.domain.Entity.*;
import com.smartnetwork.backend.domain.dtos.usuarioFirewall.CreaUsuarioFirewallDTO;
import com.smartnetwork.backend.domain.dtos.usuarioFirewall.UsuarioFirewallDTO;
import jakarta.transaction.Transactional;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioFirewallService {

    private final UsuarioFirewallRepository usuarioFirewallRepository;
    private final DispositivoRepository dispositivoRepo;
    private final FortiGateService fortiGateService;
    private final UsuarioRepository usuarioRepository;
    private final DispositivoUsuarioFirewallRepository dispositivoUsuarioFirewallRepository;

    public UsuarioFirewallService(UsuarioFirewallRepository usuarioFirewallRepository, DispositivoRepository dispositivoRepo, FortiGateService fortiGateService, UsuarioRepository usuarioRepository, DispositivoUsuarioFirewallRepository dispositivoUsuarioFirewallRepository){
        this.usuarioFirewallRepository = usuarioFirewallRepository;
        this.dispositivoRepo = dispositivoRepo;
        this.fortiGateService = fortiGateService;
        this.usuarioRepository = usuarioRepository;
        this.dispositivoUsuarioFirewallRepository = dispositivoUsuarioFirewallRepository;
    }

    /**
     * Crear un UsuarioFirewall asociado a un dispositivo
     */
    @Transactional
    public UsuarioFirewallDTO crear(CreaUsuarioFirewallDTO creaUsuarioFirewallDTO, String username){
        UsuarioFirewallDTO usuarioFirewall = crearUsuarioInterfaz(creaUsuarioFirewallDTO, username);

        if(creaUsuarioFirewallDTO.getDispositivosId() != null && !creaUsuarioFirewallDTO.getDispositivosId().isEmpty()){
            asignarUsuarioFirewallADispositivo(usuarioFirewall.getId(), creaUsuarioFirewallDTO.getDispositivosId(), username);
        }

        return usuarioFirewall;
    }

    @Transactional
    public UsuarioFirewallDTO crearUsuarioInterfaz(CreaUsuarioFirewallDTO dto, String username){
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UsuarioFirewall usuarioFirewall = new UsuarioFirewall();
        usuarioFirewall.setNombre(dto.getName());
        usuarioFirewall.setEmail(dto.getEmail());
        usuarioFirewall.setPassword(dto.getPassword());
        usuarioFirewall.setFactor(dto.getTwoFactor());
        usuarioFirewall.setTipo(dto.getType());
        usuarioFirewall.setUsuario(usuario);

        UsuarioFirewall usuarioSaved = usuarioFirewallRepository.save(usuarioFirewall);
        return toUsuarioDTO(usuarioSaved);
    }

    @Transactional
    public void asignarUsuarioFirewallADispositivo(Long usuarioFirewallId, List<Long> dispositivosId, String username){
        UsuarioFirewall usuarioFirewall = usuarioFirewallRepository.findById(usuarioFirewallId)
                .orElseThrow(() -> new RuntimeException("Usuario no existe"));

        if(!usuarioFirewall.getUsuario().getUsername().equals(username)){
            throw new RuntimeException("No autorizado");
        }

        for (Long dispositivoId : dispositivosId){
            Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
                    .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

            if (!dispositivo.getUsuario().getUsername().equals(username)) {
                throw new RuntimeException("No autorizado");
            }

            boolean yaExiste = dispositivoUsuarioFirewallRepository
                    .existsByDispositivoIdAndUsuarioFirewallId(dispositivoId, usuarioFirewallId);

            if (yaExiste) continue;

            //Map<String, Object> resultado = fortiGateService.crearUsuarioFirewall(dispositivo, usuarioFirewall);
            //if (!(Boolean) resultado.get("success")) {
            //    throw new RuntimeException("Error creando usuario en FortiGate: " + resultado);
            //}

            DispositivoUsuarioFirewall rel = new DispositivoUsuarioFirewall();
            rel.setDispositivo(dispositivo);
            rel.setUsuarioFirewall(usuarioFirewall);
            dispositivoUsuarioFirewallRepository.save(rel);
        }
    }

    public List<UsuarioFirewallDTO> listarPorDispositivo(Long dispositivoId, String username){
        Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return dispositivoUsuarioFirewallRepository.findByDispositivoId(dispositivoId)
                .stream()
                .map(rel -> toDTO(rel.getUsuarioFirewall(), dispositivoId))
                .toList();
    }

    public List<UsuarioFirewallDTO> listarPorUsuario(String username){
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(!usuario.getUsername().equals(username)){
            throw new RuntimeException("No autorizado");
        }

        return usuarioFirewallRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::toUsuarioDTO)
                .toList();
    }

    /**
     * Actualizar un UsuarioFirewall
     */
    public UsuarioFirewallDTO actualizar(Long usuarioFirewallId, CreaUsuarioFirewallDTO dto, String username){
        List<DispositivoUsuarioFirewall> rel = dispositivoUsuarioFirewallRepository
                .findByDispositivoId(usuarioFirewallId);

        if (!rel.isEmpty()) {
            throw new RuntimeException("Relación no encontrada");
        }

        UsuarioFirewall usuarioFirewall = rel.get(0).getUsuarioFirewall();

        for (DispositivoUsuarioFirewall dispositivo : rel) {
            if(!dispositivo.getDispositivo().getUsuario().getUsername().equals(username)) {
                throw new RuntimeException("No autorizado");
            }
        }

        String oldName = usuarioFirewall.getNombre();

        usuarioFirewall.setTipo(dto.getType());
        usuarioFirewall.setFactor(dto.getTwoFactor());
        usuarioFirewall.setNombre(dto.getName());
        usuarioFirewall.setEmail(dto.getEmail());
        usuarioFirewall.setPassword(dto.getPassword());

        Dispositivo dispositivoRef = rel.get(0).getDispositivo();

        for (DispositivoUsuarioFirewall dispositivo : rel) {
            Dispositivo dispositivo1 = dispositivo.getDispositivo();

            Map<String, Object> resultado = fortiGateService.editUsuario(
                    dispositivo1,
                    usuarioFirewall,
                    oldName
            );

            if (!(Boolean) resultado.get("success")) {
                throw new RuntimeException("Error eliminando UsuarioFirewall en FortiGate: " + resultado);
            }
        }

        UsuarioFirewall saved = usuarioFirewallRepository.save(usuarioFirewall);
        return toDTO(saved, dispositivoRef.getId());
    }

    /**
     * Eliminar un UsuarioFirewall
     */
    public void eliminarUsuarioFirewall(Long usuarioFirewallId, String username) {
        UsuarioFirewall usuarioFirewall = usuarioFirewallRepository.findById(usuarioFirewallId)
                .orElseThrow(() -> new RuntimeException("Usuario no existe"));

        List<DispositivoUsuarioFirewall> rel = dispositivoUsuarioFirewallRepository
                .findByUsuarioFirewallId(usuarioFirewallId);

        for (DispositivoUsuarioFirewall dispositivo : rel) {
            Dispositivo dispositivo1 = dispositivo.getDispositivo();

            if (dispositivo1.getUsuario().getUsername().equals(username)) {
                throw new RuntimeException("No autorizado");
            }

            Map<String, Object> resultado = fortiGateService.eliminarUsuarioFirewall(
                    dispositivo1,
                    usuarioFirewall.getNombre()
            );

            if (!(Boolean) resultado.get("success")) {
                throw new RuntimeException("Error eliminando UsuarioFirewall en FortiGate: " + resultado);
            }

            dispositivoUsuarioFirewallRepository.delete(dispositivo);
        }

        usuarioFirewallRepository.delete(usuarioFirewall);
    }

    private UsuarioFirewallDTO toDTO(UsuarioFirewall usuario, Long dispositivoId) {

        UsuarioFirewallDTO dto = new UsuarioFirewallDTO();

        dto.setNombre(usuario.getNombre());
        dto.setTipo(usuario.getTipo());
        dto.setPassword(usuario.getPassword());
        dto.setEmail(usuario.getEmail());
        dto.setDispositivoId(dispositivoId);

        return dto;
    }

    private UsuarioFirewallDTO toUsuarioDTO(UsuarioFirewall usuario) {
        UsuarioFirewallDTO dto = new UsuarioFirewallDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setTipo(usuario.getTipo());
        dto.setPassword(usuario.getPassword());
        dto.setEmail(usuario.getEmail());

        return dto;
    }
}


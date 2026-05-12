package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoReglaFirewallRepository;
import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.ReglaFirewallRepository;
import com.smartnetwork.backend.Repository.UsuarioRepository;
import com.smartnetwork.backend.domain.Entity.*;
import com.smartnetwork.backend.domain.dtos.Policys.CrearReglaFirewallDTO;
import com.smartnetwork.backend.domain.dtos.Policys.ReglaFirewallDTO;
import jakarta.transaction.Transactional;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class ReglaFirewallService {

    private final ReglaFirewallRepository reglaRepo;
    private final DispositivoRepository dispositivoRepo;
    private final FortiGateService fortiGateService;
    private final UsuarioRepository usuarioRepository;
    private final DispositivoReglaFirewallRepository  dispositivoReglaFirewallRepository;

    public ReglaFirewallService(ReglaFirewallRepository reglaRepo,
                                DispositivoRepository dispositivoRepo,
                                FortiGateService fortiGateService, UsuarioRepository usuarioRepository, DispositivoReglaFirewallRepository dispositivoReglaFirewallRepository) {
        this.reglaRepo = reglaRepo;
        this.dispositivoRepo = dispositivoRepo;
        this.fortiGateService = fortiGateService;
        this.usuarioRepository = usuarioRepository;
        this.dispositivoReglaFirewallRepository = dispositivoReglaFirewallRepository;
    }

    @Transactional
    public ReglaFirewallDTO crear(CrearReglaFirewallDTO dto, String username){
        ReglaFirewallDTO reglaFirewall = crearReglaFirewall(dto, username);

        if (dto.getDispositivosId() != null && !dto.getDispositivosId().isEmpty()) {
            asignarReglaFirewallADispositivos(reglaFirewall.getId(), dto.getDispositivosId(), username);
        }

        return reglaFirewall;
    }

    @Transactional
    public ReglaFirewallDTO crearReglaFirewall(CrearReglaFirewallDTO dto, String username){
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ReglaFirewall regla = new ReglaFirewall();
        regla.setNombre(dto.getNombre());
        regla.setOrigen(dto.getOrigen());
        regla.setDestino(dto.getDestino());
        regla.setIporigen(dto.getIpOrigen());
        regla.setIpdestino(dto.getIpDestino());
        regla.setServicio(dto.getServicio());
        regla.setAction(dto.getAction());
        regla.setNat(dto.getNat());
        regla.setHabilitada(true);
        regla.setUsuario(usuario);

        ReglaFirewall reglaFirewall = reglaRepo.save(regla);
        return toReglaFirewallDTO(reglaFirewall);
    }

    @Transactional
    public void asignarReglaFirewallADispositivos(Long reglaFirewallId, List<Long> dispositivosId, String username) {
        ReglaFirewall reglaFirewall = reglaRepo.findById(reglaFirewallId)
                .orElseThrow(() -> new RuntimeException("ReglaFirewall no encontrada"));

        if (!reglaFirewall.getUsuario().getUsername().equals(username)){
            throw new RuntimeException("Usuario no encontrado");
        }

        for (Long dispositivoId : dispositivosId) {
            Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
                    .orElseThrow(() -> new RuntimeException("Dispositivo no encontrada"));

            if (!dispositivo.getUsuario().getUsername().equals(username)){
                throw new RuntimeException("No autorizado");
            }

            boolean yaExiste = dispositivoReglaFirewallRepository
                    .existsByDispositivoIdAndReglaFirewallId(dispositivoId, reglaFirewallId);

            if(yaExiste) continue;

//            Map<String, Object> resultado = fortiGateService.crearPolicy(dispositivo, reglaFirewall);
//
//            if (!(Boolean) resultado.get("success")) {
//                throw new RuntimeException(
//                        "Error creando policy en FortiGate: " + resultado
//                );
//            }

            DispositivoReglaFirewall rel =  new DispositivoReglaFirewall();
            rel.setDispositivo(dispositivo);
            rel.setReglaFirewall(reglaFirewall);

            dispositivoReglaFirewallRepository.save(rel);
        }
    }

    public List<ReglaFirewallDTO> listarPorDispositivo(Long dispositivoId, String username) {
        Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no encontrada"));

        if (!dispositivo.getUsuario().getUsername().equals(username)){
            throw new RuntimeException("Usuario no encontrado");
        }

        return dispositivoReglaFirewallRepository.findByDispositivoId(dispositivoId)
                .stream()
                .map(rel -> toDTO(rel.getReglaFirewall(), dispositivoId))
                .toList();
    }

    public List<ReglaFirewallDTO> listarPorUsario (String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(!usuario.getUsername().equals(username)) throw new RuntimeException("Usuario no encontrado");

        return reglaRepo.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::toReglaFirewallDTO)
                .toList();
    }

    private ReglaFirewallDTO toDTO(ReglaFirewall regla, Long dispositivoId) {
        ReglaFirewallDTO dto = new ReglaFirewallDTO();
        dto.setId(regla.getId());
        dto.setNombre(regla.getNombre());
        dto.setOrigen(regla.getOrigen());
        dto.setDestino(regla.getDestino());
        dto.setIpOrigen(regla.getIporigen());
        dto.setIpDestino(regla.getIpdestino());
        dto.setServicio(regla.getServicio());
        dto.setHabilitada(regla.isHabilitada());
        dto.setNat(regla.getNat());
        dto.setAction(regla.getAction());
        dto.setDispositivoId(dispositivoId);
        return dto;
    }

    private ReglaFirewallDTO toReglaFirewallDTO(ReglaFirewall regla) {
        ReglaFirewallDTO dto = new ReglaFirewallDTO();
        dto.setId(regla.getId());
        dto.setNombre(regla.getNombre());
        dto.setOrigen(regla.getOrigen());
        dto.setDestino(regla.getDestino());
        dto.setIpOrigen(regla.getIporigen());
        dto.setIpDestino(regla.getIpdestino());
        dto.setServicio(regla.getServicio());
        dto.setHabilitada(regla.isHabilitada());
        dto.setNat(regla.getNat());
        dto.setAction(regla.getAction());

        return dto;
    }

    public void eliminarRegla(Long reglaId, String username) {
        ReglaFirewall regla = reglaRepo.findById(reglaId)
                .orElseThrow(() -> new RuntimeException("Regla no existe"));

        List<DispositivoReglaFirewall> relaciones = dispositivoReglaFirewallRepository
                .findByReglaFirewallId(reglaId);

        if(relaciones.isEmpty()){
            throw new RuntimeException("Relacion no encontrada");
        }

        for(DispositivoReglaFirewall relacion : relaciones){
            Dispositivo dispositivo = relacion.getDispositivo();

            if(dispositivo.getUsuario().getUsername().equals(username)){
                throw new RuntimeException("No autorizado");
            }

            Map<String, Object> resultado = fortiGateService.eliminarReglaFirewall(
                    dispositivo,
                    regla.getNombre()
            );

            if (!(Boolean) resultado.get("success")) {
                throw new RuntimeException("Error eliminando ReglaFirewall en FortiGate: " + resultado);
            }

            dispositivoReglaFirewallRepository.delete(relacion);

        }

        reglaRepo.delete(regla);
    }


    public ReglaFirewallDTO editarReglaFirewall(Long id ,CrearReglaFirewallDTO dto, String username) {

        List<DispositivoReglaFirewall> relaciones = dispositivoReglaFirewallRepository
                .findByReglaFirewallId(id);

        if (relaciones.isEmpty()) {
            throw new RuntimeException("Relacion no encontrada");
        }

        ReglaFirewall regla = relaciones.get(0).getReglaFirewall();

        for(DispositivoReglaFirewall relacion : relaciones){
            if(!relacion.getDispositivo().getUsuario().getUsername().equals(username)){
                throw new RuntimeException("No autorizado");
            }
        }

        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        regla.setNombre(dto.getNombre());
        regla.setOrigen(dto.getOrigen());
        regla.setDestino(dto.getDestino());
        regla.setIporigen(dto.getIpOrigen());
        regla.setIpdestino(dto.getIpDestino());
        regla.setServicio(dto.getServicio());
        regla.setNat(regla.getNat());
        regla.setAction(regla.getAction());
        regla.setHabilitada(true);

        Dispositivo dispositivoRef = relaciones.get(0).getDispositivo();

        for (DispositivoReglaFirewall relacion : relaciones){
            Dispositivo dispositivo = relacion.getDispositivo();

            Map<String, Object> resultado = fortiGateService.editarPolicy(dispositivo, regla);

            if (!(Boolean) resultado.get("success")) {
                throw new RuntimeException(
                        "Error editando policy en FortiGate: " + resultado
                );
            }
        }

        ReglaFirewall saved = reglaRepo.save(regla);

        return toDTO(saved, dispositivoRef.getId());
    }
}

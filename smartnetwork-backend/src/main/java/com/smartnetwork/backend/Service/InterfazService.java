package com.smartnetwork.backend.Service;
import com.smartnetwork.backend.Repository.DispositivoInterfazRepository;
import com.smartnetwork.backend.Repository.UsuarioRepository;
import com.smartnetwork.backend.domain.Entity.DispositivoInterfaz;
import com.smartnetwork.backend.domain.Entity.Usuario;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.InterfazRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.Interfaz;
import com.smartnetwork.backend.domain.dtos.interfaz.CrearInterfazDTO;
import com.smartnetwork.backend.domain.dtos.interfaz.InterfazDTO;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class InterfazService {

    private final InterfazRepository interfazRepo;
    private final DispositivoRepository dispositivoRepo;
    private final DispositivoInterfazRepository dispositivoInterfazRepo;
    private final FortiGateService fortiGateService;
    private final UsuarioRepository usuarioRepo;

    public InterfazService(
            InterfazRepository interfazRepo,
            DispositivoRepository dispositivoRepo, DispositivoInterfazRepository dispositivoInterfazRepo,
            FortiGateService fortiGateService, UsuarioRepository usuarioRepo
    ) {
        this.interfazRepo = interfazRepo;
        this.dispositivoRepo = dispositivoRepo;
        this.dispositivoInterfazRepo = dispositivoInterfazRepo;
        this.fortiGateService = fortiGateService;
        this.usuarioRepo = usuarioRepo;
    }

    public InterfazDTO crear(CrearInterfazDTO crearInterfazDTO, String nombre) {
        InterfazDTO interfaz = crearInterfaz(crearInterfazDTO, nombre);

        if (crearInterfazDTO.getDispositivosId() != null && !crearInterfazDTO.getDispositivosId().isEmpty()) {
            asignarInterfazADispositivos(interfaz.getId(), crearInterfazDTO.getDispositivosId());
        }

        return interfaz;
    }

    public InterfazDTO crearInterfaz(CrearInterfazDTO dto, String username) {
        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if ("vlan".equals(dto.getTipo())) {
            if (dto.getVlanid() == null || dto.getInterfacePadre() == null) {
                throw new RuntimeException("VLAN requiere interfacePadre y vlanid");
            }
        }

        Interfaz interfaz = new Interfaz();
        interfaz.setName(dto.getName());
        interfaz.setTipo(dto.getTipo());
        interfaz.setInterfacePadre(dto.getInterfacePadre());
        interfaz.setVlanid(dto.getVlanid());
        interfaz.setVdom(dto.getVdom() != null ? dto.getVdom() : "root");
        interfaz.setMode(dto.getMode());
        interfaz.setIp(dto.getIp());
        interfaz.setAllowaccess(dto.getAllowaccess());
        interfaz.setRole(dto.getRole());
        interfaz.setDescription(dto.getDescription());
        interfaz.setUsuario(usuario);

        Interfaz saved = interfazRepo.save(interfaz);
        return toInterfazDTO(saved);
    }

    public void asignarInterfazADispositivos(Long interfazId, List<Long> dispositivosId) {
        Interfaz interfaz = interfazRepo.findById(interfazId)
                .orElseThrow(() -> new RuntimeException("Interfaz no existe"));

        if (!interfaz.getUsuario().getId().equals(interfaz.getUsuario().getId())) {
            throw new  RuntimeException("No autorizado");
        }

        for (Long dispositivoId : dispositivosId) {
            Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
                    .orElseThrow(() -> new RuntimeException("Dispositivo no encontrado"));

            if(!dispositivo.getUsuario().getId().equals(interfaz.getUsuario().getId())) {
                throw new  RuntimeException("No autorizado");
            }

            boolean yaExiste = dispositivoInterfazRepo
                    .existsByIdDispositivoIdAndIdInterfazId(dispositivoId, interfazId);

            if (yaExiste) continue;

//            Map<String, Object> resultado = fortiGateService.crearInterfaz(dispositivo, interfaz);
//
//            if (!(Boolean) resultado.get("success")) {
//                throw new RuntimeException("Error creando address en FortiGate: " + resultado);
//            }

            DispositivoInterfaz rel = new DispositivoInterfaz();
            rel.setDispositivo(dispositivo);
            rel.setInterfaz(interfaz);
            rel.setComentario(interfaz.getDescription());

            dispositivoInterfazRepo.save(rel);
        }
    }

    public List<InterfazDTO> listarPorDispositivo(Long dispositivoId, String username) {

        Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return dispositivoInterfazRepo.findByIdDispositivoId(dispositivoId)
                .stream()
                .map(rel -> toDTO(rel.getInterfaz(), dispositivoId))
                .toList();
    }

    public List<InterfazDTO> listarPorUsuario(String username) {
        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(!usuario.getUsername().equals(username)){
            throw new RuntimeException("No autorizado");
        }

        return interfazRepo.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::toInterfazDTO)
                .toList();
    }

    private InterfazDTO toDTO(Interfaz interfaz, Long dispositivoId) {

        InterfazDTO dto = new InterfazDTO();
        dto.setId(interfaz.getId());
        dto.setName(interfaz.getName());
        dto.setTipo(interfaz.getTipo());
        dto.setInterfacePadre(interfaz.getInterfacePadre());
        dto.setVlanid(interfaz.getVlanid());
        dto.setVdom(interfaz.getVdom());
        dto.setMode(interfaz.getMode());
        dto.setIp(interfaz.getIp());
        dto.setAllowaccess(interfaz.getAllowaccess());
        dto.setRole(interfaz.getRole());
        dto.setDescription(interfaz.getDescription());

        dto.setDispositivoId(dispositivoId);

        return dto;
    }

    private InterfazDTO toInterfazDTO(Interfaz interfaz) {
        InterfazDTO dto = new InterfazDTO();
        dto.setId(interfaz.getId());
        dto.setName(interfaz.getName());
        dto.setTipo(interfaz.getTipo());
        dto.setInterfacePadre(interfaz.getInterfacePadre());
        dto.setVlanid(interfaz.getVlanid());
        dto.setVdom(interfaz.getVdom());
        dto.setMode(interfaz.getMode());
        dto.setIp(interfaz.getIp());
        dto.setAllowaccess(interfaz.getAllowaccess());
        dto.setRole(interfaz.getRole());
        dto.setDescription(interfaz.getDescription());
        return dto;
    }

    public void eliminarInterfaz(Long interfazId, String username) {
        Interfaz interfaz = interfazRepo.findById(interfazId)
                .orElseThrow(() -> new RuntimeException("Interfaz no encontrado"));

        List<DispositivoInterfaz> rel = dispositivoInterfazRepo
                .findByIdInterfazId(interfazId);

        for (DispositivoInterfaz dispositivoInterfaz : rel) {
            Dispositivo dispositivo = dispositivoInterfaz.getDispositivo();

            if (!dispositivo.getUsuario().getUsername().equals(username)) {
                throw new RuntimeException("No autorizado");
            }

            //        Map<String, Object> resultado = fortiGateService.eliminarInterfaz(dispositivo, interfaz.getName());
            //
            //        if (!(Boolean) resultado.get("success")) {
            //            throw new RuntimeException("Error eliminando Interfaz en FortiGate: " + resultado);
            //        }

            dispositivoInterfazRepo.delete(dispositivoInterfaz);
        }
        interfazRepo.delete(interfaz);
    }

    public InterfazDTO actualizar(Long interfazId, CrearInterfazDTO dto, String username) {
        List<DispositivoInterfaz> rel = dispositivoInterfazRepo
                .findByIdInterfazId(interfazId);

        if (rel.isEmpty()) {
            throw new RuntimeException("Relación no encontrada");
        }

        Interfaz interfaz = rel.get(0).getInterfaz();

        for (DispositivoInterfaz dispositivoInterfaz : rel) {
            if (!dispositivoInterfaz.getDispositivo().getUsuario().getUsername().equals(username)) {
                throw new RuntimeException("No autorizado");
            }
        }

        interfaz.setName(dto.getName());
        interfaz.setTipo(dto.getTipo());
        interfaz.setInterfacePadre(dto.getInterfacePadre());
        interfaz.setVlanid(dto.getVlanid());
        interfaz.setVdom(dto.getVdom());
        interfaz.setMode(dto.getMode());
        interfaz.setIp(dto.getIp());
        interfaz.setAllowaccess(dto.getAllowaccess());
        interfaz.setRole(dto.getRole());
        interfaz.setDescription(dto.getDescription());

        Dispositivo dispositivoRef = rel.get(0).getDispositivo();

        for (DispositivoInterfaz dispositivoInterfaz : rel) {

            Dispositivo dispositivo = dispositivoInterfaz.getDispositivo();

            Map<String, Object> resultado =
                    fortiGateService.editarInterfaz(dispositivo, interfaz, interfaz.getName());

            if (!(Boolean) resultado.get("success")) {
                throw new RuntimeException(
                        "Error actualizando interfaz en FortiGate: " + resultado
                );
            }
        }


        Interfaz saved = interfazRepo.save(interfaz);
        return toDTO(saved, dispositivoRef.getId());
    }

//    public Interfaz findByNameAndDispositivoId(String name, Long dispositivoId, String username) {
//        Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
//                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));
//
//        if (!dispositivo.getUsuario().getUsername().equals(username)) {
//            throw new RuntimeException("No autorizado");
//        }
//        return interfazRepo.findByNameAndDispositivoId(name, dispositivoId)
//                .orElseThrow(() -> new RuntimeException("Interfaz '" + name + "' no encontrada en este dispositivo"));
//    }
}

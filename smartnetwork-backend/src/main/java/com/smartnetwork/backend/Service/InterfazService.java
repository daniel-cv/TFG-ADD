package com.smartnetwork.backend.Service;
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
    private final FortiGateService fortiGateService;

    public InterfazService(
            InterfazRepository interfazRepo,
            DispositivoRepository dispositivoRepo,
            FortiGateService fortiGateService
    ) {
        this.interfazRepo = interfazRepo;
        this.dispositivoRepo = dispositivoRepo;
        this.fortiGateService = fortiGateService;
    }

    public InterfazDTO crear(CrearInterfazDTO dto, String username) {

        if (dto.getDispositivoId() == null) {
            throw new RuntimeException("dispositivoId obligatorio");
        }

        Dispositivo dispositivo = dispositivoRepo
                .findById(dto.getDispositivoId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

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
        interfaz.setDispositivo(dispositivo);

        Map<String, Object> resultado =
                fortiGateService.crearInterfaz(dispositivo, interfaz);

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException(
                    "Error creando interfaz en FortiGate: " + resultado
            );
        }

        Interfaz saved = interfazRepo.save(interfaz);

        return toDTO(saved);
    }

    public List<InterfazDTO> listarPorDispositivo(Long dispositivoId, String username) {

        Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return interfazRepo.findByDispositivoId(dispositivoId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private InterfazDTO toDTO(Interfaz interfaz) {

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

        dto.setDispositivoId(interfaz.getDispositivo().getId());

        return dto;
    }

    public void eliminar(Long interfazId, String username) {
        Interfaz interfaz = interfazRepo.findById(interfazId)
                .orElseThrow(() -> new RuntimeException("Interfaz no existe"));

        if (!interfaz.getDispositivo().getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        Map<String, Object> resultado = fortiGateService.eliminarInterfaz(interfaz.getDispositivo(), interfaz.getName());

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException("Error eliminando Interfaz en FortiGate: " + resultado);
        }

        interfazRepo.delete(interfaz);
    }

    public InterfazDTO actualizar(Long interfazId, CrearInterfazDTO dto, String username) {

        Interfaz interfaz = interfazRepo.findById(interfazId)
                .orElseThrow(() -> new RuntimeException("Interfaz no existe"));

        if (!interfaz.getDispositivo().getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        // Actualizamos campos
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

        // Llamada a FortiGate para actualizar
        Map<String, Object> resultado =
                fortiGateService.editarInterfaz(interfaz.getDispositivo(), interfaz, interfaz.getName());

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException(
                    "Error actualizando interfaz en FortiGate: " + resultado
            );
        }

        Interfaz saved = interfazRepo.save(interfaz);
        return toDTO(saved);
    }

    public Interfaz findByNameAndDispositivoId(String name, Long dispositivoId, String username) {
        Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return interfazRepo.findByNameAndDispositivoId(name, dispositivoId)
                .orElseThrow(() -> new RuntimeException("Interfaz '" + name + "' no encontrada en este dispositivo"));
    }
}

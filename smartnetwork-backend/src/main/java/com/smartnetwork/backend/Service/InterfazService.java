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

        // Validaciones básicas
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

        Interfaz saved = interfazRepo.save(interfaz);

        // 🔥 Crear interfaz en FortiGate
        Map<String, Object> resultado =
                fortiGateService.crearInterfaz(saved.getDispositivo(), saved);

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException(
                    "Error creando interfaz en FortiGate: " + resultado
            );
        }

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

        // 🔑 siempre ID
        dto.setDispositivoId(interfaz.getDispositivo().getId());

        return dto;
    }

    public void eliminar(Long interfazId, String username) {

        // 1️⃣ Obtener la interfaz de la BBDD
        Interfaz interfaz = interfazRepo.findById(interfazId)
                .orElseThrow(() -> new RuntimeException("Interfaz no existe"));

        // 2️⃣ Validar que el usuario es propietario del dispositivo
        if (!interfaz.getDispositivo().getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        Dispositivo dispositivo = interfaz.getDispositivo();
        String interfazName = interfaz.getName();

        // 3️⃣ Preparar llamada a FortiGate
        String url = "http://" + dispositivo.getIp()
                + "/api/v2/cmdb/system/interface/"
                +  URLEncoder.encode(interfazName, StandardCharsets.UTF_8)
                + "?vdom=root";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(dispositivo.getToken());

        HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {

                // ✅ Primero FortiGate OK → luego BBDD
                interfazRepo.delete(interfaz);

            } else {

                throw new RuntimeException(
                        "FortiGate respondió con estado: " + response.getStatusCode()
                );
            }

        } catch (Exception e) {

            // ❌ No tocar BBDD si falla FortiGate
            throw new RuntimeException(
                    "Error eliminando Interfaz en FortiGate (Interfaz=" + interfazName + ")", e
            );
        }
    }
}

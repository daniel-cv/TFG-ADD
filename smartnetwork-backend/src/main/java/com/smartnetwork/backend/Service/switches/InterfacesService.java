package com.smartnetwork.backend.Service.switches;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.UsuarioRepository;
import com.smartnetwork.backend.Repository.switches.AclRepository;
import com.smartnetwork.backend.Repository.switches.InterfacesRepository;
import com.smartnetwork.backend.Repository.switches.VlanRepository;
import com.smartnetwork.backend.Service.LogService;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.Log;
import com.smartnetwork.backend.domain.Entity.Usuario;
import com.smartnetwork.backend.domain.Entity.switches.Acl;
import com.smartnetwork.backend.domain.Entity.switches.Interfaces;
import com.smartnetwork.backend.domain.Entity.switches.Vlan;
import com.smartnetwork.backend.domain.Enum.TipoAccion;
import com.smartnetwork.backend.domain.dtos.switches.interfaz.CrearInterfacesDTO;
import com.smartnetwork.backend.domain.dtos.switches.interfaz.InterfacesDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class InterfacesService {

    private final InterfacesRepository interfacesRepository;
    private final DispositivoRepository dispositivoRepository;
    private final VlanRepository vlanRepository;
    private final AclRepository aclRepository;
    private final LogService logService;
    private final UsuarioRepository usuarioRepository;

    public InterfacesService(
            InterfacesRepository interfacesRepository,
            DispositivoRepository dispositivoRepository,
            VlanRepository vlanRepository,
            AclRepository aclRepository,
            LogService logService,
            UsuarioRepository usuarioRepository
    ) {
        this.interfacesRepository = interfacesRepository;
        this.dispositivoRepository = dispositivoRepository;
        this.vlanRepository = vlanRepository;
        this.aclRepository = aclRepository;
        this.logService = logService;
        this.usuarioRepository = usuarioRepository;
    }

    public List<InterfacesDTO> listarPorDispositivo(Long id, String username) {
        List<Interfaces> interfaces = interfacesRepository.findByDispositivoId(id);
        return interfaces.stream().map(i -> {

            InterfacesDTO dto = new InterfacesDTO();

            dto.setId(i.getId());
            dto.setName(i.getName());
            dto.setEstado(i.getEstado());
            dto.setMode(i.getMode());
            dto.setDescripcion(i.getDescripcion());
            dto.setEnabled(i.getEnabled());

            if (i.getAclIn() != null) {
                dto.setAclIn(i.getAclIn().getNombre());
                dto.setAclDirection(i.getAclDirection());
            }

            if (i.getVlanAccess() != null) {
                dto.setVlanAccess(i.getVlanAccess().getVlanId());
            }

            if (i.getVlansTrunk() != null) {
                dto.setVlansTrunk(i.getVlansTrunk().stream()
                        .map(Vlan::getVlanId)
                        .toList()
                );
            }
            return dto;
        }).toList();
    }

    public InterfacesDTO actualizarInterfaz(Long id, CrearInterfacesDTO dto, String username) {
        Usuario usu = usuarioRepository.findByUsername(username).orElseThrow(()->new RuntimeException("Usuario no encontrado"));
        List<Log> logs = new ArrayList<Log>();
        Interfaces interfaz = interfacesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interfaz no encontrada"));

        Dispositivo dispositivo = interfaz.getDispositivo();

        Boolean nuevoEstado = dto.getEnabled() != null ? dto.getEnabled() : interfaz.getEnabled();

        interfaz.setDescripcion(dto.getDescripcion());
        interfaz.setMode(dto.getMode());
        if (dto.getMode() != null &&
                !List.of("access", "trunk", "routed").contains(dto.getMode())) {
            throw new RuntimeException("Modo inválido");
        }


        if (dto.getVlanAccess() != null) {
            Vlan vlan = vlanRepository.findByVlanId(dto.getVlanAccess())
                    .orElseThrow(() -> new RuntimeException("VLAN no encontrada"));

            interfaz.setVlanAccess(vlan);
        } else {
            interfaz.setVlanAccess(null);
        }

        if (dto.getVlansTrunk() != null) {

            if ("access".equals(dto.getMode())) {
                throw new RuntimeException("No puedes configurar trunk en modo access");
            }
            List<Vlan> vlans = dto.getVlansTrunk().stream()
                    .map(vlanId -> vlanRepository.findByVlanId(vlanId)
                            .orElseThrow(() -> new RuntimeException("VLAN no encontrada: " + vlanId)))
                    .collect(Collectors.toList());
            interfaz.setVlansTrunk(vlans);
        }
        if (dto.getAclIn() != null) {
            Acl acl = aclRepository.findByNombreAndDispositivo(dto.getAclIn(), dispositivo)
                    .orElseThrow(() -> new RuntimeException("ACL no encontrada"));
            interfaz.setAclIn(acl);
            interfaz.setAclDirection(dto.getAclDirection() != null ? dto.getAclDirection() : "in");
        }

        if (Boolean.TRUE.equals(dto.getEliminarAcl())) {
            interfaz.setAclIn(null);
            interfaz.setAclDirection(null);
        }
        interfaz.setEnabled(nuevoEstado);
        logs.add(logService.crearLog(usu,dispositivo, TipoAccion.EDITAR,"Se ha EDITADO la Interfaz "+interfaz.getName()));
        configurarInterfaz(dispositivo, interfaz, dto);
        interfacesRepository.save(interfaz);
        logService.guardarTodos(logs);
        return mapToDTO(interfaz);
    }

    private InterfacesDTO mapToDTO(Interfaces interfaz) {

        InterfacesDTO dto = new InterfacesDTO();

        dto.setId(interfaz.getId());
        dto.setName(interfaz.getName());
        dto.setEstado(interfaz.getEstado());
        dto.setMode(interfaz.getMode());
        dto.setDescripcion(interfaz.getDescripcion());
        dto.setEnabled(interfaz.getEnabled());

        if (interfaz.getAclIn() != null) {
            dto.setAclIn(interfaz.getAclIn().getNombre());
            dto.setAclDirection(interfaz.getAclDirection());
        }

        if (interfaz.getVlanAccess() != null) {
            dto.setVlanAccess(interfaz.getVlanAccess().getVlanId());
        }

        if (interfaz.getVlansTrunk() != null) {
            dto.setVlansTrunk(
                    interfaz.getVlansTrunk().stream()
                            .map(Vlan::getVlanId)
                            .toList()
            );
        }

        return dto;
    }

    private void configurarInterfaz(Dispositivo dispositivo, Interfaces interfaz, CrearInterfacesDTO dto) {

        try {
            String url = "http://" + dispositivo.getIp() + "/command-api";

            List<String> comandos = new java.util.ArrayList<>();

            comandos.add("enable");
            comandos.add("configure terminal");
            comandos.add("interface " + interfaz.getName());

            if ("routed".equals(dto.getMode())) {
                comandos.add("no switchport");
            } else {
                comandos.add("switchport");
            }

            if (dto.getDescripcion() != null) {
                comandos.add("description " + dto.getDescripcion());
            }

            if (dto.getEnabled() != null) {
                comandos.add(dto.getEnabled() ? "no shutdown" : "shutdown");
            }

            if (!"routed".equals(dto.getMode())) {

                if (dto.getVlanAccess() != null) {
                    comandos.add("switchport mode access");
                    comandos.add("switchport access vlan " + dto.getVlanAccess());
                }

                if (dto.getVlansTrunk() != null && !dto.getVlansTrunk().isEmpty()) {

                    String vlans = dto.getVlansTrunk()
                            .stream()
                            .map(String::valueOf)
                            .reduce((a, b) -> a + "," + b)
                            .orElse("");

                    comandos.add("switchport mode trunk");
                    comandos.add("switchport trunk allowed vlan " + vlans);
                }
            }

            if ("routed".equals(dto.getMode())) {

                if (Boolean.TRUE.equals(dto.getEliminarAcl()) && interfaz.getAclIn() != null) {

                    String direction = interfaz.getAclDirection() != null
                            ? interfaz.getAclDirection()
                            : "in";

                    comandos.add("no ip access-group " +
                            interfaz.getAclIn().getNombre() + " " + direction);
                }
                if (dto.getAclIn() != null) {

                    String direction = dto.getAclDirection() != null
                            ? dto.getAclDirection()
                            : "in";

                    comandos.add("ip access-group " + dto.getAclIn() + " " + direction);
                }
            }

            comandos.add("end");
            comandos.add("write memory");

            enviarComandos(dispositivo, url, comandos);

        } catch (Exception e) {
            throw new RuntimeException("Error configurando interfaz en el switch", e);
        }
    }

    private void enviarComandos(Dispositivo dispositivo, String url, List<String> comandos) throws Exception {

        String payload = """
        {
          "jsonrpc": "2.0",
          "method": "runCmds",
          "params": {
            "version": 1,
            "cmds": %s
          },
          "id": 1
        }
        """.formatted(new ObjectMapper().writeValueAsString(comandos));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(
                dispositivo.getUsuarioConexion(),
                dispositivo.getPasswordConexion()
        );

        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        new RestTemplate().postForEntity(url, request, String.class);
    }
}
package com.smartnetwork.backend.Service.switches;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.UsuarioRepository;
import com.smartnetwork.backend.Repository.switches.VlanRepository;
import com.smartnetwork.backend.Service.LogService;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.Log;
import com.smartnetwork.backend.domain.Entity.Usuario;
import com.smartnetwork.backend.domain.Entity.switches.Interfaces;
import com.smartnetwork.backend.Repository.switches.InterfacesRepository;
import com.smartnetwork.backend.domain.Entity.switches.Vlan;
import com.smartnetwork.backend.domain.Enum.TipoAccion;
import com.smartnetwork.backend.domain.dtos.switches.vlan.CrearVlanDTO;
import com.smartnetwork.backend.domain.dtos.switches.vlan.VlanDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class VlanService {

    private final VlanRepository vlanRepository;
    private final DispositivoRepository dispositivoRepository;
    private final InterfacesRepository interfacesRepository;
    private final LogService logService;
    private final UsuarioRepository usuarioRepository;

    public VlanService(VlanRepository vlanRepository,
                       DispositivoRepository dispositivoRepository, InterfacesRepository interfacesRepository, LogService logService,UsuarioRepository usuarioRepository) {
        this.vlanRepository = vlanRepository;
        this.dispositivoRepository = dispositivoRepository;
        this.interfacesRepository = interfacesRepository;
        this.logService = logService;
        this.usuarioRepository = usuarioRepository;
    }

    public Vlan getOrCreateVlan(Integer vlanId, Dispositivo dispositivo) {
        return vlanRepository
                .findByVlanIdAndDispositivoId(vlanId, dispositivo.getId())
                .orElseGet(() -> {
                    Vlan vlan = new Vlan();
                    vlan.setVlanId(vlanId);
                    vlan.setNombre("VLAN " + vlanId);
                    vlan.setDispositivo(dispositivo);
                    return vlanRepository.save(vlan);
                });
    }

    public List<VlanDTO> listarPorDispositivo(Long dispositivoId, String username) {

        List<Vlan> vlans = vlanRepository.findByDispositivoId(dispositivoId);

        return vlans.stream().map(this::mapToDTO).toList();
    }

    public VlanDTO crearVlan(CrearVlanDTO dto, String username) {
        Usuario usu = usuarioRepository.findByUsername(username).orElseThrow(()->new RuntimeException("Usuario no encontrado"));
        List<Log> logs = new ArrayList<Log>();
        Dispositivo dispositivo = dispositivoRepository.findById(dto.getDispositivoId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no encontrado"));

        vlanRepository.findByVlanIdAndDispositivoId(dto.getVlanId(), dispositivo.getId())
                .ifPresent(v -> {
                    throw new RuntimeException("La VLAN ya existe");
                });
        Vlan vlan = new Vlan();
        vlan.setVlanId(dto.getVlanId());
        vlan.setNombre(dto.getNombre());
        vlan.setDispositivo(dispositivo);
        logs.add(logService.crearLog(usu,dispositivo, TipoAccion.CREAR,"Se ha CREADO la VLAN "+vlan.getNombre()));
        configurarVlan(dispositivo, vlan);
        vlanRepository.save(vlan);
        logService.guardarTodos(logs);
        return mapToDTO(vlan);
    }

    public VlanDTO actualizarVlan(Long id, CrearVlanDTO dto, String username) {
        Usuario usu = usuarioRepository.findByUsername(username).orElseThrow(()->new RuntimeException("Usuario no encontrado"));
        List<Log> logs = new ArrayList<Log>();
        Vlan vlan = vlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("VLAN no encontrada"));
        vlan.setNombre(dto.getNombre());
        logs.add(logService.crearLog(usu,vlan.getDispositivo(), TipoAccion.EDITAR,"Se ha EDITADO la VLAN "+vlan.getNombre()));
        configurarVlan(vlan.getDispositivo(), vlan);
        vlanRepository.save(vlan);
        return mapToDTO(vlan);
    }

    private VlanDTO mapToDTO(Vlan vlan) {
        VlanDTO dto = new VlanDTO();
        dto.setId(vlan.getId());
        dto.setVlanId(vlan.getVlanId());
        dto.setNombre(vlan.getNombre());
        return dto;
    }
    private void configurarVlan(Dispositivo dispositivo, Vlan vlan) {
        try {
            String url = "http://" + dispositivo.getIp() + "/command-api";

            List<String> comandos = new ArrayList<>();
            comandos.add("enable");
            comandos.add("configure terminal");

            comandos.add("vlan " + vlan.getVlanId());

            if (vlan.getNombre() != null) {
                comandos.add("name " + vlan.getNombre());
            }

            comandos.add("end");
            comandos.add("write memory");

            enviarComandos(dispositivo, url, comandos);

        } catch (Exception e) {
            throw new RuntimeException("Error configurando VLAN", e);
        }
    }

    private void configurarDeleteVlan(Dispositivo dispositivo, Vlan vlan) {

        try {
            String url = "http://" + dispositivo.getIp() + "/command-api";

            List<String> comandos = new ArrayList<>();
            comandos.add("enable");
            comandos.add("configure terminal");

            comandos.add("no vlan " + vlan.getVlanId());

            comandos.add("end");
            comandos.add("write memory");

            enviarComandos(dispositivo, url, comandos);

        } catch (Exception e) {
            throw new RuntimeException("Error eliminando VLAN", e);
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
    public void eliminarVlan(Long id, String username) {
        Usuario usu = usuarioRepository.findByUsername(username).orElseThrow(()->new RuntimeException("Usuario no encontrado"));
        List<Log> logs = new ArrayList<Log>();
        Vlan vlan = vlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("VLAN no encontrada"));

        List<Interfaces> interfaces = interfacesRepository.findByVlanAccess(vlan);

        for (Interfaces i : interfaces) {
            i.setVlanAccess(null);
        }

        interfacesRepository.saveAll(interfaces);
        List<Interfaces> allInterfaces = interfacesRepository.findAll();

        for (Interfaces i : allInterfaces) {
            if (i.getVlansTrunk() != null) {
                i.getVlansTrunk().remove(vlan);
            }
        }
        logs.add(logService.crearLog(usu,vlan.getDispositivo(), TipoAccion.ELIMINAR,"Se ha ELIMINADO la VLAN "+vlan.getNombre()));
        configurarDeleteVlan(vlan.getDispositivo(), vlan);
        interfacesRepository.saveAll(allInterfaces);
        vlanRepository.delete(vlan);
    }

}
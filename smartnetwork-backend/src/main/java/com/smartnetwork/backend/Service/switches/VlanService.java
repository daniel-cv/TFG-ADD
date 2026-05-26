package com.smartnetwork.backend.Service.switches;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.switches.VlanRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.switches.Interfaces;
import com.smartnetwork.backend.Repository.switches.InterfacesRepository;
import com.smartnetwork.backend.domain.Entity.switches.Vlan;
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

    public VlanService(VlanRepository vlanRepository,
                       DispositivoRepository dispositivoRepository, InterfacesRepository interfacesRepository) {
        this.vlanRepository = vlanRepository;
        this.dispositivoRepository = dispositivoRepository;
        this.interfacesRepository = interfacesRepository;
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

        Dispositivo dispositivo = dispositivoRepository.findById(dto.getDispositivoId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no encontrado"));

        // 🔥 VALIDAR DUPLICADO
        vlanRepository.findByVlanIdAndDispositivoId(dto.getVlanId(), dispositivo.getId())
                .ifPresent(v -> {
                    throw new RuntimeException("La VLAN ya existe");
                });

        Vlan vlan = new Vlan();
        vlan.setVlanId(dto.getVlanId());
        vlan.setNombre(dto.getNombre());
        vlan.setDispositivo(dispositivo);

        vlanRepository.save(vlan);

        configurarVlan(dispositivo, vlan);

        return mapToDTO(vlan);
    }

    public VlanDTO actualizarVlan(Long id, CrearVlanDTO dto, String username) {

        Vlan vlan = vlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("VLAN no encontrada"));

        vlan.setNombre(dto.getNombre());
        vlanRepository.save(vlan);

        configurarVlan(vlan.getDispositivo(), vlan);

        return mapToDTO(vlan);
    }

    private VlanDTO mapToDTO(Vlan vlan) {
        VlanDTO dto = new VlanDTO();
        dto.setId(vlan.getId());
        dto.setVlanId(vlan.getVlanId());
        dto.setNombre(vlan.getNombre());
        return dto;
    }

    // 🔥 CONFIGURAR VLAN
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

    // 🔥 BORRAR VLAN
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

    // 🔧 ENVÍO COMANDOS
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

        Vlan vlan = vlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("VLAN no encontrada"));

        // 🔥 1. Quitar VLAN de interfaces (ACCESS)
        List<Interfaces> interfaces = interfacesRepository.findByVlanAccess(vlan);

        for (Interfaces i : interfaces) {
            i.setVlanAccess(null);
        }

        interfacesRepository.saveAll(interfaces);

        // 🔥 2. Quitar VLAN de trunks
        List<Interfaces> allInterfaces = interfacesRepository.findAll();

        for (Interfaces i : allInterfaces) {
            if (i.getVlansTrunk() != null) {
                i.getVlansTrunk().remove(vlan);
            }
        }

        interfacesRepository.saveAll(allInterfaces);

        // 🔥 3. Borrar del switch
        configurarDeleteVlan(vlan.getDispositivo(), vlan);

        // 🔥 4. Borrar de BD
        vlanRepository.delete(vlan);
    }

}
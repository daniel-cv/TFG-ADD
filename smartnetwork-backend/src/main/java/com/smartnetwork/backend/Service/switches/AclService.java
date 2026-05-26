package com.smartnetwork.backend.Service.switches;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.switches.Acl;
import com.smartnetwork.backend.domain.Entity.switches.AclRegla;
import com.smartnetwork.backend.domain.dtos.switches.acls.AclDTO;
import com.smartnetwork.backend.domain.dtos.switches.acls.AclReglaDTO;
import com.smartnetwork.backend.domain.dtos.switches.acls.CrearAclDTO;
import com.smartnetwork.backend.domain.dtos.switches.acls.CrearReglaDTO;
import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.switches.AclRepository;
import com.smartnetwork.backend.Repository.switches.ReglaRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class AclService {

    private final AclRepository aclRepository;
    private final ReglaRepository reglaRepository;
    private final DispositivoRepository dispositivoRepository;

    public AclService(AclRepository aclRepository,
                      ReglaRepository reglaRepository,
                      DispositivoRepository dispositivoRepository) {
        this.aclRepository = aclRepository;
        this.reglaRepository = reglaRepository;
        this.dispositivoRepository = dispositivoRepository;
    }

    public Acl crearAcl(CrearAclDTO dto, String username) {

        Dispositivo dispositivo = dispositivoRepository.findById(dto.getDispositivoId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no encontrado"));

        aclRepository.findByNombreAndDispositivo(dto.getNombre(), dispositivo)
                .ifPresent(a -> {
                    throw new RuntimeException("La ACL ya existe");
                });

        Acl acl = new Acl();
        acl.setNombre(dto.getNombre());
        acl.setDispositivo(dispositivo);

        aclRepository.save(acl);

        configurarAcl(dispositivo, acl);

        return acl;
    }

    private void configurarAcl(Dispositivo dispositivo, Acl acl) {
        try {
            String url = getUrl(dispositivo);

            List<String> comandos = new ArrayList<>();
            comandos.add("enable");
            comandos.add("configure terminal");

            comandos.add("ip access-list " + acl.getNombre());

            comandos.add("end");
            comandos.add("write memory");

            enviarComandos(dispositivo, url, comandos);

        } catch (Exception e) {
            throw new RuntimeException("Error configurando ACL", e);
        }
    }

    public void agregarRegla(Long aclId, CrearReglaDTO dto, String username) {

        Acl acl = aclRepository.findById(aclId)
                .orElseThrow(() -> new RuntimeException("ACL no encontrada"));

        AclRegla regla = new AclRegla();
        regla.setAccion(dto.getAccion());
        regla.setOrigen(dto.getOrigen());
        regla.setDestino(dto.getDestino());
        regla.setOrden(dto.getOrden());
        regla.setAcl(acl);

        reglaRepository.save(regla);

        configurarRegla(acl.getDispositivo(), acl, regla);
    }

    private void configurarRegla(Dispositivo dispositivo, Acl acl, AclRegla regla) {
        try {
            String url = getUrl(dispositivo);

            List<String> comandos = new ArrayList<>();
            comandos.add("enable");
            comandos.add("configure terminal");

            comandos.add("ip access-list " + acl.getNombre());

            comandos.add(regla.getAccion() + " ip "
                    + regla.getOrigen() + " "
                    + regla.getDestino());

            comandos.add("end");
            comandos.add("write memory");

            enviarComandos(dispositivo, url, comandos);

        } catch (Exception e) {
            throw new RuntimeException("Error configurando regla ACL", e);
        }
    }

    @Transactional
    public void eliminarRegla(Long reglaId, String username) {

        AclRegla regla = reglaRepository.findById(reglaId)
                .orElseThrow(() -> new RuntimeException("Regla no encontrada"));

        eliminarReglaSwitch(regla.getAcl().getDispositivo(), regla);

        reglaRepository.delete(regla);
    }

    private void eliminarReglaSwitch(Dispositivo dispositivo, AclRegla regla) {
        try {
            String url = getUrl(dispositivo);

            List<String> comandos = new ArrayList<>();
            comandos.add("enable");
            comandos.add("configure terminal");

            comandos.add("ip access-list " + regla.getAcl().getNombre());

            comandos.add("no " + regla.getAccion() + " ip "
                    + regla.getOrigen() + " "
                    + regla.getDestino());

            comandos.add("end");
            comandos.add("write memory");

            enviarComandos(dispositivo, url, comandos);

        } catch (Exception e) {
            throw new RuntimeException("Error eliminando regla ACL", e);
        }
    }

    @Transactional
    public void eliminarAcl(Long id, String username) {

        Acl acl = aclRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ACL no encontrada"));

        eliminarAclSwitch(acl.getDispositivo(), acl);

        aclRepository.delete(acl);
    }

    private void eliminarAclSwitch(Dispositivo dispositivo, Acl acl) {
        try {
            String url = getUrl(dispositivo);

            List<String> comandos = new ArrayList<>();
            comandos.add("enable");
            comandos.add("configure terminal");

            comandos.add("no ip access-list " + acl.getNombre());

            comandos.add("end");
            comandos.add("write memory");

            enviarComandos(dispositivo, url, comandos);

        } catch (Exception e) {
            throw new RuntimeException("Error eliminando ACL", e);
        }
    }

    public List<AclDTO> listarPorDispositivo(Long dispositivoId) {

        return aclRepository.findByDispositivoId(dispositivoId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private String getUrl(Dispositivo dispositivo) {
        return "http://" + dispositivo.getIp() + "/command-api";
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
    private AclDTO mapToDTO(Acl acl) {

        AclDTO dto = new AclDTO();
        dto.setId(acl.getId());
        dto.setNombre(acl.getNombre());

        if (acl.getReglas() != null) {
            dto.setReglas(
                    acl.getReglas().stream()
                            .map(this::mapReglaToDTO)
                            .toList()
            );
        }

        return dto;
    }

    private AclReglaDTO mapReglaToDTO(AclRegla regla) {

        AclReglaDTO dto = new AclReglaDTO();
        dto.setId(regla.getId());
        dto.setAccion(regla.getAccion());
        dto.setOrigen(regla.getOrigen());
        dto.setDestino(regla.getDestino());
        dto.setOrden(regla.getOrden());

        return dto;
    }

}


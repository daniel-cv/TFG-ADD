package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.UsuarioRepository;
import com.smartnetwork.backend.Repository.switches.InterfacesRepository;
import com.smartnetwork.backend.Repository.switches.IpRouteRepository;
import com.smartnetwork.backend.Repository.switches.VlanRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.Usuario;
import com.smartnetwork.backend.domain.Entity.switches.Interfaces;
import com.smartnetwork.backend.domain.Entity.switches.IpRoute;
import com.smartnetwork.backend.domain.Entity.switches.Vlan;
import com.smartnetwork.backend.Service.switches.VlanService;
import com.smartnetwork.backend.domain.Enum.Fabricante;
import com.smartnetwork.backend.domain.Enum.TipoDispositivo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class DispositivoService {

    private final DispositivoRepository dispositivoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private InterfacesRepository interfacesRepository;
    private final VlanService vlanService;
    private final IpRouteRepository ipRouteRepository;

    public DispositivoService(
            DispositivoRepository dispositivoRepository,
            UsuarioRepository usuarioRepository,
            VlanRepository vlanRepository,
            VlanService vlanService,
            IpRouteRepository ipRouteRepository
    ) {
        this.dispositivoRepository = dispositivoRepository;
        this.usuarioRepository = usuarioRepository;
        this.vlanService = vlanService;
        this.ipRouteRepository = ipRouteRepository;
    }

    public Dispositivo crearDispositivo(Dispositivo dispositivo, String username) {

        try {

            if (dispositivo.getTipo() == TipoDispositivo.FIREWALL) {

                String url = "http://" + dispositivo.getIp() + "/api/v2/monitor/system/status";

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + dispositivo.getToken());

                HttpEntity<Void> entity = new HttpEntity<>(headers);

                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        String.class
                );
            }

            else if (dispositivo.getTipo() == TipoDispositivo.SWITCH) {

                String url = "http://" + dispositivo.getIp() + "/command-api";

                RestTemplate restTemplateSwitch = new RestTemplate();

                restTemplateSwitch.getInterceptors().add(
                        new BasicAuthenticationInterceptor(
                                dispositivo.getUsuarioConexion(),
                                dispositivo.getPasswordConexion()
                        )
                );

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                String body = """ 
                {
                  "jsonrpc": "2.0",
                   "method": "runCmds",
                    "params": {
                    "version": 1,
                     "cmds": [
                      "enable",
                      "show interfaces status"
                       ],
                       "format": "json"
                       },
                        "id": 1 }""";

                HttpEntity<String> entity = new HttpEntity<>(body, headers);

                String response = restTemplateSwitch.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        String.class
                ).getBody();
                String bodyRoutes = """ 
                {
                  "jsonrpc": "2.0",
                    "method": "runCmds",
                    "params": {
                    "version": 1,
                    "cmds": [
                        "enable",
                        "show running-config | section ip route"
                           ]
                        },
                         "id": 1
                }""";

                HttpEntity<String> entityRoutes = new HttpEntity<>(bodyRoutes, headers);

                String responseRoutes = restTemplateSwitch.exchange(
                        url,
                        HttpMethod.POST,
                        entityRoutes,
                        String.class
                ).getBody();

                System.out.println(response);


                Usuario usuario = usuarioRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                dispositivo.setUsuario(usuario);


                dispositivo = dispositivoRepository.save(dispositivo);

                guardarInterfaces(response, dispositivo);
                System.out.println(responseRoutes);
                guardarIpRoutes(responseRoutes, dispositivo);

                return dispositivo;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error conectando con el dispositivo: " + e.getMessage());
        }
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

    public List<Dispositivo> obtenerDispositivosDelUsuarioPorFabricante(String username, Fabricante fabricante){
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return dispositivoRepository.findByUsuarioAndFabricante(usuario, fabricante);
    }

    public Optional<Dispositivo> getDispositivo(Long id, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return dispositivoRepository.findById(id);
    }

    @Transactional
    public Dispositivo eliminarDispositivo(Long id, String username) {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Dispositivo dispositivo = dispositivoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dispositivo no encontrado"));
        System.out.println("Usuario autenticado: " + usuario.getUsername());
        System.out.println("Propietario dispositivo: " + dispositivo.getUsuario().getUsername());
        System.out.println("ID usuario autenticado: " + usuario.getId());
        System.out.println("ID propietario: " + dispositivo.getUsuario().getId());

        if (!dispositivo.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar este dispositivo");
        }

        dispositivoRepository.delete(dispositivo);

        return dispositivo;
    }


    private void guardarInterfaces(String json, Dispositivo dispositivo) {

        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode interfacesJson = root.get("result").get(1).get("interfaceStatuses");

            List<Interfaces> lista = new ArrayList<>();

            for (Iterator<String> it = interfacesJson.fieldNames(); it.hasNext();) {

                String nombre = it.next();
                JsonNode data = interfacesJson.get(nombre);

                Interfaces interfaz = interfacesRepository
                        .findByNameAndDispositivo(nombre, dispositivo)
                        .orElse(new Interfaces());

                interfaz.setName(nombre);

                String estado = data.has("linkStatus")
                        ? data.get("linkStatus").asText()
                        : "unknown";

                interfaz.setEstado(estado);

                String descripcion = data.has("description")
                        ? data.get("description").asText()
                        : "";

                interfaz.setDescripcion(descripcion);


                JsonNode vlanInfo = data.get("vlanInformation");

                if (vlanInfo != null) {

                    String modo = vlanInfo.has("interfaceMode")
                            ? vlanInfo.get("interfaceMode").asText()
                            : "unknown";

                    interfaz.setMode(modo);

                    if (vlanInfo.has("vlanId")) {
                        Integer vlanId = vlanInfo.get("vlanId").asInt();
                        interfaz.setVlanAccess(vlanService.getOrCreateVlan(vlanId, dispositivo));
                    } else {
                        interfaz.setVlanAccess(null);
                    }

                    if (vlanInfo.has("trunkAllowedVlans")) {

                        String vlansStr = vlanInfo.get("trunkAllowedVlans").asText();

                        List<Integer> vlanIds = parseVlans(vlansStr);

                        List<Vlan> vlans = vlanIds.stream()
                                .map(vlanId -> vlanService.getOrCreateVlan(vlanId, dispositivo))
                                .toList();

                        interfaz.setVlansTrunk(vlans);

                    } else {
                        interfaz.setVlansTrunk(new ArrayList<>());
                    }
                }

                interfaz.setDispositivo(dispositivo);
                lista.add(interfaz);
            }

            interfacesRepository.saveAll(lista);

        } catch (Exception e) {
            throw new RuntimeException("Error guardando interfaces", e);
        }
    }
    private List<Integer> parseVlans(String vlans) {

        return Arrays.stream(vlans.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .toList();
    }
    private void guardarIpRoutes(String json, Dispositivo dispositivo) {

        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode result = root.get("result");

            List<IpRoute> lista = new ArrayList<>();

            for (JsonNode node : result) {

                if (node.has("cmds")) {

                    JsonNode cmds = node.get("cmds");

                    cmds.fieldNames().forEachRemaining(cmd -> {

                        if (cmd.startsWith("ip route")) {

                            System.out.println("ROUTE DETECTADA: " + cmd);

                            String[] partes = cmd.split("\\s+");

                            if (partes.length >= 4) {

                                String destinoMascara = partes[2];
                                String gateway = partes[3];

                                String ipDestino;
                                String mascara;

                                if (destinoMascara.contains("/")) {
                                    String[] split = destinoMascara.split("/");
                                    ipDestino = split[0];
                                    mascara = convertirPrefijoAMascara(split[1]);
                                } else {
                                    ipDestino = destinoMascara;
                                    mascara = "0.0.0.0";
                                }

                                IpRoute route = ipRouteRepository
                                        .findByIpDestinoAndMascaraAndGatewayAndDispositivo(
                                                ipDestino, mascara, gateway, dispositivo
                                        )
                                        .orElse(new IpRoute());

                                route.setIpDestino(ipDestino);
                                route.setMascara(mascara);
                                route.setGateway(gateway);
                                route.setDispositivo(dispositivo);

                                lista.add(route);
                            }
                        }
                    });
                }
            }

            ipRouteRepository.saveAll(lista);

        } catch (Exception e) {
            throw new RuntimeException("Error guardando IP routes", e);
        }
    }
    private String convertirPrefijoAMascara(String prefijo) {

        int bits = Integer.parseInt(prefijo);
        int mask = 0xffffffff << (32 - bits);

        return ((mask >>> 24) & 0xff) + "." +
                ((mask >>> 16) & 0xff) + "." +
                ((mask >>> 8) & 0xff) + "." +
                (mask & 0xff);
    }
    @Transactional
    public Dispositivo editarDispositivo(Long id, Dispositivo datos, String username) {
        System.out.println("EDITAR EJECUTADO");
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Dispositivo dispositivo = dispositivoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dispositivo no encontrado"));

        System.out.println("Usuario autenticado: " + usuario.getUsername());
        System.out.println("Propietario dispositivo: " + dispositivo.getUsuario().getUsername());
        if (!dispositivo.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso");
        }

        dispositivo.setNombre(datos.getNombre());
        dispositivo.setIp(datos.getIp());

        return dispositivoRepository.save(dispositivo);
    }
}

package com.smartnetwork.backend.Service.switches;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.UsuarioRepository;
import com.smartnetwork.backend.Service.LogService;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.Log;
import com.smartnetwork.backend.domain.Entity.Usuario;
import com.smartnetwork.backend.domain.Entity.switches.IpRoute;
import com.smartnetwork.backend.domain.Enum.TipoAccion;
import com.smartnetwork.backend.domain.dtos.switches.IpRoute.CrearIpRouteDTO;
import com.smartnetwork.backend.domain.dtos.switches.IpRoute.IpRouteDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.smartnetwork.backend.Repository.switches.IpRouteRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class IpRouteService {
    private final IpRouteRepository ipRouteRepository;
    private final DispositivoRepository dispositivoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LogService logService;

    public IpRouteService(IpRouteRepository ipRouteRepository, DispositivoRepository dispositivoRepository, LogService logService, UsuarioRepository usuarioRepository) {
        this.ipRouteRepository = ipRouteRepository;
        this.dispositivoRepository = dispositivoRepository;
        this.logService = logService;
        this.usuarioRepository = usuarioRepository;
    }

    public IpRouteDTO crearIpRoute(CrearIpRouteDTO dto, String username) {
        Usuario usu = usuarioRepository.findByUsername(username).orElseThrow(()->new RuntimeException("Usuario no encontrado"));
        List<Log> logs = new ArrayList<Log>();
        Dispositivo dispositivo = dispositivoRepository.findById(dto.getDispositivoId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no encontrado"));


        ipRouteRepository.findByIpDestinoAndMascaraAndGatewayAndDispositivo(
                dto.getIpDestino(),
                dto.getMascara(),
                dto.getGateway(),
                dispositivo
        ).ifPresent(v -> {
                    throw new RuntimeException("La ruta ya existe");
                });
        IpRoute ipRoute = new IpRoute();
        ipRoute.setIpDestino(dto.getIpDestino());
        ipRoute.setMascara(dto.getMascara());
        ipRoute.setGateway(dto.getGateway());
        ipRoute.setDispositivo(dispositivo);
        logs.add(logService.crearLog(usu,dispositivo, TipoAccion.CREAR,"Se ha CREADO IpRoute destino "+ipRoute.getIpDestino()+" y gateway "+ipRoute.getGateway()));
        configurarIpRoute(dispositivo, ipRoute);
        ipRouteRepository.save(ipRoute);
        logService.guardarTodos(logs);

        return mapToDTO(ipRoute);
    }

    public List<IpRouteDTO> listarPorDispositivo(Long dispositivoId, String username) {

        List<IpRoute> IpRoute = ipRouteRepository.findByDispositivoId(dispositivoId);

        return IpRoute.stream().map(this::mapToDTO).toList();
    }
    private IpRouteDTO mapToDTO(IpRoute ipRoute) {
        IpRouteDTO dto = new IpRouteDTO();
        dto.setId(ipRoute.getId());
        dto.setIpDestino(ipRoute.getIpDestino());
        dto.setMascara(ipRoute.getMascara());
        dto.setGateway(ipRoute.getGateway());
        dto.setDispositivo(ipRoute.getDispositivo().getId());
        return dto;
    }

    private void configurarIpRoute(Dispositivo dispositivo, IpRoute route) {

        try {
            String url = "http://" + dispositivo.getIp() + "/command-api";

            List<String> comandos = new ArrayList<>();
            comandos.add("enable");
            comandos.add("configure terminal");
            comandos.add("ip route "
                    + route.getIpDestino() + "/" + route.getMascara() + " "
                    + route.getGateway());

            comandos.add("end");
            comandos.add("write memory");

            enviarComandos(dispositivo, url, comandos);

        } catch (Exception e) {
            throw new RuntimeException("Error configurando IP route", e);
        }
    }
    private void configurarDeleteIpRoute(Dispositivo dispositivo, IpRoute route) {

        try {
            String url = "http://" + dispositivo.getIp() + "/command-api";

            List<String> comandos = new ArrayList<>();
            comandos.add("enable");
            comandos.add("configure terminal");

            comandos.add("no ip route "
                    + route.getIpDestino() + " "
                    + route.getMascara() + " "
                    + route.getGateway());

            comandos.add("end");
            comandos.add("write memory");

            enviarComandos(dispositivo, url, comandos);

        } catch (Exception e) {
            throw new RuntimeException("Error eliminando IP Route", e);
        }
    }
    @Transactional
    public void eliminarIpRoute(Long id, String username) {
        Usuario usu = usuarioRepository.findByUsername(username).orElseThrow(()->new RuntimeException("Usuario no encontrado"));
        List<Log> logs = new ArrayList<Log>();

        IpRoute route = ipRouteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("IpRoute no encontrada"));
        logs.add(logService.crearLog(usu,route.getDispositivo(), TipoAccion.ELIMINAR,"Se ha ELIMINADO el IpRoute destino "+route.getIpDestino()+" y gateway "+route.getGateway()));
        configurarDeleteIpRoute(route.getDispositivo(), route);
        ipRouteRepository.delete(route);
        logService.guardarTodos(logs);
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
    public IpRouteDTO actualizarIpRoute(Long id, CrearIpRouteDTO dto, String username) {
        Usuario usu = usuarioRepository.findByUsername(username).orElseThrow(()->new RuntimeException("Usuario no encontrado"));
        List<Log> logs = new ArrayList<Log>();
        IpRoute route = ipRouteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route no encontrada"));

        route.setIpDestino(dto.getIpDestino());
        route.setMascara(dto.getMascara());
        route.setGateway(dto.getGateway());
        logs.add(logService.crearLog(usu,route.getDispositivo(), TipoAccion.EDITAR,"Se ha EDITADO el IpRoute  destino "+route.getIpDestino()+" y gateway "+route.getGateway()));
        configurarIpRoute(route.getDispositivo(), route);
        ipRouteRepository.save(route);
        logService.guardarTodos(logs);
        return mapToDTO(route);
    }
}

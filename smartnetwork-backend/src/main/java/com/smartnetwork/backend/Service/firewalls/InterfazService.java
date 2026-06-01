package com.smartnetwork.backend.Service.firewalls;
import com.smartnetwork.backend.Repository.firewalls.Interfaz.DispositivoInterfazRepository;
import com.smartnetwork.backend.Repository.UsuarioRepository;
import com.smartnetwork.backend.Service.LogService;
import com.smartnetwork.backend.domain.Entity.Log;
import com.smartnetwork.backend.domain.Entity.firewalls.Address.Address;
import com.smartnetwork.backend.domain.Entity.firewalls.Interfaz.DispositivoInterfaz;
import com.smartnetwork.backend.domain.Entity.Usuario;
import com.smartnetwork.backend.domain.Enum.TipoAccion;
import com.smartnetwork.backend.domain.dtos.firewalls.address.AddressDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.address.CrearAddressDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.firewalls.Interfaz.InterfazRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.firewalls.Interfaz.Interfaz;
import com.smartnetwork.backend.domain.dtos.firewalls.interfaz.CrearInterfazDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.interfaz.InterfazDTO;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InterfazService {

    private final InterfazRepository interfazRepo;
    private final DispositivoRepository dispositivoRepo;
    private final DispositivoInterfazRepository dispositivoInterfazRepo;
    private final FortiGateService fortiGateService;
    private final UsuarioRepository usuarioRepo;
    private final LogService logService;

    public InterfazService(
            InterfazRepository interfazRepo,
            DispositivoRepository dispositivoRepo,
            DispositivoInterfazRepository dispositivoInterfazRepo,
            FortiGateService fortiGateService,
            UsuarioRepository usuarioRepo,
            LogService logService
    ) {
        this.interfazRepo = interfazRepo;
        this.dispositivoRepo = dispositivoRepo;
        this.dispositivoInterfazRepo = dispositivoInterfazRepo;
        this.fortiGateService = fortiGateService;
        this.usuarioRepo = usuarioRepo;
        this.logService = logService;
    }

    @Transactional
    public InterfazDTO crear(CrearInterfazDTO dto, String username) {

        InterfazDTO interfaz = crearInterfaz(dto, username);

        if (dto.getDispositivosId() != null && !dto.getDispositivosId().isEmpty()) {
            asignarInterfazADispositivos(interfaz.getId(), dto.getDispositivosId(), username);
        }

        return interfaz;
    }

    public InterfazDTO crearInterfaz(CrearInterfazDTO dto, String username) {

        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Interfaz interfaz = new Interfaz();
        aplicarCambios(interfaz, dto);
        interfaz.setUsuario(usuario);

        return toInterfazDTO(interfazRepo.save(interfaz));
    }

    @Transactional
    public void asignarInterfazADispositivos(Long interfazId, List<Long> dispositivosIds, String username) {

        Interfaz interfaz = interfazRepo.findById(interfazId)
                .orElseThrow(() -> new RuntimeException("Interfaz no existe"));
        Usuario usu = usuarioRepo.findByUsername(username).orElseThrow(()->new RuntimeException("Usuario no encontrado"));
        List<Log> logs = new ArrayList<Log>();
        for (Long dispositivoId : dispositivosIds) {

            Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
                    .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

            if (!dispositivo.getUsuario().getUsername().equals(username)) {
                throw new RuntimeException("No autorizado");
            }

            boolean yaExiste = dispositivoInterfazRepo
                    .existsByIdDispositivoIdAndIdInterfazId(dispositivoId, interfazId);

            if (yaExiste) continue;

            Map<String, Object> res = fortiGateService.crearInterfaz(dispositivo, interfaz);

            if (!(Boolean) res.get("success")) {
                throw new RuntimeException("Error creando interfaz en FortiGate");
            }

            DispositivoInterfaz rel = new DispositivoInterfaz();
            rel.setDispositivo(dispositivo);
            rel.setInterfaz(interfaz);

            dispositivoInterfazRepo.save(rel);

            logs.add(logService.crearLog(usu,dispositivo, TipoAccion.CREAR,"Se ha CREADO la Interfaz  "+interfaz.getName()));
        }
        logService.guardarTodos(logs);
    }

    public List<InterfazDTO> listarPorDispositivo(Long dispositivoId, String username) {

        Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
                .orElseThrow();

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
                .orElseThrow();

        return interfazRepo.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::toInterfazDTO)
                .toList();
    }

    @Transactional
    public void eliminarInterfaz(Long interfazId, List<Long> dispositivosIds, String username) {

        Interfaz interfaz = interfazRepo.findById(interfazId)
                .orElseThrow();

        List<DispositivoInterfaz> relaciones =
                dispositivoInterfazRepo.findByIdInterfazId(interfazId);
        Usuario usu = usuarioRepo.findByUsername(username).orElseThrow(()->new RuntimeException("Usuario no encontrado"));
        List<Log> logs = new ArrayList<Log>();

        for (DispositivoInterfaz rel : relaciones) {

            Dispositivo d = rel.getDispositivo();

            if (!dispositivosIds.contains(d.getId())) continue;

            Map<String, Object> res =
                    fortiGateService.eliminarInterfaz(d, interfaz.getName());

            if (!(Boolean) res.get("success")) {
                throw new RuntimeException("Error eliminando interfaz");
            }

            dispositivoInterfazRepo.delete(rel);

            logs.add(logService.crearLog(usu,d, TipoAccion.ELIMINAR,"Se ha ELIMINADO la Interfaz "+interfaz.getName()));
        }
        logService.guardarTodos(logs);
    }

    @Transactional
    public InterfazDTO editarInterfaz(Long interfazId, CrearInterfazDTO dto, String username) {

        List<DispositivoInterfaz> relaciones =
                dispositivoInterfazRepo.findByIdInterfazId(interfazId);

        if (relaciones.isEmpty()) {
            throw new RuntimeException("No existe relación");
        }

        Interfaz original = relaciones.get(0).getInterfaz();

        List<Long> actuales = relaciones.stream()
                .map(r -> r.getDispositivo().getId())
                .toList();

        List<Long> editar = dto.getDispositivosId();

        if (editar == null || editar.isEmpty()) {
            throw new RuntimeException("Debes enviar dispositivosIds");
        }

        boolean edicionTotal = actuales.containsAll(editar) && editar.containsAll(actuales);
        Usuario usu = usuarioRepo.findByUsername(username).orElseThrow(()->new RuntimeException("Usuario no encontrado"));
        List<Log> logs = new ArrayList<Log>();

        if (edicionTotal) {

            aplicarCambios(original, dto);

            for (DispositivoInterfaz rel : relaciones) {

                Map<String, Object> res =
                        fortiGateService.editarInterfaz(rel.getDispositivo(),original, original.getName());

                if (!(Boolean) res.get("success")) {
                    throw new RuntimeException("Error editando interfaz");
                }
                logs.add(logService.crearLog(usu,rel.getDispositivo(), TipoAccion.EDITAR,"Se ha EDITADO la Interfaz "+original.getName()));
            }
            logService.guardarTodos(logs);
            return toInterfazDTO(interfazRepo.save(original));
        }

        Interfaz nueva = new Interfaz();
        nueva.setName(original.getName());
        nueva.setUsuario(original.getUsuario());

        aplicarCambios(nueva, dto);

        Interfaz nuevaGuardada = interfazRepo.save(nueva);

        for (DispositivoInterfaz rel : relaciones) {

            Long dispId = rel.getDispositivo().getId();

            if (!editar.contains(dispId)) continue;

            Dispositivo d = rel.getDispositivo();

            fortiGateService.eliminarInterfaz(d, original.getName());

            dispositivoInterfazRepo.delete(rel);

            asignarInterfazADispositivos(
                    nuevaGuardada.getId(),
                    List.of(dispId),
                    username
            );
            logs.add(logService.crearLog(usu,rel.getDispositivo(), TipoAccion.EDITAR,"Se ha EDITADO la Interfaz "+original.getName()));
        }

        boolean quedan =
                dispositivoInterfazRepo.existsByIdInterfazId(original.getId());

        if (!quedan) {
            interfazRepo.delete(original);
        }
        logService.guardarTodos(logs);
        return toInterfazDTO(nuevaGuardada);
    }

    private void aplicarCambios(Interfaz interfaz, CrearInterfazDTO dto) {

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
    }

    private InterfazDTO toDTO(Interfaz interfaz, Long dispositivoId) {
        InterfazDTO dto = toInterfazDTO(interfaz);
        dto.setDispositivoId(dispositivoId);
        return dto;
    }

    private InterfazDTO toInterfazDTO(Interfaz i) {
        InterfazDTO dto = new InterfazDTO();
        dto.setId(i.getId());
        dto.setName(i.getName());
        dto.setTipo(i.getTipo());
        dto.setInterfacePadre(i.getInterfacePadre());
        dto.setVlanid(i.getVlanid());
        dto.setVdom(i.getVdom());
        dto.setMode(i.getMode());
        dto.setIp(i.getIp());
        dto.setAllowaccess(i.getAllowaccess());
        dto.setRole(i.getRole());
        dto.setDescription(i.getDescription());
        return dto;
    }

    @Transactional
    public void preeliminarInterfaz(Long id, String username ) {
        Interfaz interfaz = interfazRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Interfaz no encontrada"));
        if (!interfaz.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }
        interfazRepo.delete(interfaz);
    }

    @Transactional
    public InterfazDTO preeditarInterfaz(Long interfazId, CrearInterfazDTO dto, String username) {

        Interfaz interfaz = interfazRepo.findById(interfazId)
                .orElseThrow(() -> new RuntimeException("Address no encontrada"));
        if (!interfaz.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }
        Usuario user = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        aplicarCambios(interfaz, dto);
        Interfaz saved = interfazRepo.save(interfaz);
        return toInterfazDTO(saved);
    }
}

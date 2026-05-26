package com.smartnetwork.backend.Service.firewalls;
import com.smartnetwork.backend.Repository.DispositivoInterfazRepository;
import com.smartnetwork.backend.Repository.UsuarioRepository;
import com.smartnetwork.backend.domain.Entity.DispositivoInterfaz;
import com.smartnetwork.backend.domain.Entity.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.firewalls.InterfazRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.firewalls.Interfaz;
import com.smartnetwork.backend.domain.dtos.firewalls.interfaz.CrearInterfazDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.interfaz.InterfazDTO;


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
            DispositivoRepository dispositivoRepo,
            DispositivoInterfazRepository dispositivoInterfazRepo,
            FortiGateService fortiGateService,
            UsuarioRepository usuarioRepo
    ) {
        this.interfazRepo = interfazRepo;
        this.dispositivoRepo = dispositivoRepo;
        this.dispositivoInterfazRepo = dispositivoInterfazRepo;
        this.fortiGateService = fortiGateService;
        this.usuarioRepo = usuarioRepo;
    }

    // =========================
    // CREATE COMPLETO
    // =========================
    @Transactional
    public InterfazDTO crear(CrearInterfazDTO dto, String username) {

        InterfazDTO interfaz = crearInterfaz(dto, username);

        if (dto.getDispositivosId() != null && !dto.getDispositivosId().isEmpty()) {
            asignarInterfazADispositivos(interfaz.getId(), dto.getDispositivosId(), username);
        }

        return interfaz;
    }

    // =========================
    // CREATE BÁSICO
    // =========================
    public InterfazDTO crearInterfaz(CrearInterfazDTO dto, String username) {

        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Interfaz interfaz = new Interfaz();
        aplicarCambios(interfaz, dto);
        interfaz.setUsuario(usuario);

        return toInterfazDTO(interfazRepo.save(interfaz));
    }

    // =========================
    // ASIGNAR
    // =========================
    @Transactional
    public void asignarInterfazADispositivos(Long interfazId, List<Long> dispositivosIds, String username) {

        Interfaz interfaz = interfazRepo.findById(interfazId)
                .orElseThrow(() -> new RuntimeException("Interfaz no existe"));

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
        }
    }

    // =========================
    // LISTAR
    // =========================
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

    // =========================
    // 🔥 ELIMINAR MULTI
    // =========================
    @Transactional
    public void eliminarInterfaz(Long interfazId, List<Long> dispositivosIds, String username) {

        Interfaz interfaz = interfazRepo.findById(interfazId)
                .orElseThrow();

        List<DispositivoInterfaz> relaciones =
                dispositivoInterfazRepo.findByIdInterfazId(interfazId);

        for (DispositivoInterfaz rel : relaciones) {

            Dispositivo d = rel.getDispositivo();

            if (!dispositivosIds.contains(d.getId())) continue;

            Map<String, Object> res =
                    fortiGateService.eliminarInterfaz(d, interfaz.getName());

            if (!(Boolean) res.get("success")) {
                throw new RuntimeException("Error eliminando interfaz");
            }

            dispositivoInterfazRepo.delete(rel);
        }

        boolean quedan =
                dispositivoInterfazRepo.existsByIdInterfazId(interfazId);

        if (!quedan) {
            interfazRepo.delete(interfaz);
        }
    }

    // =========================
    // 🔥 EDITAR (TOTAL vs PARCIAL)
    // =========================
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

        boolean edicionTotal =
                actuales.containsAll(editar) && editar.containsAll(actuales);

        // =========================
        // 🔵 TOTAL
        // =========================
        if (edicionTotal) {

            aplicarCambios(original, dto);

            for (DispositivoInterfaz rel : relaciones) {

                Map<String, Object> res =
                        fortiGateService.editarInterfaz(rel.getDispositivo(),original, original.getName());

                if (!(Boolean) res.get("success")) {
                    throw new RuntimeException("Error editando interfaz");
                }
            }

            return toInterfazDTO(interfazRepo.save(original));
        }

        // =========================
        // 🔴 PARCIAL
        // =========================
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
        }

        boolean quedan =
                dispositivoInterfazRepo.existsByIdInterfazId(original.getId());

        if (!quedan) {
            interfazRepo.delete(original);
        }

        return toInterfazDTO(nuevaGuardada);
    }

    // =========================
    // HELPERS
    // =========================
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
}

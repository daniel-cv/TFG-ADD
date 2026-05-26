package com.smartnetwork.backend.Service.firewalls;

import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.firewalls.UsuarioFirewall.UsuarioFirewallRepository;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.firewalls.UsuarioFirewall.DispositivoUsuarioFirewall;
import com.smartnetwork.backend.domain.Entity.firewalls.UsuarioFirewall.UsuarioFirewall;
import com.smartnetwork.backend.domain.dtos.firewalls.usuarioFirewall.CreaUsuarioFirewallDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.usuarioFirewall.UsuarioFirewallDTO;
import com.smartnetwork.backend.Repository.firewalls.UsuarioFirewall.DispositivoUsuarioFirewallRepository;
import com.smartnetwork.backend.Repository.UsuarioRepository;
import com.smartnetwork.backend.domain.Entity.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UsuarioFirewallService {

    private final UsuarioFirewallRepository usuarioFirewallRepository;
    private final DispositivoRepository dispositivoRepo;
    private final FortiGateService fortiGateService;
    private final UsuarioRepository usuarioRepository;
    private final DispositivoUsuarioFirewallRepository dispositivoUsuarioFirewallRepository;

    public UsuarioFirewallService(UsuarioFirewallRepository usuarioFirewallRepository, DispositivoRepository dispositivoRepo, FortiGateService fortiGateService, UsuarioRepository usuarioRepository, DispositivoUsuarioFirewallRepository dispositivoUsuarioFirewallRepository){
        this.usuarioFirewallRepository = usuarioFirewallRepository;
        this.dispositivoRepo = dispositivoRepo;
        this.fortiGateService = fortiGateService;
        this.usuarioRepository = usuarioRepository;
        this.dispositivoUsuarioFirewallRepository = dispositivoUsuarioFirewallRepository;
    }

    /**
     * Crear un UsuarioFirewall asociado a un dispositivo
     */
    @Transactional
    public UsuarioFirewallDTO crear(CreaUsuarioFirewallDTO creaUsuarioFirewallDTO, String username){
        UsuarioFirewallDTO usuarioFirewall = crearUsuarioInterfaz(creaUsuarioFirewallDTO, username);

        if(creaUsuarioFirewallDTO.getDispositivosIds() != null && !creaUsuarioFirewallDTO.getDispositivosIds().isEmpty()){
            asignarUsuarioFirewallADispositivo(usuarioFirewall.getId(), creaUsuarioFirewallDTO.getDispositivosIds(), username);
        }

        return usuarioFirewall;
    }

    @Transactional
    public UsuarioFirewallDTO crearUsuarioInterfaz(CreaUsuarioFirewallDTO dto, String username){
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UsuarioFirewall usuarioFirewall = new UsuarioFirewall();
        usuarioFirewall.setNombre(dto.getName());
        usuarioFirewall.setEmail(dto.getEmail());
        usuarioFirewall.setPassword(dto.getPassword());
        usuarioFirewall.setFactor(dto.getTwoFactor());
        usuarioFirewall.setTipo(dto.getType());
        usuarioFirewall.setUsuario(usuario);

        UsuarioFirewall usuarioSaved = usuarioFirewallRepository.save(usuarioFirewall);
        return toUsuarioDTO(usuarioSaved);
    }

    @Transactional
    public void asignarUsuarioFirewallADispositivo(Long usuarioFirewallId, List<Long> dispositivosId, String username){
        UsuarioFirewall usuarioFirewall = usuarioFirewallRepository.findById(usuarioFirewallId)
                .orElseThrow(() -> new RuntimeException("Usuario no existe"));

        if(!usuarioFirewall.getUsuario().getUsername().equals(username)){
            throw new RuntimeException("No autorizado");
        }

        for (Long dispositivoId : dispositivosId){
            Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
                    .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

            if (!dispositivo.getUsuario().getUsername().equals(username)) {
                throw new RuntimeException("No autorizado");
            }

            boolean yaExiste = dispositivoUsuarioFirewallRepository
                    .existsByDispositivoIdAndUsuarioFirewallId(dispositivoId, usuarioFirewallId);

            if (yaExiste) continue;

            Map<String, Object> resultado = fortiGateService.crearUsuarioFirewall(dispositivo, usuarioFirewall);
            if (!(Boolean) resultado.get("success")) {
               throw new RuntimeException("Error creando usuario en FortiGate: " + resultado);
            }

            DispositivoUsuarioFirewall rel = new DispositivoUsuarioFirewall();
            rel.setDispositivo(dispositivo);
            rel.setUsuarioFirewall(usuarioFirewall);
            dispositivoUsuarioFirewallRepository.save(rel);
        }
    }

    public List<UsuarioFirewallDTO> listarPorDispositivo(Long dispositivoId, String username){
        Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return dispositivoUsuarioFirewallRepository.findByDispositivoId(dispositivoId)
                .stream()
                .map(rel -> toDTO(rel.getUsuarioFirewall(), dispositivoId))
                .toList();
    }

    public List<UsuarioFirewallDTO> listarPorUsuario(String username){
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(!usuario.getUsername().equals(username)){
            throw new RuntimeException("No autorizado");
        }

        return usuarioFirewallRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::toUsuarioDTO)
                .toList();
    }

    /**
     * Actualizar un UsuarioFirewall
     */
    @Transactional
    public UsuarioFirewallDTO actualizar(
            Long usuarioFirewallId,
            CreaUsuarioFirewallDTO dto,
            String username
    ) {

        List<DispositivoUsuarioFirewall> relaciones =
                dispositivoUsuarioFirewallRepository
                        .findByUsuarioFirewallId(usuarioFirewallId);

        if (relaciones.isEmpty()) {
            throw new RuntimeException("Relación no encontrada");
        }

        UsuarioFirewall original =
                relaciones.get(0).getUsuarioFirewall();

        for (DispositivoUsuarioFirewall rel : relaciones) {

            if (!rel.getDispositivo()
                    .getUsuario()
                    .getUsername()
                    .equals(username)) {

                throw new RuntimeException("No autorizado");
            }
        }

        List<Long> dispositivosActuales = relaciones.stream()
                .map(rel -> rel.getDispositivo().getId())
                .toList();

        List<Long> dispositivosEditar = dto.getDispositivosIds();

        if (dispositivosEditar == null || dispositivosEditar.isEmpty()) {
            throw new RuntimeException("Debes enviar dispositivosIds");
        }

        boolean edicionTotal =
                dispositivosActuales.containsAll(dispositivosEditar)
                        &&
                        dispositivosEditar.containsAll(dispositivosActuales);

        /* ===================================== */
        /* EDICION TOTAL */
        /* ===================================== */

        if (edicionTotal) {

            String oldName = original.getNombre();

            aplicarCambios(original, dto);

            for (DispositivoUsuarioFirewall rel : relaciones) {

                Dispositivo dispositivo = rel.getDispositivo();

                Map<String, Object> resultado =
                        fortiGateService.editUsuario(
                                dispositivo,
                                original,
                                oldName
                        );

                if (!(Boolean) resultado.get("success")) {
                    throw new RuntimeException(
                            "Error editando usuario: " + resultado
                    );
                }
            }

            UsuarioFirewall saved =
                    usuarioFirewallRepository.save(original);

            return toUsuarioDTO(saved);
        }

        /* ===================================== */
        /* EDICION PARCIAL */
        /* ===================================== */

        UsuarioFirewall nuevo = new UsuarioFirewall();

        nuevo.setNombre(original.getNombre());
        nuevo.setUsuario(original.getUsuario());

        aplicarCambios(nuevo, dto);

        UsuarioFirewall nuevoGuardado =
                usuarioFirewallRepository.save(nuevo);

        for (DispositivoUsuarioFirewall rel : relaciones) {

            Long dispId = rel.getDispositivo().getId();

            if (!dispositivosEditar.contains(dispId)) {
                continue;
            }

            Dispositivo dispositivo = rel.getDispositivo();

            Map<String, Object> eliminar =
                    fortiGateService.eliminarUsuarioFirewall(
                            dispositivo,
                            original.getNombre()
                    );

            if (!(Boolean) eliminar.get("success")) {
                throw new RuntimeException(
                        "Error eliminando usuario antiguo"
                );
            }

            dispositivoUsuarioFirewallRepository.delete(rel);

            asignarUsuarioFirewallADispositivo(
                    nuevoGuardado.getId(),
                    List.of(dispId),
                    username
            );
        }

        boolean quedanRelaciones =
                dispositivoUsuarioFirewallRepository
                        .existsByUsuarioFirewallId(original.getId());

        if (!quedanRelaciones) {
            usuarioFirewallRepository.delete(original);
        }

        return toUsuarioDTO(nuevoGuardado);
    }
    /**
     * Eliminar un UsuarioFirewall
     */
    @Transactional
    public void eliminarUsuarioFirewall(
            Long usuarioFirewallId,
            List<Long> dispositivosIds,
            String username
    ) {

        UsuarioFirewall usuarioFirewall =
                usuarioFirewallRepository.findById(usuarioFirewallId)
                        .orElseThrow(() ->
                                new RuntimeException("Usuario no existe"));

        List<DispositivoUsuarioFirewall> relaciones =
                dispositivoUsuarioFirewallRepository
                        .findByUsuarioFirewallId(usuarioFirewallId);

        if (relaciones.isEmpty()) {
            throw new RuntimeException("No existen relaciones");
        }

        for (DispositivoUsuarioFirewall rel : relaciones) {

            Dispositivo dispositivo = rel.getDispositivo();

            if (!dispositivosIds.contains(dispositivo.getId())) {
                continue;
            }

            if (!dispositivo.getUsuario()
                    .getUsername()
                    .equals(username)) {

                throw new RuntimeException("No autorizado");
            }

            Map<String, Object> resultado =
                    fortiGateService.eliminarUsuarioFirewall(
                            dispositivo,
                            usuarioFirewall.getNombre()
                    );

            if (!(Boolean) resultado.get("success")) {

                throw new RuntimeException(
                        "Error eliminando usuario firewall: "
                                + resultado
                );
            }

            dispositivoUsuarioFirewallRepository.delete(rel);
        }

        boolean quedanRelaciones =
                dispositivoUsuarioFirewallRepository
                        .existsByUsuarioFirewallId(usuarioFirewallId);

        if (!quedanRelaciones) {
            usuarioFirewallRepository.delete(usuarioFirewall);
        }
    }

    private UsuarioFirewallDTO toDTO(UsuarioFirewall usuario, Long dispositivoId) {

        UsuarioFirewallDTO dto = new UsuarioFirewallDTO();

        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setTipo(usuario.getTipo());
        dto.setPassword(usuario.getPassword());
        dto.setEmail(usuario.getEmail());
        dto.setDispositivoId(dispositivoId);

        return dto;
    }

    private UsuarioFirewallDTO toUsuarioDTO(UsuarioFirewall usuario) {
        UsuarioFirewallDTO dto = new UsuarioFirewallDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setTipo(usuario.getTipo());
        dto.setPassword(usuario.getPassword());
        dto.setEmail(usuario.getEmail());

        return dto;
    }

    private void aplicarCambios(
            UsuarioFirewall usuario,
            CreaUsuarioFirewallDTO dto
    ) {

        usuario.setNombre(dto.getName());
        usuario.setEmail(dto.getEmail());
        usuario.setTipo(dto.getType());
        usuario.setFactor(dto.getTwoFactor());

        if (dto.getPassword() != null &&
                !dto.getPassword().isBlank()) {

            usuario.setPassword(dto.getPassword());
        }
    }
}


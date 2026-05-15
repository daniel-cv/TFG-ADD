package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.DispositivoReglaFirewallRepository;
import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.ReglaFirewallRepository;
import com.smartnetwork.backend.Repository.UsuarioRepository;

import com.smartnetwork.backend.domain.Entity.*;

import com.smartnetwork.backend.domain.dtos.Policys.CrearReglaFirewallDTO;
import com.smartnetwork.backend.domain.dtos.Policys.ReglaFirewallDTO;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReglaFirewallService {

    private final ReglaFirewallRepository reglaRepo;

    private final DispositivoRepository dispositivoRepo;

    private final FortiGateService fortiGateService;

    private final UsuarioRepository usuarioRepository;

    private final DispositivoReglaFirewallRepository
            dispositivoReglaFirewallRepository;

    public ReglaFirewallService(

            ReglaFirewallRepository reglaRepo,

            DispositivoRepository dispositivoRepo,

            FortiGateService fortiGateService,

            UsuarioRepository usuarioRepository,

            DispositivoReglaFirewallRepository
                    dispositivoReglaFirewallRepository

    ) {

        this.reglaRepo = reglaRepo;

        this.dispositivoRepo = dispositivoRepo;

        this.fortiGateService = fortiGateService;

        this.usuarioRepository = usuarioRepository;

        this.dispositivoReglaFirewallRepository =
                dispositivoReglaFirewallRepository;
    }

    /* ===================================================== */
    /* CREATE */
    /* ===================================================== */

    @Transactional
    public ReglaFirewallDTO crear(
            CrearReglaFirewallDTO dto,
            String username
    ) {

        ReglaFirewallDTO reglaFirewall =
                crearReglaFirewall(dto, username);

        if (
                dto.getDispositivosId() != null
                        &&
                        !dto.getDispositivosId().isEmpty()
        ) {

            asignarReglaFirewallADispositivos(
                    reglaFirewall.getId(),
                    dto.getDispositivosId(),
                    username
            );
        }

        return reglaFirewall;
    }

    @Transactional
    public ReglaFirewallDTO crearReglaFirewall(
            CrearReglaFirewallDTO dto,
            String username
    ) {

        Usuario usuario =
                usuarioRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Usuario no encontrado"
                                )
                        );

        ReglaFirewall regla =
                new ReglaFirewall();

        regla.setNombre(dto.getNombre());

        regla.setOrigen(dto.getOrigen());

        regla.setDestino(dto.getDestino());

        regla.setIporigen(dto.getIpOrigen());

        regla.setIpdestino(dto.getIpDestino());

        regla.setServicio(dto.getServicio());

        regla.setAction(dto.getAction());

        regla.setNat(dto.getNat());

        regla.setHabilitada(true);

        regla.setUsuario(usuario);

        ReglaFirewall reglaFirewall =
                reglaRepo.save(regla);

        return toReglaFirewallDTO(reglaFirewall);
    }

    /* ===================================================== */
    /* APPLY */
    /* ===================================================== */

    @Transactional
    public void asignarReglaFirewallADispositivos(

            Long reglaFirewallId,

            List<Long> dispositivosId,

            String username

    ) {

        ReglaFirewall reglaFirewall =
                reglaRepo.findById(reglaFirewallId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "ReglaFirewall no encontrada"
                                )
                        );

        if (
                !reglaFirewall.getUsuario()
                        .getUsername()
                        .equals(username)
        ) {

            throw new RuntimeException(
                    "No autorizado"
            );
        }

        for (Long dispositivoId : dispositivosId) {

            Dispositivo dispositivo =
                    dispositivoRepo.findById(dispositivoId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Dispositivo no encontrado"
                                    )
                            );

            if (
                    !dispositivo.getUsuario()
                            .getUsername()
                            .equals(username)
            ) {

                throw new RuntimeException(
                        "No autorizado"
                );
            }

            boolean yaExiste =
                    dispositivoReglaFirewallRepository
                            .existsByDispositivoIdAndReglaFirewallId(
                                    dispositivoId,
                                    reglaFirewallId
                            );

            if (yaExiste) {
                continue;
            }

            Map<String, Object> resultado =
                    fortiGateService.crearPolicy(
                            dispositivo,
                            reglaFirewall
                    );

            if (!(Boolean)
                    resultado.get("success")) {

                throw new RuntimeException(
                        "Error creando policy en FortiGate"
                );
            }

            DispositivoReglaFirewall rel =
                    new DispositivoReglaFirewall();

            rel.setDispositivo(dispositivo);

            rel.setReglaFirewall(reglaFirewall);

            dispositivoReglaFirewallRepository
                    .save(rel);
        }
    }

    /* ===================================================== */
    /* GET DEVICE */
    /* ===================================================== */

    public List<ReglaFirewallDTO>
    listarPorDispositivo(
            Long dispositivoId,
            String username
    ) {

        Dispositivo dispositivo =
                dispositivoRepo.findById(dispositivoId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dispositivo no encontrado"
                                )
                        );

        if (
                !dispositivo.getUsuario()
                        .getUsername()
                        .equals(username)
        ) {

            throw new RuntimeException(
                    "No autorizado"
            );
        }

        return dispositivoReglaFirewallRepository
                .findByDispositivoId(dispositivoId)
                .stream()
                .map(rel ->
                        toDTO(
                                rel.getReglaFirewall(),
                                dispositivoId
                        )
                )
                .toList();
    }

    /* ===================================================== */
    /* GET USER */
    /* ===================================================== */

    public List<ReglaFirewallDTO>
    listarPorUsario(String username) {

        Usuario usuario =
                usuarioRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Usuario no encontrado"
                                )
                        );

        return reglaRepo.findByUsuarioId(
                        usuario.getId()
                )
                .stream()
                .map(this::toReglaFirewallDTO)
                .toList();
    }

    /* ===================================================== */
    /* DELETE */
    /* ===================================================== */

    @Transactional
    public void eliminarRegla(

            Long reglaId,

            List<Long> dispositivosIds,

            String username

    ) {

        ReglaFirewall regla =
                reglaRepo.findById(reglaId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Regla no existe"
                                )
                        );

        List<DispositivoReglaFirewall>
                relaciones =
                dispositivoReglaFirewallRepository
                        .findByReglaFirewallId(
                                reglaId
                        );

        if (relaciones.isEmpty()) {

            throw new RuntimeException(
                    "Relacion no encontrada"
            );
        }

        for (
                DispositivoReglaFirewall relacion
                : relaciones
        ) {

            Dispositivo dispositivo =
                    relacion.getDispositivo();

            if (
                    !dispositivosIds.contains(
                            dispositivo.getId()
                    )
            ) {

                continue;
            }

            if (
                    !dispositivo.getUsuario()
                            .getUsername()
                            .equals(username)
            ) {

                throw new RuntimeException(
                        "No autorizado"
                );
            }

            Map<String, Object> resultado =
                    fortiGateService
                            .eliminarReglaFirewall(
                                    dispositivo,
                                    regla.getNombre()
                            );

            if (!(Boolean)
                    resultado.get("success")) {

                throw new RuntimeException(
                        "Error eliminando policy"
                );
            }

            dispositivoReglaFirewallRepository
                    .delete(relacion);
        }

        boolean quedanRelaciones =
                dispositivoReglaFirewallRepository
                        .existsByReglaFirewallId(
                                reglaId
                        );

        if (!quedanRelaciones) {

            reglaRepo.delete(regla);
        }
    }

    /* ===================================================== */
    /* EDIT */
    /* ===================================================== */

    @Transactional
    public ReglaFirewallDTO editarReglaFirewall(

            Long id,

            CrearReglaFirewallDTO dto,

            String username

    ) {

        List<DispositivoReglaFirewall>
                relaciones =
                dispositivoReglaFirewallRepository
                        .findByReglaFirewallId(id);

        if (relaciones.isEmpty()) {

            throw new RuntimeException(
                    "Relacion no encontrada"
            );
        }

        ReglaFirewall original =
                relaciones.get(0)
                        .getReglaFirewall();

        for (
                DispositivoReglaFirewall relacion
                : relaciones
        ) {

            if (
                    !relacion.getDispositivo()
                            .getUsuario()
                            .getUsername()
                            .equals(username)
            ) {

                throw new RuntimeException(
                        "No autorizado"
                );
            }
        }

        List<Long> dispositivosActuales =
                relaciones.stream()
                        .map(rel ->
                                rel.getDispositivo()
                                        .getId()
                        )
                        .toList();

        List<Long> dispositivosEditar =
                dto.getDispositivosId();

        boolean edicionTotal =
                dispositivosActuales.containsAll(
                        dispositivosEditar
                )
                        &&
                        dispositivosEditar.containsAll(
                                dispositivosActuales
                        );

        /* ===================================================== */
        /* EDICION TOTAL */
        /* ===================================================== */

        if (edicionTotal) {

            aplicarCambios(original, dto);

            for (
                    DispositivoReglaFirewall relacion
                    : relaciones
            ) {

                Dispositivo dispositivo =
                        relacion.getDispositivo();

                Map<String, Object> resultado =
                        fortiGateService.editarPolicy(
                                dispositivo,
                                original
                        );

                if (!(Boolean)
                        resultado.get("success")) {

                    throw new RuntimeException(
                            "Error editando policy"
                    );
                }
            }

            ReglaFirewall saved =
                    reglaRepo.save(original);

            return toReglaFirewallDTO(saved);
        }

        /* ===================================================== */
        /* EDICION PARCIAL */
        /* ===================================================== */

        ReglaFirewall nueva =
                new ReglaFirewall();

        nueva.setNombre(
                original.getNombre()
        );

        nueva.setUsuario(
                original.getUsuario()
        );

        nueva.setHabilitada(true);

        aplicarCambios(nueva, dto);

        ReglaFirewall nuevaGuardada =
                reglaRepo.save(nueva);

        for (
                DispositivoReglaFirewall relacion
                : relaciones
        ) {

            Long dispositivoId =
                    relacion.getDispositivo()
                            .getId();

            if (
                    !dispositivosEditar
                            .contains(dispositivoId)
            ) {

                continue;
            }

            Dispositivo dispositivo =
                    relacion.getDispositivo();

            fortiGateService.eliminarReglaFirewall(
                    dispositivo,
                    original.getNombre()
            );

            dispositivoReglaFirewallRepository
                    .delete(relacion);

            asignarReglaFirewallADispositivos(

                    nuevaGuardada.getId(),

                    List.of(dispositivoId),

                    username
            );
        }

        boolean quedanRelaciones =
                dispositivoReglaFirewallRepository
                        .existsByReglaFirewallId(
                                original.getId()
                        );

        if (!quedanRelaciones) {

            reglaRepo.delete(original);
        }

        return toReglaFirewallDTO(
                nuevaGuardada
        );
    }

    /* ===================================================== */
    /* HELPERS */
    /* ===================================================== */

    private void aplicarCambios(

            ReglaFirewall regla,

            CrearReglaFirewallDTO dto

    ) {

        regla.setNombre(dto.getNombre());

        regla.setOrigen(dto.getOrigen());

        regla.setDestino(dto.getDestino());

        regla.setIporigen(dto.getIpOrigen());

        regla.setIpdestino(dto.getIpDestino());

        regla.setServicio(dto.getServicio());

        regla.setNat(dto.getNat());

        regla.setAction(dto.getAction());

        regla.setHabilitada(true);
    }

    /* ===================================================== */
    /* DTO */
    /* ===================================================== */

    private ReglaFirewallDTO toDTO(
            ReglaFirewall regla,
            Long dispositivoId
    ) {

        ReglaFirewallDTO dto =
                new ReglaFirewallDTO();

        dto.setId(regla.getId());

        dto.setNombre(regla.getNombre());

        dto.setOrigen(regla.getOrigen());

        dto.setDestino(regla.getDestino());

        dto.setIpOrigen(regla.getIporigen());

        dto.setIpDestino(regla.getIpdestino());

        dto.setServicio(regla.getServicio());

        dto.setHabilitada(
                regla.isHabilitada()
        );

        dto.setNat(regla.getNat());

        dto.setAction(regla.getAction());

        dto.setDispositivoId(dispositivoId);

        return dto;
    }

    private ReglaFirewallDTO
    toReglaFirewallDTO(
            ReglaFirewall regla
    ) {

        ReglaFirewallDTO dto =
                new ReglaFirewallDTO();

        dto.setId(regla.getId());

        dto.setNombre(regla.getNombre());

        dto.setOrigen(regla.getOrigen());

        dto.setDestino(regla.getDestino());

        dto.setIpOrigen(regla.getIporigen());

        dto.setIpDestino(regla.getIpdestino());

        dto.setServicio(regla.getServicio());

        dto.setHabilitada(
                regla.isHabilitada()
        );

        dto.setNat(regla.getNat());

        dto.setAction(regla.getAction());

        return dto;
    }
}
package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.*;
import com.smartnetwork.backend.Service.FortiGateService;
import com.smartnetwork.backend.domain.Entity.*;
import com.smartnetwork.backend.domain.dtos.address.AddressDTO;
import com.smartnetwork.backend.domain.dtos.address.CrearAddressDTO;
import jakarta.transaction.Transactional;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepo;
    private final InterfazRepository interfazRepo;
    private final DispositivoRepository dispositivoRepo;
    private final FortiGateService fortiGateService;
    private final DispositivoAddressRepository  dispositivoAddressRepo;
    private final UsuarioRepository usuarioRepository;

    public AddressService(
            AddressRepository addressRepo,
            InterfazRepository interfazRepo,
            DispositivoRepository dispositivoRepo, FortiGateService fortiGateService, DispositivoAddressRepository dispositivoAddressRepo, UsuarioRepository usuarioRepository
    ) {
        this.addressRepo = addressRepo;
        this.interfazRepo = interfazRepo;
        this.dispositivoRepo = dispositivoRepo;
        this.fortiGateService = fortiGateService;
        this.dispositivoAddressRepo = dispositivoAddressRepo;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public AddressDTO crear(CrearAddressDTO dto, String username) {

        AddressDTO address = crearAddress(dto, username);

        if (dto.getDispositivosIds() != null && !dto.getDispositivosIds().isEmpty()) {
            asignarAddressADispositivos(address.getId(), dto.getDispositivosIds(), username);
        }

        return address;
    }

    @Transactional
    public AddressDTO crearAddress(CrearAddressDTO dto, String username) {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Address address = new Address();
        address.setName(dto.getName());
        address.setType(dto.getType());
        address.setIp(dto.getIp());
        address.setComentario(dto.getComentario());

        if (dto.getIpdestino() != null && !dto.getIpdestino().isBlank()) {
            address.setIpdestino(dto.getIpdestino().trim());
        }

        // --- Lógica unificada para Interfaz ---
        if (dto.getInterfazId() != null) {
            if (dto.getInterfazId() < 0) {
                // Manejo de puertos por defecto (Igual que en editarAddress)
                String nombrePort = switch (dto.getInterfazId().intValue()) {
                    case -1 -> "port1";
                    case -2 -> "port2";
                    case -3 -> "port3";
                    case -4 -> "port4";
                    default -> throw new RuntimeException("Puerto default no soportado");
                };

                Interfaz interfaz = interfazRepo.findByName(nombrePort)
                        .orElseGet(() -> {
                            Interfaz nueva = new Interfaz();
                            nueva.setName(nombrePort);
                            nueva.setTipo("Default");
                            nueva.setUsuario(usuario);
                            return interfazRepo.save(nueva);
                        });
                address.setInterfaz(interfaz);
            } else {
                // Manejo de interfaces personalizadas por ID
                Interfaz interfaz = interfazRepo.findById(dto.getInterfazId())
                        .orElseThrow(() -> new RuntimeException("Interfaz no existe"));
                address.setInterfaz(interfaz);
            }
        }

        address.setUsuario(usuario);

        Address saved = addressRepo.save(address);
        return toAddressDTO(saved);
    }

    @Transactional
    public void asignarAddressADispositivos(Long addressId, List<Long> dispositivosIds, String username) {

        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address no existe"));

        if (!address.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        for (Long dispositivoId : dispositivosIds) {

            Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
                    .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

            if (!dispositivo.getUsuario().getUsername().equals(username)) {
                throw new RuntimeException("No autorizado");
            }

            boolean yaExiste = dispositivoAddressRepo
                    .existsByDispositivoIdAndAddressId(dispositivoId, addressId);

            if (yaExiste) continue;


            Map<String, Object> resultado = fortiGateService.crearAddress(dispositivo, address);
            if (!(Boolean) resultado.get("success")) {
                throw new RuntimeException("Error creando address en FortiGate: " + resultado);
            }


            DispositivoAddress rel = new DispositivoAddress();
            rel.setDispositivo(dispositivo);
            rel.setAddress(address);
            rel.setComentario(address.getComentario());

            dispositivoAddressRepo.save(rel);
        }
    }


    public List<AddressDTO> listarPorDispositivo(Long dispositivoId, String username) {

        Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return dispositivoAddressRepo.findByDispositivoId(dispositivoId)
                .stream()
                .map(rel -> toDTO(rel.getAddress(), dispositivoId))
                .toList();
    }

    public List<AddressDTO> listarPorUsuario(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(!usuario.getUsername().equals(username)){
            throw new RuntimeException("No autorizado");
        }

        return addressRepo.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::toAddressDTO)
                .toList();
    }

    private AddressDTO toDTO(Address address, Long dispositivoId) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setName(address.getName());
        dto.setType(address.getType());
        dto.setIp(address.getIp());
        dto.setIpdestino(address.getIpdestino());
        dto.setComentario(address.getComentario());
        dto.setDispositivoId(dispositivoId);

        if (address.getInterfaz() != null) {
            dto.setInterfazId(address.getInterfaz().getId());
        }

        return dto;
    }

    private AddressDTO toAddressDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setName(address.getName());
        dto.setType(address.getType());
        dto.setIp(address.getIp());
        dto.setIpdestino(address.getIpdestino());
        dto.setComentario(address.getComentario());

        if (address.getInterfaz() != null) {
            dto.setInterfazId(address.getInterfaz().getId());
        }

        return dto;
    }

    @Transactional
    public void eliminarAddress(Long addressId, List<Long> dispositivosIds, String username) {

        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address no existe"));

        List<DispositivoAddress> relaciones = dispositivoAddressRepo
                .findAllByAddressId(addressId);

        if (relaciones.isEmpty()) {
            throw new RuntimeException("No existen relaciones para este Address");
        }

        // 🔥 Relaciones que realmente se eliminarán
        List<DispositivoAddress> relacionesABorrar = relaciones.stream()
                .filter(rel -> dispositivosIds.contains(rel.getDispositivo().getId()))
                .toList();

        if (relacionesABorrar.isEmpty()) {
            throw new RuntimeException("No hay dispositivos válidos para eliminar");
        }

        // 🔥 Validar y eliminar en FortiGate
        for (DispositivoAddress rel : relacionesABorrar) {

            Dispositivo dispositivo = rel.getDispositivo();

            if (!dispositivo.getUsuario().getUsername().equals(username)) {
                throw new RuntimeException("No autorizado");
            }

            Map<String, Object> resultado = fortiGateService
                    .eliminarAddress(dispositivo, address.getName());

            if (!(Boolean) resultado.get("success")) {
                throw new RuntimeException(
                        "Error eliminando Address en FortiGate (" +
                                dispositivo.getNombre() + "): " + resultado
                );
            }
        }

        // 🔥 Borrar relaciones SOLO después del loop
        dispositivoAddressRepo.deleteAll(relacionesABorrar);

        // 🔥 Forzar sincronización con BD
        dispositivoAddressRepo.flush();

        // 🔥 Borrar Address solo si ya no tiene relaciones
        boolean quedanRelaciones = dispositivoAddressRepo
                .existsByAddressId(addressId);

        if (!quedanRelaciones) {
            addressRepo.delete(address);
        }
    }

    @Transactional
    public AddressDTO editarAddress(Long addressId, CrearAddressDTO dto, String username) {

        List<DispositivoAddress> relaciones = dispositivoAddressRepo
                .findAllByAddressId(addressId);

        if (relaciones.isEmpty()) {
            throw new RuntimeException("Relación no encontrada");
        }

        Address original = relaciones.get(0).getAddress();

        // 🔒 validar usuario
        for (DispositivoAddress rel : relaciones) {
            if (!rel.getDispositivo().getUsuario().getUsername().equals(username)) {
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

        boolean edicionTotal = dispositivosActuales.containsAll(dispositivosEditar)
                && dispositivosEditar.containsAll(dispositivosActuales);

        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // =========================
        // 🔵 EDICIÓN TOTAL
        // =========================
        if (edicionTotal) {

            aplicarCambiosAddress(original, dto, user);

            for (DispositivoAddress rel : relaciones) {

                Dispositivo dispositivo = rel.getDispositivo();

                Map<String, Object> resultado = fortiGateService
                        .editarAddress(dispositivo, original, original.getName());

                if (!(Boolean) resultado.get("success")) {
                    throw new RuntimeException(
                            "Error editando Address en FortiGate (" + dispositivo.getNombre() + "): " + resultado
                    );
                }
            }

            Address saved = addressRepo.save(original);
            return toAddressDTO(saved);
        }

        // =========================
        // 🔴 EDICIÓN PARCIAL
        // =========================

        // 1. Crear nueva address
        Address nueva = new Address();
        nueva.setName(original.getName());
        nueva.setUsuario(original.getUsuario());

        aplicarCambiosAddress(nueva, dto, user);

        Address nuevaGuardada = addressRepo.save(nueva);

        // 2. Procesar SOLO los dispositivos seleccionados
        for (DispositivoAddress rel : relaciones) {

            Long dispId = rel.getDispositivo().getId();

            if (!dispositivosEditar.contains(dispId)) continue;

            Dispositivo dispositivo = rel.getDispositivo();

            // 🔥 1. eliminar antigua en Fortigate
            Map<String, Object> eliminar = fortiGateService
                    .eliminarAddress(dispositivo, original.getName());

            if (!(Boolean) eliminar.get("success")) {
                throw new RuntimeException("Error eliminando address antigua en Fortigate");
            }

            // 🔥 2. eliminar relación BD
            dispositivoAddressRepo.delete(rel);

            // 🔥 3. aplicar nueva correctamente (USANDO TU LÓGICA BUENA)
            asignarAddressADispositivos(
                    nuevaGuardada.getId(),
                    List.of(dispId),
                    username
            );
        }

        // 🔥 3. limpiar original si ya no se usa
        boolean quedanRelaciones = dispositivoAddressRepo
                .existsByAddressId(original.getId());

        if (!quedanRelaciones) {
            addressRepo.delete(original);
        }

        return toAddressDTO(nuevaGuardada);
    }

    private void aplicarCambiosAddress(Address address, CrearAddressDTO dto, Usuario user) {

        address.setType(dto.getType());
        address.setIp(dto.getIp());
        address.setIpdestino(dto.getIpdestino() != null ? dto.getIpdestino().trim() : null);
        address.setComentario(dto.getComentario());

        if (dto.getInterfazId() != null) {

            if (dto.getInterfazId() < 0) {

                String nombrePort = switch (dto.getInterfazId().intValue()) {
                    case -1 -> "port1";
                    case -2 -> "port2";
                    case -3 -> "port3";
                    case -4 -> "port4";
                    default -> throw new RuntimeException("Puerto default no soportado");
                };

                Interfaz interfaz = interfazRepo
                        .findByName(nombrePort)
                        .orElseGet(() -> {
                            Interfaz nueva = new Interfaz();
                            nueva.setName(nombrePort);
                            nueva.setTipo("Default");
                            nueva.setUsuario(user);
                            return interfazRepo.save(nueva);
                        });

                address.setInterfaz(interfaz);

            } else {

                Interfaz interfaz = interfazRepo.findById(dto.getInterfazId())
                        .orElseThrow(() -> new RuntimeException("Interfaz no existe"));

                address.setInterfaz(interfaz);
            }

        } else {
            address.setInterfaz(null);
        }
    }
}

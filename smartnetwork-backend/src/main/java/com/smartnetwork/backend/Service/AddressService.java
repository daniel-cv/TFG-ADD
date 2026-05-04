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

        for (DispositivoAddress rel : relaciones) {

            Dispositivo dispositivo = rel.getDispositivo();

            // Solo eliminar de los dispositivos seleccionados
            if (!dispositivosIds.contains(dispositivo.getId())) {
                continue;
            }

            if (!dispositivo.getUsuario().getUsername().equals(username)) {
                throw new RuntimeException("No autorizado");
            }

            Map<String, Object> resultado = fortiGateService
                    .eliminarAddress(dispositivo, address.getName());

            if (!(Boolean) resultado.get("success")) {
                throw new RuntimeException("Error eliminando Address en FortiGate: " + resultado);
            }

            dispositivoAddressRepo.delete(rel);
        }

        // 🔥 IMPORTANTE: solo borrar Address si ya no tiene relaciones
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

        // El address es el mismo para todas las relaciones
        Address address = relaciones.get(0).getAddress();

        // =========================
        // VALIDAR USUARIO EN TODOS
        // =========================
        for (DispositivoAddress rel : relaciones) {
            if (!rel.getDispositivo().getUsuario().getUsername().equals(username)) {
                throw new RuntimeException("No autorizado");
            }
        }
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        address.setType(dto.getType());
        address.setIp(dto.getIp());
        address.setIpdestino(dto.getIpdestino() != null ? dto.getIpdestino().trim() : null);
        address.setComentario(dto.getComentario());

        Dispositivo dispositivoRef = relaciones.get(0).getDispositivo();

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

        // =========================
        // ACTUALIZAR EN FORTIGATE (TODOS LOS DISPOSITIVOS)
        // =========================
        for (DispositivoAddress rel : relaciones) {

            Dispositivo dispositivo = rel.getDispositivo();

            Map<String, Object> resultado = fortiGateService
                    .editarAddress(dispositivo, address, address.getName());

            if (!(Boolean) resultado.get("success")) {
                throw new RuntimeException(
                        "Error editando Address en FortiGate (" + dispositivo.getNombre() + "): " + resultado
                );
            }
        }

        // =========================
        // GUARDAR EN BD
        // =========================
        Address saved = addressRepo.save(address);

        return toDTO(saved, dispositivoRef.getId());
    }
}

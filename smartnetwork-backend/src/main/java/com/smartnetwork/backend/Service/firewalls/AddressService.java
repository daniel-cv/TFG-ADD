package com.smartnetwork.backend.Service.firewalls;

import com.smartnetwork.backend.Repository.firewalls.Address.AddressRepository;
import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.firewalls.Address.DispositivoAddressRepository;
import com.smartnetwork.backend.Repository.firewalls.Interfaz.InterfazRepository;
import com.smartnetwork.backend.Service.LogService;
import com.smartnetwork.backend.domain.Entity.firewalls.Address.Address;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.firewalls.Address.DispositivoAddress;
import com.smartnetwork.backend.domain.Entity.firewalls.Interfaz.Interfaz;
import com.smartnetwork.backend.domain.Enum.TipoAccion;
import com.smartnetwork.backend.domain.dtos.firewalls.address.AddressDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.address.CrearAddressDTO;
import com.smartnetwork.backend.Repository.*;
import com.smartnetwork.backend.domain.Entity.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AddressService {

    private final AddressRepository addressRepo;
    private final InterfazRepository interfazRepo;
    private final DispositivoRepository dispositivoRepo;
    private final FortiGateService fortiGateService;
    private final DispositivoAddressRepository dispositivoAddressRepo;
    private final UsuarioRepository usuarioRepository;
    private final LogService logService;

    public AddressService(
            AddressRepository addressRepo,
            InterfazRepository interfazRepo,
            DispositivoRepository dispositivoRepo, FortiGateService fortiGateService, DispositivoAddressRepository dispositivoAddressRepo, UsuarioRepository usuarioRepository, LogService logService
    ) {
        this.addressRepo = addressRepo;
        this.interfazRepo = interfazRepo;
        this.dispositivoRepo = dispositivoRepo;
        this.fortiGateService = fortiGateService;
        this.dispositivoAddressRepo = dispositivoAddressRepo;
        this.usuarioRepository = usuarioRepository;
        this.logService = logService;
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

        if (dto.getInterfazId() != null) {
            if (dto.getInterfazId() < 0) {
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

        Usuario usu = usuarioRepository.findByUsername(username).orElseThrow(()->new RuntimeException("Usuario no encontrado"));
        List<Log> logs = new ArrayList<Log>();
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

            logs.add(logService.crearLog(usu,dispositivo, TipoAccion.CREAR,"Se ha CREADO el Address "+address.getName()));
        }
        logService.guardarTodos(logs);
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

        List<DispositivoAddress> relacionesABorrar = relaciones.stream()
                .filter(rel -> dispositivosIds.contains(rel.getDispositivo().getId()))
                .toList();

        if (relacionesABorrar.isEmpty()) {
            throw new RuntimeException("No hay dispositivos válidos para eliminar");
        }
        Usuario usu = usuarioRepository.findByUsername(username).orElseThrow(()->new RuntimeException("Usuario no encontrado"));
        List<Log> logs = new ArrayList<Log>();
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
            logs.add(logService.crearLog(usu,dispositivo, TipoAccion.ELIMINAR,"Se ha ELIMINADO el Address "+address.getName()));
        }

        dispositivoAddressRepo.deleteAll(relacionesABorrar);
        dispositivoAddressRepo.flush();
        logService.guardarTodos(logs);
    }

    @Transactional
    public AddressDTO editarAddress(Long addressId, CrearAddressDTO dto, String username) {

        List<DispositivoAddress> relaciones = dispositivoAddressRepo
                .findAllByAddressId(addressId);

        if (relaciones.isEmpty()) {
            throw new RuntimeException("Relación no encontrada");
        }

        Address original = relaciones.get(0).getAddress();

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
        List<Log> logs = new ArrayList<Log>();

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
                logs.add(logService.crearLog(user,dispositivo, TipoAccion.EDITAR,"Se ha EDITADO el Address "+original.getName()));
            }

            Address saved = addressRepo.save(original);
            logService.guardarTodos(logs);
            return toAddressDTO(saved);
        }

        Address nueva = new Address();
        nueva.setName(original.getName());
        nueva.setUsuario(original.getUsuario());

        aplicarCambiosAddress(nueva, dto, user);

        Address nuevaGuardada = addressRepo.save(nueva);

        for (DispositivoAddress rel : relaciones) {

            Long dispId = rel.getDispositivo().getId();

            if (!dispositivosEditar.contains(dispId)) continue;

            Dispositivo dispositivo = rel.getDispositivo();

            Map<String, Object> eliminar = fortiGateService
                    .eliminarAddress(dispositivo, original.getName());

            if (!(Boolean) eliminar.get("success")) {
                throw new RuntimeException("Error eliminando address antigua en Fortigate");
            }

            dispositivoAddressRepo.delete(rel);

            asignarAddressADispositivos(
                    nuevaGuardada.getId(),
                    List.of(dispId),
                    username
            );
            logs.add(logService.crearLog(user,dispositivo, TipoAccion.EDITAR,"Se ha CREADO el Address "+original.getName()));
        }

        boolean quedanRelaciones = dispositivoAddressRepo
                .existsByAddressId(original.getId());

        if (!quedanRelaciones) {
            addressRepo.delete(original);
        }
        logService.guardarTodos(logs);
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

package com.smartnetwork.backend.Service.firewalls;

import com.smartnetwork.backend.Repository.firewalls.AddressRepository;
import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.firewalls.InterfazRepository;
import com.smartnetwork.backend.domain.Entity.firewalls.Address;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.firewalls.Interfaz;
import com.smartnetwork.backend.domain.dtos.firewalls.address.AddressDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.address.CrearAddressDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AddressService {

    private final AddressRepository addressRepo;
    private final InterfazRepository interfazRepo;
    private final DispositivoRepository dispositivoRepo;
    private final FortiGateService fortiGateService;
    private final String auxiliar = null;

    public AddressService(
            AddressRepository addressRepo,
            InterfazRepository interfazRepo,
            DispositivoRepository dispositivoRepo, FortiGateService fortiGateService
    ) {
        this.addressRepo = addressRepo;
        this.interfazRepo = interfazRepo;
        this.dispositivoRepo = dispositivoRepo;
        this.fortiGateService = fortiGateService;
    }

    public AddressDTO crear(CrearAddressDTO dto, String username) {
        if (dto.getDispositivoId() == null) throw new RuntimeException("dispositivoId obligatorio");

        Dispositivo dispositivo = dispositivoRepo.findById(dto.getDispositivoId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) throw new RuntimeException("No autorizado");

        Address address = new Address();
        address.setName(dto.getName());
        address.setType(dto.getType());
        address.setIp(dto.getIp());
        address.setIpdestino(dto.getIpdestino() != null ? dto.getIpdestino().trim() : null);
        address.setComentario(dto.getComentario());
        address.setDispositivo(dispositivo);

        // Lógica de Interfaz: Buscar o Crear antes de asignar
        if (dto.getInterfazId() != null) {
            if (dto.getInterfazId() < 0) {
                String nombrePort = switch (dto.getInterfazId().intValue()) {
                    case -1 -> "port1";
                    case -2 -> "port2";
                    case -3 -> "port3";
                    case -4 -> "port4";
                    default -> throw new RuntimeException("Puerto default no soportado");
                };

                // BUSCAMOS primero. Si no existe, CREAMOS y GUARDAMOS en la DB.
                Interfaz interfaz = interfazRepo.findByNameAndDispositivoId(nombrePort, dispositivo.getId())
                        .orElseGet(() -> {
                            Interfaz nueva = new Interfaz();
                            nueva.setName(nombrePort);
                            nueva.setTipo("Default");
                            nueva.setDispositivo(dispositivo);
                            return interfazRepo.save(nueva); // <--- ESTO evita el error de Transient
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

        // FortiGate
        Map<String, Object> resultado = fortiGateService.crearAddress(dispositivo, address);
        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException("Error en FortiGate: " + resultado);
        }

        // Guardar Address final
        return toDTO(addressRepo.save(address));
    }

    public List<AddressDTO> listarPorDispositivo(Long dispositivoId, String username) {

        Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return addressRepo.findByDispositivoId(dispositivoId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private AddressDTO toDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setName(address.getName());
        dto.setType(address.getType());
        dto.setIp(address.getIp());
        dto.setIpdestino(address.getIpdestino()); // <-- línea nueva
        dto.setComentario(address.getComentario());
        dto.setDispositivoId(address.getDispositivo().getId());
        if(address.getInterfaz() != null) {
            dto.setInterfazId(address.getInterfaz().getId());
        }
        return dto;
    }

    public void eliminarAddress(Long addressId, String username) {
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address no existe"));

        if (!address.getDispositivo().getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        Map<String, Object> resultado = fortiGateService.eliminarAddress(address.getDispositivo(), address.getName());

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException("Error eliminando Address en FortiGate: " + resultado);
        }

        addressRepo.delete(address);
    }

    public AddressDTO editarAddress(Long addressId, CrearAddressDTO dto, String username) {
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address no existe"));

        if (!address.getDispositivo().getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        Dispositivo dispositivo = address.getDispositivo();

        // Actualización de campos básicos
        address.setType(dto.getType());
        address.setIp(dto.getIp());
        address.setIpdestino(dto.getIpdestino() != null ? dto.getIpdestino().trim() : null);
        address.setComentario(dto.getComentario());

        // Gestión de Interfaz
        if (dto.getInterfazId() != null) {
            if (dto.getInterfazId() < 0) {
                // IDs negativos: port1 a port4
                String nombrePort = switch (dto.getInterfazId().intValue()) {
                    case -1 -> "port1";
                    case -2 -> "port2";
                    case -3 -> "port3";
                    case -4 -> "port4";
                    default -> throw new RuntimeException("Puerto default no soportado");
                };

                // BUSCAR O CREAR (y persistir inmediatamente si es nueva)
                Interfaz interfaz = interfazRepo.findByNameAndDispositivoId(nombrePort, dispositivo.getId())
                        .orElseGet(() -> {
                            Interfaz nueva = new Interfaz();
                            nueva.setName(nombrePort);
                            nueva.setTipo("Default");
                            nueva.setDispositivo(dispositivo);
                            return interfazRepo.save(nueva); // <--- CRITICO: Guardar antes de asignar
                        });
                address.setInterfaz(interfaz);
            } else {
                // ID positivo: buscar interfaz existente
                Interfaz interfaz = interfazRepo.findById(dto.getInterfazId())
                        .orElseThrow(() -> new RuntimeException("Interfaz no existe"));
                address.setInterfaz(interfaz);
            }
        } else {
            address.setInterfaz(null);
        }

        // Comunicación con FortiGate
        Map<String, Object> resultado = fortiGateService.editarAddress(dispositivo, address, address.getName());
        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException("Error editando Address en FortiGate: " + resultado);
        }

        return toDTO(addressRepo.save(address));
    }
}

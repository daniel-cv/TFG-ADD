package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.AddressRepository;
import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.InterfazRepository;
import com.smartnetwork.backend.Service.FortiGateService;
import com.smartnetwork.backend.domain.Entity.Address;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.Interfaz;
import com.smartnetwork.backend.domain.dtos.address.AddressDTO;
import com.smartnetwork.backend.domain.dtos.address.CrearAddressDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepo;
    private final InterfazRepository interfazRepo;
    private final DispositivoRepository dispositivoRepo;
    private final FortiGateService fortiGateService;

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

        if (dto.getDispositivoId() == null) {
            throw new RuntimeException("dispositivoId obligatorio");
        }

        Dispositivo dispositivo = dispositivoRepo.findById(dto.getDispositivoId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        Address address = new Address();
        address.setName(dto.getName());
        address.setType(dto.getType());
        address.setIp(dto.getIp());
        address.setComentario(dto.getComentario());
        address.setDispositivo(dispositivo);

        // Interfaz (opcional)
        if (dto.getInterfazId() != null) {
            Interfaz interfaz = interfazRepo.findById(dto.getInterfazId())
                    .orElseThrow(() -> new RuntimeException("Interfaz no existe"));
            address.setInterfaz(interfaz);
        }

        Address saved = addressRepo.save(address);
        fortiGateService.crearAddress(saved.getDispositivo(), saved);
        return toDTO(saved);
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
        dto.setComentario(address.getComentario());

        // 🔑 SIEMPRE .getId()
        dto.setDispositivoId(address.getDispositivo().getId());

        // 🔑 AQUÍ VA LA LÍNEA
        if (address.getInterfaz() != null) {
            dto.setInterfazId(address.getInterfaz().getId());
        }

        return dto;
    }
}

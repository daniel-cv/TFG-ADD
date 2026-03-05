package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.AddressRepository;
import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.Repository.InterfazRepository;
import com.smartnetwork.backend.Service.FortiGateService;
import com.smartnetwork.backend.domain.Entity.Address;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import com.smartnetwork.backend.domain.Entity.Interfaz;
import com.smartnetwork.backend.domain.Entity.ReglaFirewall;
import com.smartnetwork.backend.domain.dtos.address.AddressDTO;
import com.smartnetwork.backend.domain.dtos.address.CrearAddressDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

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

        Dispositivo dispositivo = dispositivoRepo
                .findById(dto.getDispositivoId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        Address address = new Address();
        address.setName(dto.getName());
        address.setType(dto.getType());
        address.setIp(dto.getIp());
        String ipDestino = dto.getIpdestino();
        if(ipDestino != null && !ipDestino.isBlank()){
            address.setIpdestino(ipDestino.trim()); // eliminar espacios
        } else {
            address.setIpdestino(null); // evita string vacío
        }
        address.setComentario(dto.getComentario());
        address.setDispositivo(dispositivo);

        // Interfaz (opcional)
        if (dto.getInterfazId() != null) {
            Interfaz interfaz = interfazRepo.findById(dto.getInterfazId())
                    .orElseThrow(() -> new RuntimeException("Interfaz no existe"));
            address.setInterfaz(interfaz);
        }

        Address saved = addressRepo.save(address);
        Map<String, Object> resultado =
        fortiGateService.crearAddress(saved.getDispositivo(), saved);

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException(
                    "Error creando policy en FortiGate: " + resultado
            );
        }
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

        Dispositivo dispositivo = address.getDispositivo();
        String addressName = address.getName();

        String url = "http://" + dispositivo.getIp()
                + "/api/v2/cmdb/firewall/address/"
                + addressName
                + "?vdom=root";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(dispositivo.getToken());

        HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                // ✅ Primero FortiGate OK → luego BBDD
                addressRepo.delete(address);
            } else {
                throw new RuntimeException(
                        "FortiGate respondió con estado: " + response.getStatusCode()
                );
            }

        } catch (Exception e) {
            // ❌ No tocar BBDD si falla FortiGate
            throw new RuntimeException(
                    "Error eliminando Address en FortiGate (Address=" + addressName + ")", e
            );
        }
    }

}

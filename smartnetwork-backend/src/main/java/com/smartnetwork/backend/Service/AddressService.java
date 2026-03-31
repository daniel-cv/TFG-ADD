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
    public void crear(CrearAddressDTO dto, String username) {

        Address address = new Address();
        address.setName(dto.getName());
        address.setType(dto.getType());
        address.setIp(dto.getIp());
        String ipDestino = dto.getIpdestino();

        if(ipDestino != null && !ipDestino.isBlank()){
            address.setIpdestino(ipDestino.trim());
        } else {
            address.setIpdestino(null);
        }
        address.setComentario(dto.getComentario());

        if (dto.getInterfazId() != null) {
            Interfaz interfaz = interfazRepo.findById(dto.getInterfazId())
                    .orElseThrow(() -> new RuntimeException("Interfaz no existe"));
            address.setInterfaz(interfaz);
        }

        Address saved = addressRepo.save(address);

        for (Long dispositivoId : dto.getDispositivosIds()) {

            Dispositivo dispositivo = dispositivoRepo.findById(dispositivoId)
                    .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

            if (!dispositivo.getUsuario().getUsername().equals(username)) {
                throw new RuntimeException("No autorizado");
            }
//          Map<String, Object> resultado = fortiGateService.crearAddress(dispositivo, address);
//
//          if (!(Boolean) resultado.get("success")) {
//              throw new RuntimeException(
//                      "Error creando policy en FortiGate: " + resultado
//              );
//          }
            DispositivoAddress rel = new DispositivoAddress(dispositivo, saved, dto.getComentario());
            dispositivoAddressRepo.save(rel);
        }
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
            Interfaz interfaz = interfazRepo.findById(dto.getInterfazId())
                    .orElseThrow(() -> new RuntimeException("Interfaz no existe"));
            address.setInterfaz(interfaz);
        }

        address.setUsuario(usuario);
        Address saved = addressRepo.save(address);
        return toAddressDTO(saved);
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

    public void eliminarAddress(Long addressId, String username) {
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address no existe"));

        DispositivoAddress rel = dispositivoAddressRepo
                .findByAddressId(addressId)
                .orElseThrow(() -> new RuntimeException("Relación no encontrada"));

        Dispositivo dispositivo = rel.getDispositivo();

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        Map<String, Object> resultado = fortiGateService.eliminarAddress(dispositivo, address.getName());

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException("Error eliminando Address en FortiGate: " + resultado);
        }
        dispositivoAddressRepo.delete(rel);
        addressRepo.delete(address);
    }

    public AddressDTO editarAddress(Long addressId, CrearAddressDTO dto, String username) {
        DispositivoAddress rel = dispositivoAddressRepo
                .findByAddressId(addressId)
                .orElseThrow(() -> new RuntimeException("Relación no encontrada"));

        Dispositivo dispositivo = rel.getDispositivo();
        Address address = rel.getAddress();

        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        address.setType(dto.getType());
        address.setIp(dto.getIp());
        String ipDestino = dto.getIpdestino();
        if (ipDestino != null && !ipDestino.isBlank()) {
            address.setIpdestino(ipDestino.trim());
        } else {
            address.setIpdestino(null);
        }
        address.setComentario(dto.getComentario());

        if (dto.getInterfazId() != null) {
            Interfaz interfaz = interfazRepo.findById(dto.getInterfazId())
                    .orElseThrow(() -> new RuntimeException("Interfaz no existe"));
            address.setInterfaz(interfaz);
        } else {
            address.setInterfaz(null);
        }

        Map<String, Object> resultado = fortiGateService.editarAddress(dispositivo, address, address.getName());

        if (!(Boolean) resultado.get("success")) {
            throw new RuntimeException("Error editando Address en FortiGate: " + resultado);
        }

        Address saved = addressRepo.save(address);
        return toDTO(saved, dispositivo.getId());
    }

}

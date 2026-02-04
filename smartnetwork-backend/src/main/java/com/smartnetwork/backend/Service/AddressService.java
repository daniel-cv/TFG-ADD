package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.AddressRepository;
import com.smartnetwork.backend.Repository.DispositivoRepository;
import com.smartnetwork.backend.domain.Entity.Address;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final DispositivoRepository dispositivoRepo;
    private final DispositivoService dispositivoService;
    private final RestTemplate restTemplate = new RestTemplate();

    public AddressService(AddressRepository addressRepository, DispositivoRepository dispositivoRepo,DispositivoService dispositivoService) {
        this.addressRepository = addressRepository;
        this.dispositivoRepo = dispositivoRepo;
        this.dispositivoService = dispositivoService;
    }

    public Address create(Address address, String username){
        Dispositivo dispositivo = dispositivoRepo
                .findById(address.getDispositivo().getId())
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        // 🔐 Seguridad básica: el dispositivo es del usuario
        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        address.setDispositivo(dispositivo);

        //FALTA COMPLETAR cearAddress en DispositivoService
        try {
           dispositivoService.crearAddress(dispositivo,address);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return addressRepository.save(address);

    }

    public List<Address> findAllByDispositivo(Long dispositivoId, String username) {

        Dispositivo dispositivo = dispositivoRepo
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        // 🔐 Seguridad: comprobar propietario
        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return addressRepository.findByDispositivoId(dispositivoId);
    }

    public void delete(Address address, String username, Long dispositivoId){
        Dispositivo dispositivo = dispositivoRepo
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        // 🔐 Seguridad: comprobar propietario
        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }
        addressRepository.delete(address);
    }

    public Optional<Address> findById(String username, Long dispositivoId, Long addressId) {

        Dispositivo dispositivo = dispositivoRepo
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        // 🔐 Seguridad: comprobar propietario
        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }

        return addressRepository.findByIdAndDispositivoId(addressId, dispositivoId);
    }

    public Address update(Address address, String username,Long dispositivoId){
        Dispositivo dispositivo = dispositivoRepo
                .findById(dispositivoId)
                .orElseThrow(() -> new RuntimeException("Dispositivo no existe"));

        // 🔐 Seguridad: comprobar propietario
        if (!dispositivo.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }
        return addressRepository.save(address);
    }
}

package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Service.AddressService;
import com.smartnetwork.backend.domain.Entity.Address;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/dispositivos/{dispositivoId}/addresses")
public class AddresController {
    private final AddressService addressService;

    public AddresController(AddressService addressService){
        this.addressService = addressService;
    }
    // 🔹 CREAR address
    @PostMapping("/create")
    public Address crear(
            @PathVariable Long dispositivoId,
            @RequestBody Address address,
            Authentication authentication
    ) {
        String username = authentication.getName();

        // aseguramos que viene bien asociado
        address.getDispositivo().setId(dispositivoId);

        return addressService.create(address, username);
    }

    // 🔹 LISTAR addresses de un dispositivo
    @GetMapping("/findAll")
    public List<Address> listar(
            @PathVariable Long dispositivoId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return addressService.findAllByDispositivo(dispositivoId, username);
    }

    // 🔹 OBTENER una address
    @GetMapping("/get/{addressId}")
    public Address obtener(
            @PathVariable Long dispositivoId,
            @PathVariable Long addressId,
            Authentication authentication
    ) {
        String username = authentication.getName();

        return addressService
                .findById(username, dispositivoId, addressId)
                .orElseThrow(() -> new RuntimeException("Address no encontrada"));
    }

    // 🔹 ACTUALIZAR
    @PutMapping("/update/{addressId}")
    public Address actualizar(
            @PathVariable Long dispositivoId,
            @PathVariable Long addressId,
            @RequestBody Address address,
            Authentication authentication
    ) {
        String username = authentication.getName();
        address.setId(addressId);
        return addressService.update(address, username, dispositivoId);
    }

    // 🔹 ELIMINAR
    @DeleteMapping("/delete/{addressId}")
    public void eliminar(
            @PathVariable Long dispositivoId,
            @PathVariable Long addressId,
            Authentication authentication
    ) {
        String username = authentication.getName();

        Address address = addressService
                .findById(username, dispositivoId, addressId)
                .orElseThrow(() -> new RuntimeException("Address no encontrada"));

        addressService.delete(address, username, dispositivoId);
    }

}

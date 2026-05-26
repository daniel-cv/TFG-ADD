package com.smartnetwork.backend.Controller.firewalls;

import com.smartnetwork.backend.Service.firewalls.AddressService;
import com.smartnetwork.backend.domain.dtos.firewalls.address.AddressDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.address.CrearAddressDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/firewalls/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/create")
    public AddressDTO crear(@RequestBody CrearAddressDTO dto, Authentication auth) {
        return addressService.crear(dto, auth.getName());
    }

    @GetMapping("/dispositivo/{id}")
    public List<AddressDTO> listar(
            @PathVariable Long id,
            Authentication auth
    ) {
        return addressService.listarPorDispositivo(id, auth.getName());
    }

    @DeleteMapping("/delete/{id}")
    public void eliminar(
            @PathVariable Long id,
            Authentication auth
    ) {
        addressService.eliminarAddress(id, auth.getName());
    }
    @PutMapping("/edit/{id}")
    public AddressDTO editar(
            @PathVariable Long id,
            @RequestBody CrearAddressDTO dto,
            Authentication auth
    ) {
        return addressService.editarAddress(id, dto, auth.getName());
    }
}
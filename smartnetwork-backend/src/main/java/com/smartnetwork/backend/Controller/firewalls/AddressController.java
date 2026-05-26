package com.smartnetwork.backend.Controller.firewalls;

import com.smartnetwork.backend.Service.firewalls.AddressService;
import com.smartnetwork.backend.domain.dtos.firewalls.address.AddressDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.address.CrearAddressDTO;
import org.springframework.http.ResponseEntity;
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
    public void crear(@RequestBody CrearAddressDTO dto, Authentication auth) {
        addressService.crearAddress(dto, auth.getName());
    }

    @GetMapping("/dispositivo/{id}")
    public List<AddressDTO> listar(
            @PathVariable Long id,
            Authentication auth
    ) {
        return addressService.listarPorDispositivo(id, auth.getName());
    }

    @GetMapping("/usuario")
    public List<AddressDTO> listarAll(
            @PathVariable Long id,
            Authentication auth
    ){
        return addressService.listarPorDispositivo(id, auth.getName());
    }

    @PostMapping("/{addressId}/dispositivos")
    public ResponseEntity<Void> asignarAddress(
            @PathVariable Long addressId,
            @RequestBody List<Long> dispositivosIds,
            Authentication auth
    ) {
        addressService.asignarAddressADispositivos(addressId, dispositivosIds, auth.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/full")
    public AddressDTO crearCompleto(
            @RequestBody CrearAddressDTO dto,
            Authentication auth
    ) {
        return addressService.crear(dto, auth.getName());
    }

    @GetMapping("/usuario/")
    public List<AddressDTO> listarPorUsuario(
            Authentication auth
    ) {
        return addressService.listarPorUsuario(auth.getName());
    }

    @DeleteMapping("/delete/{addressId}")
    public void eliminar(
            @RequestBody List <Long> dispositivosIds,
            @PathVariable Long addressId,
            Authentication auth
    ) {
        addressService.eliminarAddress(addressId,dispositivosIds, auth.getName());
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
package com.smartnetwork.backend.Controller.firewalls;

import com.smartnetwork.backend.Service.firewalls.ReglaFirewallService;
import com.smartnetwork.backend.domain.dtos.firewalls.Policys.CrearReglaFirewallDTO;
import com.smartnetwork.backend.domain.dtos.firewalls.Policys.ReglaFirewallDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/firewalls/reglas")
@RequiredArgsConstructor
public class ReglaFirewallController {

    private final ReglaFirewallService
            reglaFirewallService;

    /* ===================================================== */
    /* GET DEVICE */
    /* ===================================================== */

    @GetMapping("/dispositivo/{id}")
    public List<ReglaFirewallDTO>
    listarPorDispositivo(

            @PathVariable Long id,

            Authentication auth

    ) {

        return reglaFirewallService
                .listarPorDispositivo(
                        id,
                        auth.getName()
                );
    }

    /* ===================================================== */
    /* GET USER */
    /* ===================================================== */

    @GetMapping("/usuario")
    public List<ReglaFirewallDTO>
    listarUsuario(
            Authentication auth
    ) {

        return reglaFirewallService
                .listarPorUsario(
                        auth.getName()
                );
    }

    /* ===================================================== */
    /* CREATE SIMPLE */
    /* ===================================================== */

    @PostMapping("/create")
    public ReglaFirewallDTO crear(

            @RequestBody
            CrearReglaFirewallDTO dto,

            Authentication auth

    ) {

        return reglaFirewallService
                .crearReglaFirewall(
                        dto,
                        auth.getName()
                );
    }

    /* ===================================================== */
    /* CREATE FULL */
    /* ===================================================== */

    @PostMapping("/full")
    public ReglaFirewallDTO crearCompleta(

            @RequestBody
            CrearReglaFirewallDTO dto,

            Authentication auth

    ) {

        return reglaFirewallService
                .crear(
                        dto,
                        auth.getName()
                );
    }

    /* ===================================================== */
    /* APPLY */
    /* ===================================================== */

    @PostMapping("/{id}/dispositivos")
    public void asignar(

            @PathVariable Long id,

            @RequestBody
            List<Long> dispositivosIds,

            Authentication auth

    ) {

        reglaFirewallService
                .asignarReglaFirewallADispositivos(

                        id,

                        dispositivosIds,

                        auth.getName()
                );
    }

    /* ===================================================== */
    /* EDIT */
    /* ===================================================== */

    @PutMapping("/edit/{id}")
    public ReglaFirewallDTO editar(

            @PathVariable Long id,

            @RequestBody
            CrearReglaFirewallDTO dto,

            Authentication auth

    ) {

        return reglaFirewallService
                .editarReglaFirewall(

                        id,

                        dto,

                        auth.getName()
                );
    }

    /* ===================================================== */
    /* DELETE */
    /* ===================================================== */

    @DeleteMapping("/delete/{id}")
    public void eliminar(
            @PathVariable Long id,
            @RequestBody Map<String, List<Long>> body,
            Authentication auth
    ) {
        List<Long> dispositivosIds =
                body.get("dispositivosIds");
        reglaFirewallService
                .eliminarRegla(

                        id,

                        dispositivosIds,

                        auth.getName()
                );
    }
}
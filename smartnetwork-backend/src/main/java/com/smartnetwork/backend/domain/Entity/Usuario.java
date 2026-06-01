package com.smartnetwork.backend.domain.Entity;

import com.smartnetwork.backend.domain.Entity.firewalls.Address.Address;
import com.smartnetwork.backend.domain.Entity.firewalls.Interfaz.Interfaz;
import com.smartnetwork.backend.domain.Entity.firewalls.ReglaFirewall.ReglaFirewall;
import com.smartnetwork.backend.domain.Entity.firewalls.Service.Service;
import com.smartnetwork.backend.domain.Entity.firewalls.UsuarioFirewall.UsuarioFirewall;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
@ToString(exclude = "dispositivos")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    private String role;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dispositivo> dispositivos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Service> services = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Interfaz> interfaz = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioFirewall> usuarioFirewalls = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReglaFirewall> reglasFirewall = new ArrayList<>();
}



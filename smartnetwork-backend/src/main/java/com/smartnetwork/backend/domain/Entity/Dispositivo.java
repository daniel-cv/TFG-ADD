package com.smartnetwork.backend.domain.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartnetwork.backend.domain.Entity.firewalls.*;
import com.smartnetwork.backend.domain.Entity.firewalls.Address.DispositivoAddress;
import com.smartnetwork.backend.domain.Entity.firewalls.Interfaz.DispositivoInterfaz;
import com.smartnetwork.backend.domain.Entity.firewalls.ReglaFirewall.DispositivoReglaFirewall;
import com.smartnetwork.backend.domain.Entity.firewalls.Service.DispositivoService;
import com.smartnetwork.backend.domain.Entity.firewalls.UsuarioFirewall.DispositivoUsuarioFirewall;
import com.smartnetwork.backend.domain.Enum.EstadoDispositivo;
import com.smartnetwork.backend.domain.Enum.Fabricante;
import com.smartnetwork.backend.domain.Enum.TipoDispositivo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dispositivos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"usuario", "credencial", "configuraciones", "reglasFirewall", "virtualaddress", "usuarioFirewall"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Dispositivo {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = true)
    private String token;

    @Column(nullable = true)
    private String usuarioConexion;

    @Column(nullable = true)
    private String passwordConexion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDispositivo tipo;

    @Column(nullable = false, unique = true)
    private String ip;

    @Column(nullable = false)
    private Integer puerto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Fabricante fabricante;

    @Enumerated(EnumType.STRING)
    private EstadoDispositivo estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference
    private Usuario usuario;

    @OneToOne(mappedBy = "dispositivo", cascade = CascadeType.ALL)
    private Credencial credencial;

    @OneToMany(mappedBy = "dispositivo", cascade = CascadeType.ALL)
    private List<Configuracion> configuraciones = new ArrayList<>();

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "dispositivo", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<DispositivoReglaFirewall> reglasFirewall = new ArrayList<>();

    @OneToMany(mappedBy = "dispositivo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<DispositivoAddress> dispositivoAddresses = new ArrayList<>();


    @OneToMany(mappedBy = "dispositivo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<DispositivoService> dispositivoServices = new ArrayList<>();

    @OneToMany(mappedBy = "dispositivo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<DispositivoInterfaz>  dispositivoInterfaz = new ArrayList<>();

    @OneToMany(mappedBy = "dispositivo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<VirtualIp> virtualaddress = new ArrayList<>();

    @OneToMany(mappedBy = "dispositivo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Log> logs = new ArrayList<>();

    @OneToMany(mappedBy = "dispositivo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<DispositivoUsuarioFirewall> dispositivoUsuarioFirewalls = new ArrayList<>();


}


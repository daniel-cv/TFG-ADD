package com.smartnetwork.backend.domain.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@ToString(exclude = {
        "usuario",
        "credencial",
        "configuraciones",
        "reglasFirewall",
        "addresses",
        "services",
        "virtualaddress",
        "usuarioFirewall"
})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Dispositivo {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String token;

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
    private List<ReglaFirewall> reglasFirewall = new ArrayList<>();

    @OneToMany(mappedBy = "dispositivo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "dispositivo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Service> services = new ArrayList<>();

    @OneToMany(mappedBy = "dispositivo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<VirtualIp> virtualaddress = new ArrayList<>();

    @OneToMany(mappedBy = "dispositivo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UsuarioFirewall> usuarioFirewall = new ArrayList<>();
}


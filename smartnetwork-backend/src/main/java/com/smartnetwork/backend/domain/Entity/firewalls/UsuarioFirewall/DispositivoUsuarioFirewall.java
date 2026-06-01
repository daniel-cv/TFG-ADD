package com.smartnetwork.backend.domain.Entity.firewalls.UsuarioFirewall;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispositivo_usuarioFirewall")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispositivoUsuarioFirewall {

    @EmbeddedId
    private DispositivoUsuarioFirewallId id =  new DispositivoUsuarioFirewallId();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dispositivoId")
    @JoinColumn(name = "dispositivo_id")
    private Dispositivo dispositivo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioFirewallId")
    @JoinColumn(name = "usuario_firewall_id")
    private UsuarioFirewall usuarioFirewall;

    private String comentario;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public DispositivoUsuarioFirewall(Dispositivo dispositivo, UsuarioFirewall usuarioFirewall, String comentario) {
        this.dispositivo = dispositivo;
        this.usuarioFirewall = usuarioFirewall;
        this.comentario = comentario;
        this.createdAt = LocalDateTime.now();
        this.id = new DispositivoUsuarioFirewallId(dispositivo.getId(), usuarioFirewall.getId());
    }
}

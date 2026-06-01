package com.smartnetwork.backend.domain.Entity.firewalls.ReglaFirewall;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispositivo_reglaFirewall")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispositivoReglaFirewall {
    @EmbeddedId
    private DispositivoReglaFirewallId id = new DispositivoReglaFirewallId();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dispositivoId")
    @JoinColumn(name = "dispositivo_id")
    private Dispositivo dispositivo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("reglaFirewallId")
    @JoinColumn(name = "regla_firewall_id")
    private ReglaFirewall reglaFirewall;

    private String comentario;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public DispositivoReglaFirewall(Dispositivo dispositivo, ReglaFirewall reglaFirewall, String comentario) {
        this.dispositivo = dispositivo;
        this.reglaFirewall = reglaFirewall;
        this.comentario = comentario;
        this.id = new DispositivoReglaFirewallId(dispositivo.getId(),  reglaFirewall.getId());
    }
}

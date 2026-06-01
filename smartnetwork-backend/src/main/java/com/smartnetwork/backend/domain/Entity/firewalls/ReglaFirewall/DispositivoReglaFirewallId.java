package com.smartnetwork.backend.domain.Entity.firewalls.ReglaFirewall;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispositivoReglaFirewallId implements Serializable {
    private Long dispositivoId;
    private Long reglaFirewallId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DispositivoReglaFirewallId)) return false;
        DispositivoReglaFirewallId that = (DispositivoReglaFirewallId) o;
        return Objects.equals(dispositivoId, that.dispositivoId) &&
                Objects.equals(reglaFirewallId, that.reglaFirewallId);
    }

    @Override
    public int hashCode() {return Objects.hash(dispositivoId, reglaFirewallId);}
}

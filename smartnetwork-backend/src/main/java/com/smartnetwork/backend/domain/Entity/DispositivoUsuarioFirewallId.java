package com.smartnetwork.backend.domain.Entity;

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
public class DispositivoUsuarioFirewallId implements Serializable {
    private Long dispositivoId;
    private Long usuarioFirewallId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DispositivoUsuarioFirewallId)) return false;
        DispositivoUsuarioFirewallId that = (DispositivoUsuarioFirewallId) o;
        return Objects.equals(dispositivoId, that.dispositivoId)
                && Objects.equals(usuarioFirewallId, that.usuarioFirewallId);
    }

    @Override
    public int hashCode() {return Objects.hash(dispositivoId, usuarioFirewallId);}
}

package com.smartnetwork.backend.domain.Entity.firewalls.Interfaz;

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
public class DispositivoInterfazId implements Serializable {
    private Long dispositivoId;
    private Long interfazId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DispositivoInterfazId)) return false;
        DispositivoInterfazId that = (DispositivoInterfazId) o;
        return Objects.equals(dispositivoId, that.dispositivoId) &&
                Objects.equals(interfazId, that.interfazId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dispositivoId, interfazId);
    }
}

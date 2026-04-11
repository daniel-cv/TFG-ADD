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
public class DispositivoServiceId implements Serializable {

    private Long dispositivoId;
    private Long serviceId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DispositivoServiceId)) return false;
        DispositivoServiceId that = (DispositivoServiceId) o;
        return Objects.equals(dispositivoId, that.dispositivoId) &&
                Objects.equals(serviceId, that.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dispositivoId, serviceId);
    }
}

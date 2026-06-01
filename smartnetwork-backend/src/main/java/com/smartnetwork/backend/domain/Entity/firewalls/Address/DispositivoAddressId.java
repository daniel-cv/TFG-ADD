package com.smartnetwork.backend.domain.Entity.firewalls.Address;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispositivoAddressId implements Serializable {

    private Long dispositivoId;
    private Long addressId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DispositivoAddressId)) return false;
        DispositivoAddressId that = (DispositivoAddressId) o;
        return Objects.equals(dispositivoId, that.dispositivoId) &&
                Objects.equals(addressId, that.addressId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dispositivoId, addressId);
    }
}

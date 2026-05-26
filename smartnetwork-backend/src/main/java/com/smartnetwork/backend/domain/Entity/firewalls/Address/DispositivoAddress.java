package com.smartnetwork.backend.domain.Entity.firewalls.Address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartnetwork.backend.domain.Entity.Dispositivo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispositivo_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispositivoAddress {

    @EmbeddedId
    private DispositivoAddressId id = new DispositivoAddressId();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dispositivoId")
    @JoinColumn(name = "dispositivo_id")
    private Dispositivo dispositivo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("addressId")
    @JoinColumn(name = "address_id")
    private Address address;

    private String comentario;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public DispositivoAddress(Dispositivo dispositivo, Address address, String comentario) {
        this.dispositivo = dispositivo;
        this.address = address;
        this.comentario = comentario;
        this.id = new DispositivoAddressId(dispositivo.getId(), address.getId());
    }
}

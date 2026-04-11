package com.smartnetwork.backend.domain.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispositivo_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispositivoService {
    @EmbeddedId
    private DispositivoServiceId id = new DispositivoServiceId();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dispositivoId")
    @JoinColumn(name = "dispositivo_id")
    private Dispositivo dispositivo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("serviceId")
    @JoinColumn(name = "service_id")
    private Service service;

    private String comentario;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public DispositivoService(Dispositivo dispositivo, Service service, String comentario) {
        this.dispositivo = dispositivo;
        this.service = service;
        this.comentario = comentario;
        this.id = new DispositivoServiceId(dispositivo.getId(),  service.getId());
    }
}

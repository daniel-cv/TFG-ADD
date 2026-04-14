package com.smartnetwork.backend.domain.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.lang.model.element.NestingKind;
import java.time.LocalDateTime;

@Entity
@Table(name = "dispositivo_interfaz")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispositivoInterfaz {
    @EmbeddedId
    private DispositivoInterfazId id =  new DispositivoInterfazId();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dispositivoId")
    @JoinColumn(name = "dispositivo_id")
    private Dispositivo dispositivo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("interfazId")
    @JoinColumn(name = "interfaz_id")
    private Interfaz interfaz;

    private String comentario;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public DispositivoInterfaz(Dispositivo dispositivo, Interfaz interfaz, String comentario) {
        this.dispositivo = dispositivo;
        this.interfaz = interfaz;
        this.comentario = comentario;
        this.id = new DispositivoInterfazId(dispositivo.getId(), interfaz.getId());
    }
}

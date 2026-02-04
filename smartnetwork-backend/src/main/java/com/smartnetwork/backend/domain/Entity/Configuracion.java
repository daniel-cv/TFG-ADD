package com.smartnetwork.backend.domain.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "configuraciones")
public class Configuracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoConfiguracion; // VLAN, ACL, INTERFAZ...

    @Lob
    private String contenido;

    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "dispositivo_id")
    private Dispositivo dispositivo;
}


package com.smartnetwork.backend.domain.Entity.firewalls;

import com.smartnetwork.backend.domain.Entity.Dispositivo;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "services")
@ToString(exclude = "dispositivo")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Service {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String comentario;

    private String categoria;

    @Column(nullable = false)
    private String tipoProtocolo;

    @Column(nullable = true)
    private String ip;

    @Column(nullable = true)
    private String destinationPort;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;
}

package com.smartnetwork.backend.domain.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    private String role; // ROLE_USER, ROLE_ADMIN

    @OneToMany(mappedBy = "usuario")
    @JsonManagedReference
    private List<Dispositivo> dispositivos;
}


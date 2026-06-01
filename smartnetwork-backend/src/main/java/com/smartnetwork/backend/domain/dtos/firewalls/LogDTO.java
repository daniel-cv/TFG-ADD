package com.smartnetwork.backend.domain.dtos.firewalls;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LogDTO {

    private Long id;
    private LocalDateTime fechaHora;
    private String accion;
    private String mensaje;

    private String usuario;
    private String dispositivo;
}
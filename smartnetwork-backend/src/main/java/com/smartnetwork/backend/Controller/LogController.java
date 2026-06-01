package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Service.LogService;
import com.smartnetwork.backend.domain.Entity.Log;
import com.smartnetwork.backend.domain.dtos.firewalls.address.AddressDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smartnetwork.backend.domain.dtos.firewalls.LogDTO;

import java.util.List;

@RestController
@RequestMapping("/api/log")
public class LogController {

    private final LogService logService;
    public LogController(LogService logService){
        this.logService = logService;
    }
    @GetMapping("/{id}")
    public List<LogDTO> listarLogs(
            @PathVariable Long id,
            Authentication auth
    ){
        return logService.listarPorDispositivo(id, auth.getName());
    }
}
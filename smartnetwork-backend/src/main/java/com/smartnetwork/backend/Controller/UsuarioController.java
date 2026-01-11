package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.domain.Entity.Usuario;
import com.smartnetwork.backend.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/usuario")
@CrossOrigin(origins = "http://localhost:5173") // Permite tu frontend
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping ("/findAll")
    public @ResponseBody List<Usuario> getUsuarios(){
        return usuarioService.findAll();
    }

    @PostMapping("/create")
    public @ResponseBody Usuario createUsuario(@RequestBody Usuario usuario) {
        return usuarioService.createUsuario(usuario);
    }
    @PostMapping("/login")
    public Usuario login(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        return usuarioService.loadUserByUsername(username, password);
    }
}

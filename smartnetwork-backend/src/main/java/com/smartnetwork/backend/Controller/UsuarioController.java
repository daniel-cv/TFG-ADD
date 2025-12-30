package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Entity.Usuario;
import com.smartnetwork.backend.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/usuario")
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
    public Usuario login(@RequestBody Usuario usuario) {
        return usuarioService.loadUserByUsername(usuario.getUsername(), usuario.getPassword());
    }
}

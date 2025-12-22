package com.smartnetwork.backend.Controller;

import com.smartnetwork.backend.Entity.Usuario;
import com.smartnetwork.backend.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController ("/usuario")
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
}

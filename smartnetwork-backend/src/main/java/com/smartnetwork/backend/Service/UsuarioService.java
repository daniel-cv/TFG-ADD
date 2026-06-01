package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.domain.Entity.Usuario;
import com.smartnetwork.backend.Repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario createUsuario(Usuario usuario) {

        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre de usuario ya existe"
            );
        }

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El email ya está registrado"
            );
        }

        String regexemail = "^[A-Za-z0-9.%+-]+@[A-Za-z0-9.-]+[A-Za-z]{2,}$";
        if (!usuario.getEmail().matches(regexemail)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El email no parece un email válido"
            );
        }

        String regexusername = "^[A-Za-z0-9][A-Za-z0-9._]*$";
        if (!usuario.getUsername().matches(regexusername)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nombre no válido, debe empezar por letras o números y solo puede contener '.' y ''"
            );
        }
/*
        String regex = "^(?=.[a-z])(?=.[A-Z])(?=.*[^A-Za-z0-9]).{8,}$";
        if (!usuario.getPassword().matches(regex)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un carácter especial"
            );
        }
*/
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario loadUserByUsername(String username, String password)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return usuario;

    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ese email"));
    }

    public void  deleteById(long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario updateUsuario(Usuario usuario) {
        usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("No existe el usuario a actualizar"));
        return usuarioRepository.save(usuario);
    }

}

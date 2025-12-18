package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Entity.Usuario;
import com.smartnetwork.backend.Repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crea un usuario a partir de los datos obtenidos, la contraseña es encriptada
     * @param usuario
     * @return
     */
    public Usuario createUsuario (Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    /**
     * Obtiene todos los empleados de la bbdd
     * @return
     */
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    /**
     * Obtiene el usuario con el id que se le solicite
     * @param id
     * @return
     */
    public Optional<Usuario> findById(int id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Elimina el usuario con el id solicitado de la bbdd
     * @param id
     */
    public void  deleteById(int id) {
        usuarioRepository.deleteById(id);
    }

    /**
     * Actualiza los datos del usuario
     * @param usuario
     * @return
     */
    public Usuario updateUsuario (Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}

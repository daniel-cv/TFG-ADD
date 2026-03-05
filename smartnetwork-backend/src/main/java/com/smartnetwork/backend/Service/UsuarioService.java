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

    /**
     * Crea un usuario a partir de los datos obtenidos, la contraseña es encriptada
     * @param usuario
     * @return
     */
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

    /**
     * Obtiene el usuario con el username que se le solicite
     * @param username
     * @return
     */
    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }


    /**
     * Obtiene todos los usuarios de la bbdd
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
    public Usuario findById(int id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /**
     * Busca usuario por username
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    public Usuario loadUserByUsername(String username, String password)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return usuario;

    }
    /**
     * Obtiene el usuario con el email que se le solicite
     * @param email
     * @return
     */
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ese email"));
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
    public Usuario updateUsuario(Usuario usuario) {
        usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("No existe el usuario a actualizar"));
        return usuarioRepository.save(usuario);
    }

}

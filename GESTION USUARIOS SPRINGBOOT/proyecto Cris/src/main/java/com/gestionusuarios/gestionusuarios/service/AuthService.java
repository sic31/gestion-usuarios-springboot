package com.gestionusuarios.gestionusuarios.service;

import com.gestionusuarios.gestionusuarios.dto.AuthResponse;
import com.gestionusuarios.gestionusuarios.exception.CredencialesInvalidasException;
import com.gestionusuarios.gestionusuarios.exception.RolNoEncontradoException;
import com.gestionusuarios.gestionusuarios.exception.UsuarioNoEncontradoException;
import com.gestionusuarios.gestionusuarios.exception.UsuarioYaExisteException;
import com.gestionusuarios.gestionusuarios.model.Rol;
import com.gestionusuarios.gestionusuarios.model.Usuario;
import com.gestionusuarios.gestionusuarios.repository.RolRepository;
import com.gestionusuarios.gestionusuarios.repository.UsuarioRepository;
import com.gestionusuarios.gestionusuarios.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public Usuario registro(String nombre, String correo, String password) {
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new UsuarioYaExisteException("El correo " + correo + " ya está registrado");
        }

        Rol rolUsuario = rolRepository.findByNombre("usuario")
                .orElseThrow(() -> new RolNoEncontradoException("Rol 'usuario' no encontrado en la base de datos"));

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rolUsuario);
        usuario.setEstado("activo");
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setUltimoAcceso(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    public AuthResponse login(String correo, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(correo, password)
            );
        } catch (BadCredentialsException e) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos");
        }

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuario);
        return new AuthResponse(token, usuario.getRol().getNombre(), "Autenticación exitosa");
    }
}

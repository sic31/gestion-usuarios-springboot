package com.gestionusuarios.gestionusuarios.controller;

import com.gestionusuarios.gestionusuarios.dto.AuthResponse;
import com.gestionusuarios.gestionusuarios.dto.LoginRequest;
import com.gestionusuarios.gestionusuarios.dto.RegisterRequest;
import com.gestionusuarios.gestionusuarios.dto.UsuarioDTO;
import com.gestionusuarios.gestionusuarios.model.Usuario;
import com.gestionusuarios.gestionusuarios.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registro(@Valid @RequestBody RegisterRequest request) {
        Usuario usuario = authService.registro(
                request.getNombre(),
                request.getCorreo(),
                request.getPassword()
        );
        UsuarioDTO usuarioDTO = mapearUsuarioDTO(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse respuesta = authService.login(
                request.getCorreo(),
                request.getPassword()
        );
        return ResponseEntity.ok(respuesta);
    }

    private UsuarioDTO mapearUsuarioDTO(Usuario usuario) {
        return new UsuarioDTO(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getCorreo(),
            usuario.getRol().getNombre(),
            usuario.getEstado(),
            usuario.getFechaRegistro(),
            usuario.getUltimoAcceso()
        );
    }
}

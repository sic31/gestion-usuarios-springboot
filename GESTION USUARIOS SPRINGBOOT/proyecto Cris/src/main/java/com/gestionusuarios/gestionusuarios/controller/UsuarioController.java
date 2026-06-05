package com.gestionusuarios.gestionusuarios.controller;

import com.gestionusuarios.gestionusuarios.dto.*;
import com.gestionusuarios.gestionusuarios.exception.CredencialesInvalidasException;
import com.gestionusuarios.gestionusuarios.exception.RolNoEncontradoException;
import com.gestionusuarios.gestionusuarios.exception.UsuarioNoEncontradoException;
import com.gestionusuarios.gestionusuarios.exception.UsuarioYaExisteException;
import com.gestionusuarios.gestionusuarios.model.Rol;
import com.gestionusuarios.gestionusuarios.model.Usuario;
import com.gestionusuarios.gestionusuarios.repository.RolRepository;
import com.gestionusuarios.gestionusuarios.repository.UsuarioRepository;
import com.gestionusuarios.gestionusuarios.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    // ──────────────────────────────────────────────
    // Endpoints de usuario autenticado (/api/usuarios)
    // ──────────────────────────────────────────────

    @GetMapping("/api/usuarios/me")
    public ResponseEntity<UsuarioDTO> getMe(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(mapearUsuarioDTO(usuario));
    }

    @PutMapping("/api/usuarios/me")
    public ResponseEntity<UsuarioDTO> updateMe(
            @AuthenticationPrincipal Usuario usuarioActual,
            @RequestBody UsuarioDTO updateData) {

        if (updateData.getNombre() != null && !updateData.getNombre().isBlank()) {
            usuarioActual.setNombre(updateData.getNombre());
        }
        if (updateData.getCorreo() != null && !updateData.getCorreo().isBlank()) {
            if (!updateData.getCorreo().equals(usuarioActual.getCorreo())
                    && usuarioRepository.existsByCorreo(updateData.getCorreo())) {
                throw new UsuarioYaExisteException("El correo " + updateData.getCorreo() + " ya está en uso");
            }
            usuarioActual.setCorreo(updateData.getCorreo());
        }

        Usuario actualizado = usuarioService.save(usuarioActual);
        return ResponseEntity.ok(mapearUsuarioDTO(actualizado));
    }

    @PutMapping("/api/usuarios/me/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal Usuario usuarioActual,
            @Valid @RequestBody ChangePasswordRequest request) {

        if (!passwordEncoder.matches(request.getPasswordActual(), usuarioActual.getPassword())) {
            throw new CredencialesInvalidasException("La contraseña actual es incorrecta");
        }

        usuarioActual.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioService.save(usuarioActual);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));
    }

    @DeleteMapping("/api/usuarios/me")
    public ResponseEntity<Void> deactivateMe(@AuthenticationPrincipal Usuario usuarioActual) {
        usuarioActual.setEstado("inactivo");
        usuarioService.save(usuarioActual);
        return ResponseEntity.noContent().build();
    }

    // ──────────────────────────────────────────────
    // Endpoints de administrador (/api/admin)
    // ──────────────────────────────────────────────

    @GetMapping("/api/admin/usuarios")
    public ResponseEntity<List<UsuarioDTO>> getAll() {
        List<Usuario> usuarios = usuarioService.findAll();
        List<UsuarioDTO> dtos = usuarios.stream()
                .map(this::mapearUsuarioDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/api/admin/usuarios")
    public ResponseEntity<UsuarioDTO> createUser(
            @Valid @RequestBody AdminCreateUserRequest request) {

        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new UsuarioYaExisteException("El correo " + request.getCorreo() + " ya está registrado");
        }

        Rol rol = rolRepository.findByNombre(request.getRol())
                .orElseThrow(() -> new RolNoEncontradoException("Rol '" + request.getRol() + "' no encontrado"));

        Usuario nuevo = new Usuario();
        nuevo.setNombre(request.getNombre());
        nuevo.setCorreo(request.getCorreo());
        nuevo.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevo.setRol(rol);
        nuevo.setEstado("activo");
        nuevo.setFechaRegistro(java.time.LocalDateTime.now());
        nuevo.setUltimoAcceso(java.time.LocalDateTime.now());

        return ResponseEntity.status(201).body(mapearUsuarioDTO(usuarioService.save(nuevo)));
    }

    @PutMapping("/api/admin/usuarios/{id}")
    public ResponseEntity<UsuarioDTO> updateUser(
            @PathVariable Long id,
            @RequestBody AdminUpdateUserRequest request) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario con id " + id + " no encontrado"));

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            usuario.setNombre(request.getNombre());
        }
        if (request.getCorreo() != null && !request.getCorreo().isBlank()) {
            if (!request.getCorreo().equals(usuario.getCorreo())
                    && usuarioRepository.existsByCorreo(request.getCorreo())) {
                throw new UsuarioYaExisteException("El correo " + request.getCorreo() + " ya está en uso");
            }
            usuario.setCorreo(request.getCorreo());
        }
        if (request.getRol() != null && !request.getRol().isBlank()) {
            Rol rol = rolRepository.findByNombre(request.getRol())
                    .orElseThrow(() -> new RolNoEncontradoException("Rol '" + request.getRol() + "' no encontrado"));
            usuario.setRol(rol);
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return ResponseEntity.ok(mapearUsuarioDTO(usuarioService.save(usuario)));
    }

    @DeleteMapping("/api/admin/usuarios/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario adminActual) {

        if (adminActual.getId().equals(id)) {
            throw new IllegalArgumentException("No puedes eliminarte a ti mismo");
        }
        if (!usuarioService.delete(id)) {
            throw new UsuarioNoEncontradoException("Usuario con id " + id + " no encontrado");
        }
        return ResponseEntity.noContent().build();
    }

    // ──────────────────────────────────────────────
    // Helper
    // ──────────────────────────────────────────────

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

package com.gestionusuarios.gestionusuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String correo;
    private String rol;
    private String estado;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoAcceso;
}

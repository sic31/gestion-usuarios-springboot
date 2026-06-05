package com.gestionusuarios.gestionusuarios.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class AdminUpdateUserRequest {

    private String nombre;

    @Email(message = "El correo debe ser válido")
    private String correo;

    private String rol; // "usuario" o "admin"

    private String password; // opcional, si viene se cambia
}

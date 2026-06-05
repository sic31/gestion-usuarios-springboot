package com.gestionusuarios.gestionusuarios.exception;

public class RolNoEncontradoException extends RuntimeException {
    
    public RolNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public RolNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

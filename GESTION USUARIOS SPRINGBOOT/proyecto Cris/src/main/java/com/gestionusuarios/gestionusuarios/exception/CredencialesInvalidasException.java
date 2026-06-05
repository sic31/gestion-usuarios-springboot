package com.gestionusuarios.gestionusuarios.exception;

public class CredencialesInvalidasException extends RuntimeException {
    
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }

    public CredencialesInvalidasException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

package com.joaovpg.economize.usuario;

public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() {
        super("E-mail ou senha invalidos");
    }
}

package com.joaovpg.economize.usuario;

public class EmailJaCadastradoException extends RuntimeException {
    public EmailJaCadastradoException() {
        super("E-mail ja cadastrado");
    }
}

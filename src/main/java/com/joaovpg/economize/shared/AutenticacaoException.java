package com.joaovpg.economize.shared;

public class AutenticacaoException extends RuntimeException {
    private final String codigo;

    public AutenticacaoException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }

    public String codigo() {
        return codigo;
    }
}

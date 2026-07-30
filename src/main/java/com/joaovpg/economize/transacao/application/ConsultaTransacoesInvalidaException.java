package com.joaovpg.economize.transacao.application;

public class ConsultaTransacoesInvalidaException extends RuntimeException {
    private final String campo;

    public ConsultaTransacoesInvalidaException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }

    public String campo() {
        return campo;
    }
}

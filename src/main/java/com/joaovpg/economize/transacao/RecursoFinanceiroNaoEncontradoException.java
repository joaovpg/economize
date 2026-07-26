package com.joaovpg.economize.transacao;

public class RecursoFinanceiroNaoEncontradoException extends RuntimeException {
    public RecursoFinanceiroNaoEncontradoException(String recurso) {
        super(recurso + " nao encontrado");
    }
}

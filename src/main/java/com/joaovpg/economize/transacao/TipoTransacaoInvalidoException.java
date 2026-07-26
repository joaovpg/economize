package com.joaovpg.economize.transacao;

public class TipoTransacaoInvalidoException extends RuntimeException {
    public TipoTransacaoInvalidoException() {
        super("Transferencias devem ser criadas pelo modulo transferencia");
    }
}

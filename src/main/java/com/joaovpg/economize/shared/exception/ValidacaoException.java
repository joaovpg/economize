package com.joaovpg.economize.shared.exception;

public class ValidacaoException extends RuntimeException {
  private final String campo;

  public ValidacaoException(String campo, String mensagem) {
    super(mensagem);
    this.campo = campo;
  }

  public String campo() {
    return campo;
  }
}

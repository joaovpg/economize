package com.joaovpg.economize.shared.exception;

public class RecursoNaoEncontradoException extends RuntimeException {
  private final String codigo;

  public RecursoNaoEncontradoException(String codigo, String mensagem) {
    super(mensagem);
    this.codigo = codigo;
  }

  public String codigo() {
    return codigo;
  }
}

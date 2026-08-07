package com.joaovpg.economize.recorrencia.enums;

public enum FrequenciaRecorrencia {
  DAILY("DAILY"),
  WEEKLY("WEEKLY"),
  MONTHLY("MONTHLY"),
  YEARLY("YEARLY");

  private final String valorRrule;

  FrequenciaRecorrencia(String valorRrule) {
    this.valorRrule = valorRrule;
  }

  public String valorRrule() {
    return valorRrule;
  }
}

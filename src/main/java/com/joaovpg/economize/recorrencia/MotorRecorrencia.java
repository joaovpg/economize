package com.joaovpg.economize.recorrencia;

import java.time.LocalDate;
import java.util.List;

public interface MotorRecorrencia {
  List<OcorrenciaRecorrencia> expandir(RegraRecorrencia regra, LocalDate inicio, LocalDate fim);
}

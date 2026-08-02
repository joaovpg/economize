package com.joaovpg.economize.transacao.http;

import com.joaovpg.economize.shared.http.ErroResponse;
import com.joaovpg.economize.transacao.application.ConsultaTransacoesInvalidaException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class ConsultaTransacoesInvalidaMapper
    implements ExceptionMapper<ConsultaTransacoesInvalidaException> {
  @Override
  public Response toResponse(ConsultaTransacoesInvalidaException exception) {
    return Response.status(Response.Status.BAD_REQUEST)
        .entity(
            new ErroResponse(
                "DADOS_INVALIDOS",
                "Um ou mais campos sao invalidos",
                Map.of(exception.campo(), exception.getMessage())))
        .build();
  }
}

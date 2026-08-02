package com.joaovpg.economize.shared.http;

import com.joaovpg.economize.shared.RecursoNaoEncontradoException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RecursoNaoEncontradoMapper implements ExceptionMapper<RecursoNaoEncontradoException> {
  @Override
  public Response toResponse(RecursoNaoEncontradoException exception) {
    return Response.status(Response.Status.NOT_FOUND)
        .entity(new ErroResponse(exception.codigo(), exception.getMessage()))
        .build();
  }
}

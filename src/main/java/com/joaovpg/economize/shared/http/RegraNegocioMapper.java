package com.joaovpg.economize.shared.http;

import com.joaovpg.economize.shared.RegraNegocioException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RegraNegocioMapper implements ExceptionMapper<RegraNegocioException> {
  @Override
  public Response toResponse(RegraNegocioException exception) {
    return Response.status(422)
        .entity(new ErroResponse(exception.codigo(), exception.getMessage()))
        .build();
  }
}

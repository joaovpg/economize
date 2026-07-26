package com.joaovpg.economize.shared.http;

import com.joaovpg.economize.shared.AutenticacaoException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AutenticacaoMapper implements ExceptionMapper<AutenticacaoException> {
    @Override
    public Response toResponse(AutenticacaoException exception) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErroResponse(exception.codigo(), exception.getMessage()))
                .build();
    }
}

package com.joaovpg.economize.usuario.http;

import com.joaovpg.economize.compartilhado.http.ErroResponse;
import com.joaovpg.economize.usuario.CredenciaisInvalidasException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CredenciaisInvalidasMapper implements ExceptionMapper<CredenciaisInvalidasException> {
    @Override
    public Response toResponse(CredenciaisInvalidasException exception) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErroResponse("CREDENCIAIS_INVALIDAS", exception.getMessage()))
                .build();
    }
}

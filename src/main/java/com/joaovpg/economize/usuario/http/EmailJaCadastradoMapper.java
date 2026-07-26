package com.joaovpg.economize.usuario.http;

import com.joaovpg.economize.compartilhado.http.ErroResponse;
import com.joaovpg.economize.usuario.EmailJaCadastradoException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class EmailJaCadastradoMapper implements ExceptionMapper<EmailJaCadastradoException> {
    @Override
    public Response toResponse(EmailJaCadastradoException exception) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErroResponse("EMAIL_JA_CADASTRADO", exception.getMessage()))
                .build();
    }
}

package com.joaovpg.economize.transacao.http;

import com.joaovpg.economize.compartilhado.http.ErroResponse;
import com.joaovpg.economize.transacao.RecursoFinanceiroNaoEncontradoException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RecursoFinanceiroNaoEncontradoMapper implements ExceptionMapper<RecursoFinanceiroNaoEncontradoException> {
    @Override
    public Response toResponse(RecursoFinanceiroNaoEncontradoException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErroResponse("RECURSO_NAO_ENCONTRADO", exception.getMessage()))
                .build();
    }
}

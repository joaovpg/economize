package com.joaovpg.economize.usuario.http;

import com.joaovpg.economize.usuario.AutenticarUsuario;
import com.joaovpg.economize.usuario.http.dto.request.LoginRequest;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/autenticacao")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AutenticacaoResource {
    private final AutenticarUsuario autenticarUsuario;
    private final AutenticacaoHttpMapper mapper;

    public AutenticacaoResource(AutenticarUsuario autenticarUsuario, AutenticacaoHttpMapper mapper) {
        this.autenticarUsuario = autenticarUsuario;
        this.mapper = mapper;
    }

    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid LoginRequest request) {
        var comando = mapper.toCommand(request);
        var resultado = autenticarUsuario.executar(comando);
        var response = mapper.toResponse(resultado);
        return Response.ok(response).build();
    }
}

package com.joaovpg.economize.categoria.http;

import com.joaovpg.economize.categoria.SituacaoCategoria;
import com.joaovpg.economize.categoria.application.CadastrarCategoria;
import com.joaovpg.economize.categoria.application.EditarCategoria;
import com.joaovpg.economize.categoria.application.ListarCategorias;
import com.joaovpg.economize.categoria.http.dto.request.CadastrarCategoriaRequest;
import com.joaovpg.economize.categoria.http.dto.request.EditarCategoriaRequest;
import com.joaovpg.economize.shared.http.LogHttpErrors;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/categorias")
@LogHttpErrors
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("usuario")
public class CategoriaResource {
  private final CadastrarCategoria cadastrarCategoria;
  private final EditarCategoria editarCategoria;
  private final ListarCategorias listarCategorias;
  private final CategoriaHttpMapper mapper;
  private final JsonWebToken token;

  public CategoriaResource(
      CadastrarCategoria cadastrarCategoria,
      EditarCategoria editarCategoria,
      ListarCategorias listarCategorias,
      CategoriaHttpMapper mapper,
      JsonWebToken token) {
    this.cadastrarCategoria = cadastrarCategoria;
    this.editarCategoria = editarCategoria;
    this.listarCategorias = listarCategorias;
    this.mapper = mapper;
    this.token = token;
  }

  @POST
  public Response cadastrar(@Valid CadastrarCategoriaRequest request) {
    var resultado = cadastrarCategoria.executar(mapper.toCommand(usuarioId(), request));

    return Response.status(Response.Status.CREATED).entity(mapper.toResponse(resultado)).build();
  }

  @PUT
  @Path("/{categoriaId}")
  public Response editar(
      @PathParam("categoriaId") UUID categoriaId, @Valid EditarCategoriaRequest request) {
    var resultado = editarCategoria.executar(mapper.toCommand(usuarioId(), categoriaId, request));
    return Response.ok(mapper.toResponse(resultado)).build();
  }

  @GET
  public Response listar(@QueryParam("situacao") SituacaoCategoria situacao) {
    var resposta =
        listarCategorias.executar(usuarioId(), situacao).stream().map(mapper::toResponse).toList();
    return Response.ok(resposta).build();
  }

  private UUID usuarioId() {
    return UUID.fromString(token.getSubject());
  }
}

package com.joaovpg.economize.categoria;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.joaovpg.economize.usuario.StatusUsuario;
import com.joaovpg.economize.usuario.Usuario;
import com.joaovpg.economize.usuario.UsuarioRepository;
import de.mkammerer.argon2.Argon2Factory;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CategoriaResourceTest {
  @Inject UsuarioRepository usuarioRepository;

  private String email;
  private String outroEmail;

  @BeforeEach
  @Transactional
  void prepararUsuario() {
    email = "categoria-" + UUID.randomUUID() + "@example.com";
    var usuario = new Usuario();
    usuario.setNome("Pessoa de teste");
    usuario.setEmail(email);
    usuario.setSenhaHash(Argon2Factory.create().hash(2, 19_456, 1, "senha-segura".toCharArray()));
    usuario.setTimezone("America/Sao_Paulo");
    usuario.setStatus(StatusUsuario.ATIVO);
    usuarioRepository.persist(usuario);
    outroEmail = "outro-" + UUID.randomUUID() + "@example.com";
    criarUsuario(outroEmail);
  }

  @Test
  void cadastraCategoriaAtivaComDadosNormalizados() {
    given()
        .auth()
        .oauth2(autenticar())
        .contentType("application/json")
        .body(
            """
            {
              "nome":"  Moradia  ",
              "cor":"#33aaff",
              "categoriaPaiId":null
            }
            """)
        .when()
        .post("/api/categorias")
        .then()
        .statusCode(201)
        .body("id", notNullValue())
        .body("nome", equalTo("Moradia"))
        .body("cor", equalTo("#33AAFF"))
        .body("situacao", equalTo("ATIVA"));
  }

  @Test
  void rejeitaNomeDuplicadoNoMesmoNivelSemDiferenciarCaixa() {
    var token = autenticar();
    cadastrar(token, "Moradia", null);

    given()
        .auth()
        .oauth2(token)
        .contentType("application/json")
        .body(
            """
            {"nome":"moradia","cor":null,"categoriaPaiId":null}
            """)
        .when()
        .post("/api/categorias")
        .then()
        .statusCode(422)
        .body("codigo", equalTo("NOME_CATEGORIA_DUPLICADO"));
  }

  @Test
  void editaCategoriaEListaEstadosEmOrdemAlfabetica() {
    var token = autenticar();
    var moradiaId = cadastrar(token, "Moradia", null);
    var moveisId = cadastrar(token, "Moveis", moradiaId);

    given()
        .auth()
        .oauth2(token)
        .contentType("application/json")
        .body(
            """
            {
              "nome":" Decoracao ",
              "cor":"#abcdef",
              "categoriaPaiId":null,
              "situacao":"INATIVA"
            }
            """)
        .when()
        .put("/api/categorias/{id}", moveisId)
        .then()
        .statusCode(200)
        .body("nome", equalTo("Decoracao"))
        .body("cor", equalTo("#ABCDEF"))
        .body("categoriaPaiId", equalTo(null))
        .body("situacao", equalTo("INATIVA"));

    given()
        .auth()
        .oauth2(token)
        .queryParam("situacao", "ATIVA")
        .when()
        .get("/api/categorias")
        .then()
        .statusCode(200)
        .body("", hasSize(1))
        .body("[0].id", equalTo(moradiaId));

    given()
        .auth()
        .oauth2(token)
        .when()
        .get("/api/categorias")
        .then()
        .statusCode(200)
        .body("nome", equalTo(java.util.List.of("Decoracao", "Moradia")));
  }

  @Test
  void rejeitaCicloIndiretoNaHierarquia() {
    var token = autenticar();
    var moradiaId = cadastrar(token, "Moradia", null);
    var moveisId = cadastrar(token, "Moveis", moradiaId);

    editar(token, moradiaId, "Moradia", moveisId, "ATIVA")
        .statusCode(422)
        .body("codigo", equalTo("HIERARQUIA_CATEGORIA_CICLICA"));
  }

  @Test
  void exigeInativacaoDeBaixoParaCimaEAtivacaoDeCimaParaBaixo() {
    var token = autenticar();
    var moradiaId = cadastrar(token, "Moradia", null);
    var moveisId = cadastrar(token, "Moveis", moradiaId);

    editar(token, moradiaId, "Moradia", null, "INATIVA")
        .statusCode(422)
        .body("codigo", equalTo("CATEGORIA_POSSUI_DESCENDENTE_ATIVA"));
    editar(token, moveisId, "Moveis", moradiaId, "INATIVA").statusCode(200);
    editar(token, moradiaId, "Moradia", null, "INATIVA").statusCode(200);
    editar(token, moveisId, "Moveis", moradiaId, "ATIVA")
        .statusCode(422)
        .body("codigo", equalTo("CATEGORIA_POSSUI_ANCESTRAL_INATIVA"));
  }

  @Test
  void permiteAtivarCategoriaAoMoveLaParaUmPaiAtivo() {
    var token = autenticar();
    var paiInativoId = cadastrar(token, "Arquivadas", null);
    var paiAtivoId = cadastrar(token, "Despesas", null);
    var filhaId = cadastrar(token, "Moradia", paiInativoId);
    editar(token, filhaId, "Moradia", paiInativoId, "INATIVA").statusCode(200);
    editar(token, paiInativoId, "Arquivadas", null, "INATIVA").statusCode(200);

    editar(token, filhaId, "Moradia", paiAtivoId, "ATIVA")
        .statusCode(200)
        .body("categoriaPaiId", equalTo(paiAtivoId))
        .body("situacao", equalTo("ATIVA"));
  }

  @Test
  void ocultaCategoriaDeOutroUsuario() {
    var categoriaId = cadastrar(autenticar(), "Moradia", null);
    email = outroEmail;

    editar(autenticar(), categoriaId, "Invadida", null, "ATIVA")
        .statusCode(404)
        .body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));
  }

  private String cadastrar(String token, String nome, String paiId) {
    return given()
        .auth()
        .oauth2(token)
        .contentType("application/json")
        .body(
            """
            {"nome":"%s","cor":null,"categoriaPaiId":%s}
            """
                .formatted(nome, paiId == null ? "null" : "\"" + paiId + "\""))
        .when()
        .post("/api/categorias")
        .then()
        .statusCode(201)
        .extract()
        .path("id");
  }

  private io.restassured.response.ValidatableResponse editar(
      String token, String categoriaId, String nome, String paiId, String situacao) {
    return given()
        .auth()
        .oauth2(token)
        .contentType("application/json")
        .body(
            """
            {"nome":"%s","cor":null,"categoriaPaiId":%s,"situacao":"%s"}
            """
                .formatted(nome, paiId == null ? "null" : "\"" + paiId + "\"", situacao))
        .when()
        .put("/api/categorias/{id}", categoriaId)
        .then();
  }

  void criarUsuario(String emailUsuario) {
    var usuario = new Usuario();
    usuario.setNome("Outra pessoa");
    usuario.setEmail(emailUsuario);
    usuario.setSenhaHash(Argon2Factory.create().hash(2, 19_456, 1, "senha-segura".toCharArray()));
    usuario.setTimezone("America/Sao_Paulo");
    usuario.setStatus(StatusUsuario.ATIVO);
    usuarioRepository.persist(usuario);
  }

  private String autenticar() {
    return given()
        .contentType("application/json")
        .body(
            """
            {"email":"%s","senha":"senha-segura"}
            """
                .formatted(email))
        .when()
        .post("/api/autenticacao/login")
        .then()
        .statusCode(200)
        .extract()
        .path("token");
  }
}

package com.joaovpg.economize.transacao;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.joaovpg.economize.categoria.Categoria;
import com.joaovpg.economize.categoria.CategoriaRepository;
import com.joaovpg.economize.conta.ContaFinanceira;
import com.joaovpg.economize.conta.ContaFinanceiraRepository;
import com.joaovpg.economize.recorrencia.GrupoRecorrencia;
import com.joaovpg.economize.recorrencia.GrupoRecorrenciaRepository;
import com.joaovpg.economize.recorrencia.StatusRecorrencia;
import com.joaovpg.economize.usuario.StatusUsuario;
import com.joaovpg.economize.usuario.Usuario;
import com.joaovpg.economize.usuario.UsuarioRepository;
import de.mkammerer.argon2.Argon2Factory;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TransacaoResourceTest {
    private static final ZoneId FUSO_USUARIO = ZoneId.of("Pacific/Pago_Pago");
    private static final ZoneId FUSO_OUTRO_USUARIO = ZoneId.of("Pacific/Kiritimati");

    @Inject UsuarioRepository usuarioRepository;
    @Inject ContaFinanceiraRepository contaRepository;
    @Inject CategoriaRepository categoriaRepository;
    @Inject TransacaoRepository transacaoRepository;
    @Inject GrupoRecorrenciaRepository grupoRecorrenciaRepository;

    private String email;
    private String emailOutroUsuario;
    private UUID contaId;
    private UUID contaInativaId;
    private UUID contaSecundariaId;
    private UUID contaOutroUsuarioId;
    private UUID categoriaId;
    private UUID categoriaInativaId;
    private UUID categoriaOutroUsuarioId;

    @BeforeEach
    @Transactional
    void prepararDados() {
        email = "transacao-" + UUID.randomUUID() + "@example.com";
        var argon2 = Argon2Factory.create();
        var usuario = new Usuario();
        usuario.setNome("Pessoa de teste");
        usuario.setEmail(email);
        usuario.setSenhaHash(argon2.hash(2, 19_456, 1, "senha-segura".toCharArray()));
        usuario.setTimezone(FUSO_USUARIO.getId());
        usuario.setStatus(StatusUsuario.ATIVO);
        usuarioRepository.persist(usuario);

        var conta = new ContaFinanceira();
        conta.setUsuario(usuario);
        conta.setNome("Conta principal");
        conta.setMoeda("BRL");
        conta.setSaldoInicial(BigDecimal.ZERO);
        conta.setDataSaldoInicial(LocalDate.of(2026, 1, 1));
        contaRepository.persist(conta);

        var contaInativa = new ContaFinanceira();
        contaInativa.setUsuario(usuario);
        contaInativa.setNome("Conta inativa");
        contaInativa.setMoeda("BRL");
        contaInativa.setSaldoInicial(BigDecimal.ZERO);
        contaInativa.setDataSaldoInicial(LocalDate.of(2026, 1, 1));
        contaInativa.setAtivo(false);
        contaRepository.persist(contaInativa);

        var contaSecundaria = new ContaFinanceira();
        contaSecundaria.setUsuario(usuario);
        contaSecundaria.setNome("Conta secundaria");
        contaSecundaria.setMoeda("BRL");
        contaSecundaria.setSaldoInicial(BigDecimal.ZERO);
        contaSecundaria.setDataSaldoInicial(LocalDate.of(2026, 2, 1));
        contaRepository.persist(contaSecundaria);

        var categoria = novaCategoria(usuario, "Alimentacao", true);
        var categoriaInativa = novaCategoria(usuario, "Arquivada", false);
        contaRepository.flush();
        contaId = conta.getId();
        contaInativaId = contaInativa.getId();
        contaSecundariaId = contaSecundaria.getId();
        categoriaId = categoria.getId();
        categoriaInativaId = categoriaInativa.getId();

        var outroUsuario = new Usuario();
        outroUsuario.setNome("Outra pessoa");
        emailOutroUsuario = "outra-" + UUID.randomUUID() + "@example.com";
        outroUsuario.setEmail(emailOutroUsuario);
        outroUsuario.setSenhaHash(argon2.hash(2, 19_456, 1, "outra-senha".toCharArray()));
        outroUsuario.setTimezone(FUSO_OUTRO_USUARIO.getId());
        outroUsuario.setStatus(StatusUsuario.ATIVO);
        usuarioRepository.persist(outroUsuario);
        var contaOutroUsuario = new ContaFinanceira();
        contaOutroUsuario.setUsuario(outroUsuario);
        contaOutroUsuario.setNome("Conta de outra pessoa");
        contaOutroUsuario.setMoeda("BRL");
        contaOutroUsuario.setSaldoInicial(BigDecimal.ZERO);
        contaOutroUsuario.setDataSaldoInicial(LocalDate.of(2026, 1, 1));
        contaRepository.persist(contaOutroUsuario);
        var categoriaOutroUsuario = novaCategoria(outroUsuario, "Categoria alheia", true);
        contaRepository.flush();
        contaOutroUsuarioId = contaOutroUsuario.getId();
        categoriaOutroUsuarioId = categoriaOutroUsuario.getId();
    }

    @Test
    void criaTransacaoPlanejadaParaUsuarioAutenticado() {
        String token = autenticar();

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                        {
                          "contaId":"%s",
                          "situacao":"PLANEJADA",
                          "tipo":"DESPESA",
                          "descricao":"Mercado",
                          "valor":125.4500,
                          "dataFinanceira":"2026-07-26"
                        }
                        """.formatted(contaId))
                .when().post("/api/transacoes")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("situacao", equalTo("PLANEJADA"))
                .body("tipo", equalTo("DESPESA"))
                .body("descricao", equalTo("Mercado"))
                .body("dataFinanceira", equalTo("2026-07-26"))
                .body("efetivadoEm", nullValue());
    }

    @Test
    void criaReceitaEfetivadaNaDataAtualDoFusoDoUsuario() {
        var hoje = LocalDate.now(FUSO_USUARIO);

        criarTransacaoEfetivada(autenticar(), "RECEITA", hoje, categoriaId)
                .statusCode(201)
                .body("situacao", equalTo("EFETIVADA"))
                .body("tipo", equalTo("RECEITA"))
                .body("dataFinanceira", equalTo(hoje.toString()))
                .body("categoriaId", equalTo(categoriaId.toString()))
                .body("efetivadoEm", matchesPattern("^\\d{4}-\\d{2}-\\d{2}T.*Z$"));
    }

    @Test
    void criaDespesaEfetivadaComDataPassada() {
        var ontem = LocalDate.now(FUSO_USUARIO).minusDays(1);

        criarTransacaoEfetivada(autenticar(), "DESPESA", ontem, null)
                .statusCode(201)
                .body("situacao", equalTo("EFETIVADA"))
                .body("tipo", equalTo("DESPESA"))
                .body("dataFinanceira", equalTo(ontem.toString()))
                .body("efetivadoEm", notNullValue());
    }

    @Test
    void rejeitaTransacaoEfetivadaComDataFuturaNoFusoDoUsuario() {
        var amanha = LocalDate.now(FUSO_USUARIO).plusDays(1);

        criarTransacaoEfetivada(autenticar(), "DESPESA", amanha, null)
                .statusCode(422)
                .body("codigo", equalTo("DATA_FINANCEIRA_FUTURA"));
    }

    @Test
    void rejeitaCategoriaInativaNaCriacaoEfetivada() {
        criarTransacaoEfetivada(autenticar(), "DESPESA",
                LocalDate.now(FUSO_USUARIO), categoriaInativaId)
                .statusCode(404)
                .body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));
    }

    @Test
    void rejeitaContaInativaNaCriacaoEfetivada() {
        criarTransacaoEfetivada(autenticar(), contaInativaId, "DESPESA",
                LocalDate.now(FUSO_USUARIO), null)
                .statusCode(404)
                .body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));
    }

    @Test
    void ocultaCategoriaDeOutroUsuarioNaCriacaoEfetivada() {
        criarTransacaoEfetivada(autenticar(), "DESPESA",
                LocalDate.now(FUSO_USUARIO), categoriaOutroUsuarioId)
                .statusCode(404)
                .body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));
    }

    @Test
    void consideraODateLineAoValidarDataEfetivadaNoFusoDoUsuario() {
        var hojeEmKiritimati = LocalDate.now(FUSO_OUTRO_USUARIO);

        criarTransacaoEfetivada(autenticarOutroUsuario(), contaOutroUsuarioId, "RECEITA",
                hojeEmKiritimati, categoriaOutroUsuarioId)
                .statusCode(201);
        criarTransacaoEfetivada(autenticar(), "RECEITA", hojeEmKiritimati, null)
                .statusCode(422)
                .body("codigo", equalTo("DATA_FINANCEIRA_FUTURA"));
    }

    @Test
    void rejeitaTransacaoEfetivadaAnteriorADataDoSaldoInicial() {
        criarTransacaoEfetivada(autenticar(), "DESPESA", LocalDate.of(2025, 12, 31), null)
                .statusCode(422)
                .body("codigo", equalTo("DATA_FINANCEIRA_ANTERIOR_SALDO_INICIAL"));
    }

    @Test
    void exigeSituacaoExplicita() {
        given()
                .auth().oauth2(autenticar())
                .contentType("application/json")
                .body("""
                        {
                          "contaId":"%s",
                          "tipo":"DESPESA",
                          "descricao":"Mercado",
                          "valor":10.00,
                          "dataFinanceira":"2026-07-26"
                        }
                        """.formatted(contaId))
                .when().post("/api/transacoes")
                .then().statusCode(400);
    }

    @Test
    void rejeitaNomesLegadosDoContrato() {
        given()
                .auth().oauth2(autenticar())
                .contentType("application/json")
                .body("""
                        {
                          "contaId":"%s",
                          "situacao":"PLANEJADA",
                          "status":"PLANEJADA",
                          "tipo":"DESPESA",
                          "descricao":"Mercado",
                          "valor":10.00,
                          "dataFinanceira":"2026-07-26",
                          "dataVencimento":"2026-07-26"
                        }
                        """.formatted(contaId))
                .when().post("/api/transacoes")
                .then().statusCode(400);
    }

    @Test
    void rejeitaCriacaoSemAutenticacao() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "contaId":"%s",
                          "situacao":"PLANEJADA",
                          "tipo":"DESPESA",
                          "descricao":"Mercado",
                          "valor":10.00,
                          "dataFinanceira":"2026-07-26"
                        }
                        """.formatted(contaId))
                .when().post("/api/transacoes")
                .then().statusCode(401);
    }

    @Test
    void rejeitaCredenciaisInvalidasSemExporDetalhes() {
        given()
                .contentType("application/json")
                .body("""
                        {"email":"%s","senha":"incorreta"}
                        """.formatted(email))
                .when().post("/api/autenticacao/login")
                .then()
                .statusCode(401)
                .body("codigo", equalTo("CREDENCIAIS_INVALIDAS"));
    }

    @Test
    void autenticaComTokenBearer() {
        given()
                .contentType("application/json")
                .body("""
                        {"email":"%s","senha":"senha-segura"}
                        """.formatted(email))
                .when().post("/api/autenticacao/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("tipo", equalTo("Bearer"));
    }

    @Test
    void rejeitaTipoTransferencia() {
        given()
                .auth().oauth2(autenticar())
                .contentType("application/json")
                .body("""
                        {
                          "contaId":"%s",
                          "situacao":"PLANEJADA",
                          "tipo":"TRANSFERENCIA",
                          "descricao":"Movimentacao",
                          "valor":10.00,
                          "dataFinanceira":"2026-07-26"
                        }
                        """.formatted(contaId))
                .when().post("/api/transacoes")
                .then()
                .statusCode(422)
                .body("codigo", equalTo("TIPO_TRANSACAO_INVALIDO"))
                .body("campos", anEmptyMap());
    }

    @Test
    void ocultaContaDeOutroUsuario() {
        given()
                .auth().oauth2(autenticar())
                .contentType("application/json")
                .body("""
                        {
                          "contaId":"%s",
                          "situacao":"PLANEJADA",
                          "tipo":"DESPESA",
                          "descricao":"Movimentacao",
                          "valor":10.00,
                          "dataFinanceira":"2026-07-26"
                        }
                        """.formatted(contaOutroUsuarioId))
                .when().post("/api/transacoes")
                .then()
                .statusCode(404)
                .body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));
    }

    @Test
    void bloqueiaAlteracaoDosDadosIniciaisAposPrimeiraTransacao() {
        var token = autenticar();
        criarTransacao(token, "2026-07-26").statusCode(201);

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                        {
                          "nome":"Conta principal",
                          "moeda":"BRL",
                          "saldoInicial":1,
                          "dataSaldoInicial":"2026-01-01",
                          "situacao":"ATIVA"
                        }
                        """)
                .when().put("/api/contas/{id}", contaId)
                .then()
                .statusCode(422)
                .body("codigo", equalTo("DADOS_INICIAIS_CONTA_IMUTAVEIS"));
    }

    @Test
    void rejeitaTransacaoAnteriorADataDoSaldoInicial() {
        criarTransacao(autenticar(), "2025-12-31")
                .statusCode(422)
                .body("codigo", equalTo("DATA_FINANCEIRA_ANTERIOR_SALDO_INICIAL"));
    }

    @Test
    void permiteAlterarNomeESituacaoPreservandoDadosIniciaisBloqueados() {
        var token = autenticar();
        criarTransacao(token, "2026-07-26").statusCode(201);

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                        {
                          "nome":"Reserva",
                          "moeda":"BRL",
                          "saldoInicial":0.000,
                          "dataSaldoInicial":"2026-01-01",
                          "situacao":"INATIVA"
                        }
                        """)
                .when().put("/api/contas/{id}", contaId)
                .then()
                .statusCode(200)
                .body("nome", equalTo("Reserva"))
                .body("situacao", equalTo("INATIVA"));
    }

    @Test
    void excluiFisicamenteTransacaoPlanejadaERejeitaNovaExclusao() {
        var token = autenticar();
        UUID transacaoId = criarTransacao(token, "2026-07-26")
                .statusCode(201)
                .extract().jsonPath().getUUID("id");

        excluirTransacao(token, transacaoId).statusCode(204);
        excluirTransacao(token, transacaoId)
                .statusCode(404)
                .body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));

        QuarkusTransaction.requiringNew().run(() ->
                org.junit.jupiter.api.Assertions.assertFalse(transacaoRepository.findByIdOptional(transacaoId).isPresent()));
    }

    @Test
    void excluiFisicamenteTransacaoEfetivadaSemLiberarDadosIniciaisDaConta() {
        var token = autenticar();
        UUID transacaoId = criarTransacaoEfetivada(token, "DESPESA", LocalDate.now(FUSO_USUARIO), null)
                .statusCode(201)
                .extract().jsonPath().getUUID("id");

        excluirTransacao(token, transacaoId).statusCode(204);

        QuarkusTransaction.requiringNew().run(() ->
                org.junit.jupiter.api.Assertions.assertTrue(
                        contaRepository.findById(contaId).isDadosIniciaisBloqueados()));
    }

    @Test
    void ocultaTransacaoDeOutroUsuarioNaExclusao() {
        UUID transacaoId = criarTransacaoEfetivada(
                autenticarOutroUsuario(), contaOutroUsuarioId, "RECEITA",
                LocalDate.now(FUSO_OUTRO_USUARIO), categoriaOutroUsuarioId)
                .statusCode(201)
                .extract().jsonPath().getUUID("id");

        excluirTransacao(autenticar(), transacaoId)
                .statusCode(404)
                .body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));
        excluirTransacao(autenticarOutroUsuario(), transacaoId).statusCode(204);
    }

    @Test
    void retornaNaoEncontradoParaIdentificadorInexistente() {
        excluirTransacao(autenticar(), UUID.randomUUID())
                .statusCode(404)
                .body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));
    }

    @Test
    void rejeitaExclusaoDeTransacaoQuePertenceAOutroFluxo() {
        var token = autenticar();
        UUID transacaoId = criarTransacao(token, "2026-07-26")
                .statusCode(201)
                .extract().jsonPath().getUUID("id");
        QuarkusTransaction.requiringNew().run(() ->
                transacaoRepository.findById(transacaoId).setTipo(TipoTransacao.TRANSFERENCIA));

        excluirTransacao(token, transacaoId)
                .statusCode(422)
                .body("codigo", equalTo("TRANSACAO_NAO_SIMPLES"));
    }

    @Test
    void rejeitaExclusaoDeTransacaoVinculadaARecorrencia() {
        var token = autenticar();
        UUID transacaoId = criarTransacao(token, "2026-07-26")
                .statusCode(201)
                .extract().jsonPath().getUUID("id");
        QuarkusTransaction.requiringNew().run(() -> {
            var transacao = transacaoRepository.findById(transacaoId);
            var grupo = new GrupoRecorrencia();
            grupo.setUsuario(transacao.getUsuario());
            grupo.setDescricao("Recorrencia de teste");
            grupo.setStatus(StatusRecorrencia.ATIVO);
            grupoRecorrenciaRepository.persist(grupo);
            transacao.setGrupoRecorrencia(grupo);
            transacao.setExcecaoRecorrencia(true);
        });

        excluirTransacao(token, transacaoId)
                .statusCode(422)
                .body("codigo", equalTo("TRANSACAO_NAO_SIMPLES"));
    }

    @Test
    void exigeAutenticacaoParaExcluirTransacao() {
        given()
                .when().delete("/api/transacoes/{id}", UUID.randomUUID())
                .then().statusCode(401);
    }

    @Test
    void alteraTodosOsCamposMutaveisEEfetivaTransacao() {
        var token = autenticar();
        var transacaoId = criarTransacao(token, "2026-07-26").statusCode(201)
                .extract().jsonPath().getUUID("id");
        var hoje = LocalDate.now(FUSO_USUARIO);

        alterarTransacao(token, transacaoId, contaSecundariaId, categoriaId, "EFETIVADA", "RECEITA",
                "  Salario corrigido  ", "  Observacao corrigida  ", "987.6543", hoje)
                .statusCode(200)
                .body("id", equalTo(transacaoId.toString()))
                .body("contaId", equalTo(contaSecundariaId.toString()))
                .body("categoriaId", equalTo(categoriaId.toString()))
                .body("situacao", equalTo("EFETIVADA"))
                .body("tipo", equalTo("RECEITA"))
                .body("descricao", equalTo("Salario corrigido"))
                .body("observacoes", equalTo("Observacao corrigida"))
                .body("valor", equalTo(987.6543F))
                .body("dataFinanceira", equalTo(hoje.toString()))
                .body("efetivadoEm", notNullValue());
    }

    @Test
    void preservaInstanteAoCorrigirTransacaoQuePermaneceEfetivada() {
        var token = autenticar();
        var ontem = LocalDate.now(FUSO_USUARIO).minusDays(1);
        var respostaCriacao = criarTransacaoEfetivada(token, "DESPESA", ontem, categoriaId)
                .statusCode(201).extract();
        var transacaoId = respostaCriacao.jsonPath().getUUID("id");
        var efetivadoEmOriginal = QuarkusTransaction.requiringNew().call(() ->
                transacaoRepository.findById(transacaoId).getEfetivadoEm());

        var respostaAlteracao = alterarTransacao(token, transacaoId, contaId, categoriaId, "EFETIVADA", "DESPESA",
                "Descricao corrigida", null, "20.0000", ontem.minusDays(1))
                .statusCode(200).extract();

        assertEquals(efetivadoEmOriginal,
                Instant.parse(respostaAlteracao.jsonPath().getString("efetivadoEm")));
    }

    @Test
    void replanejaLimpaInstanteENovaEfetivacaoRegistraOutro() {
        var token = autenticar();
        var hoje = LocalDate.now(FUSO_USUARIO);
        var respostaCriacao = criarTransacaoEfetivada(token, "DESPESA", hoje, null)
                .statusCode(201).extract();
        var transacaoId = respostaCriacao.jsonPath().getUUID("id");
        var efetivadoEmOriginal = QuarkusTransaction.requiringNew().call(() ->
                transacaoRepository.findById(transacaoId).getEfetivadoEm());

        alterarTransacao(token, transacaoId, contaId, null, "PLANEJADA", "DESPESA",
                "Replanejada", null, "10.0000", hoje.plusDays(1))
                .statusCode(200)
                .body("efetivadoEm", nullValue());
        alterarTransacao(token, transacaoId, contaId, null, "EFETIVADA", "DESPESA",
                "Efetivada novamente", null, "10.0000", hoje)
                .statusCode(200)
                .body("efetivadoEm", notNullValue());

        QuarkusTransaction.requiringNew().run(() ->
                assertNotEquals(efetivadoEmOriginal, transacaoRepository.findById(transacaoId).getEfetivadoEm()));
    }

    @Test
    void permiteManterContaECategoriaQueForamInativadas() {
        var token = autenticar();
        var transacaoId = criarTransacaoEfetivada(token, "DESPESA", LocalDate.now(FUSO_USUARIO), categoriaId)
                .statusCode(201).extract().jsonPath().getUUID("id");
        QuarkusTransaction.requiringNew().run(() -> {
            contaRepository.findById(contaId).setAtivo(false);
            categoriaRepository.findById(categoriaId).setAtivo(false);
        });

        alterarTransacao(token, transacaoId, contaId, categoriaId, "EFETIVADA", "RECEITA",
                "Correcao historica", null, "15.0000", LocalDate.now(FUSO_USUARIO))
                .statusCode(200)
                .body("contaId", equalTo(contaId.toString()))
                .body("categoriaId", equalTo(categoriaId.toString()));
    }

    @Test
    void rejeitaNovasAssociacoesInativasEPreservaEstadoAnterior() {
        var token = autenticar();
        var transacaoId = criarTransacao(token, "2026-07-26").statusCode(201)
                .extract().jsonPath().getUUID("id");

        alterarTransacao(token, transacaoId, contaInativaId, categoriaInativaId, "EFETIVADA", "RECEITA",
                "Nao deve persistir", "Falha atomica", "99.0000", LocalDate.now(FUSO_USUARIO))
                .statusCode(404)
                .body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));

        QuarkusTransaction.requiringNew().run(() -> {
            var transacao = transacaoRepository.findById(transacaoId);
            assertEquals(contaId, transacao.getConta().getId());
            assertNull(transacao.getCategoria());
            assertEquals(SituacaoTransacao.PLANEJADA, transacao.getSituacao());
            assertEquals(TipoTransacao.DESPESA, transacao.getTipo());
            assertEquals("Mercado", transacao.getDescricao());
            assertEquals(new BigDecimal("10.0000"), transacao.getValor());
        });
    }

    @Test
    void rejeitaDataFuturaAoEfetivarEDataAnteriorAoSaldoDaNovaConta() {
        var token = autenticar();
        var transacaoId = criarTransacao(token, "2026-07-26").statusCode(201)
                .extract().jsonPath().getUUID("id");

        alterarTransacao(token, transacaoId, contaId, null, "EFETIVADA", "DESPESA",
                "Futura", null, "10.0000", LocalDate.now(FUSO_USUARIO).plusDays(1))
                .statusCode(422).body("codigo", equalTo("DATA_FINANCEIRA_FUTURA"));
        alterarTransacao(token, transacaoId, contaSecundariaId, null, "PLANEJADA", "DESPESA",
                "Anterior", null, "10.0000", LocalDate.of(2026, 1, 31))
                .statusCode(422).body("codigo", equalTo("DATA_FINANCEIRA_ANTERIOR_SALDO_INICIAL"));
    }

    @Test
    void ocultaTransacaoAlheiaERejeitaTransacaoVinculadaNaAlteracao() {
        var tokenOutroUsuario = autenticarOutroUsuario();
        var transacaoAlheiaId = criarTransacaoEfetivada(tokenOutroUsuario, contaOutroUsuarioId, "RECEITA",
                LocalDate.now(FUSO_OUTRO_USUARIO), categoriaOutroUsuarioId)
                .statusCode(201).extract().jsonPath().getUUID("id");
        alterarTransacao(autenticar(), transacaoAlheiaId, contaId, null, "PLANEJADA", "DESPESA",
                "Oculta", null, "10.0000", LocalDate.now(FUSO_USUARIO))
                .statusCode(404).body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));

        var token = autenticar();
        var vinculadaId = criarTransacao(token, "2026-07-26").statusCode(201)
                .extract().jsonPath().getUUID("id");
        QuarkusTransaction.requiringNew().run(() ->
                transacaoRepository.findById(vinculadaId).setTipo(TipoTransacao.TRANSFERENCIA));
        alterarTransacao(token, vinculadaId, contaId, null, "PLANEJADA", "DESPESA",
                "Vinculada", null, "10.0000", LocalDate.now(FUSO_USUARIO))
                .statusCode(422).body("codigo", equalTo("TRANSACAO_NAO_SIMPLES"));
    }

    @Test
    void rejeitaTipoTransferenciaEExigeEstadoCompletoNaAlteracao() {
        var token = autenticar();
        var transacaoId = criarTransacao(token, "2026-07-26").statusCode(201)
                .extract().jsonPath().getUUID("id");

        alterarTransacao(token, transacaoId, contaId, null, "PLANEJADA", "TRANSFERENCIA",
                "Transferencia", null, "10.0000", LocalDate.now(FUSO_USUARIO))
                .statusCode(422).body("codigo", equalTo("TIPO_TRANSACAO_INVALIDO"));
        given().auth().oauth2(token).contentType("application/json")
                .body("{\"descricao\":\"Incompleta\"}")
                .when().put("/api/transacoes/{id}", transacaoId)
                .then().statusCode(400);
    }

    @Test
    void rejeitaRecursosInexistentesOuAlheiosNaAlteracao() {
        var token = autenticar();
        var transacaoId = criarTransacao(token, "2026-07-26").statusCode(201)
                .extract().jsonPath().getUUID("id");
        var hoje = LocalDate.now(FUSO_USUARIO);

        alterarTransacao(token, transacaoId, UUID.randomUUID(), null, "PLANEJADA", "DESPESA",
                "Conta inexistente", null, "10.0000", hoje)
                .statusCode(404).body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));
        alterarTransacao(token, transacaoId, contaOutroUsuarioId, null, "PLANEJADA", "DESPESA",
                "Conta alheia", null, "10.0000", hoje)
                .statusCode(404).body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));
        alterarTransacao(token, transacaoId, contaId, UUID.randomUUID(), "PLANEJADA", "DESPESA",
                "Categoria inexistente", null, "10.0000", hoje)
                .statusCode(404).body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));
        alterarTransacao(token, transacaoId, contaId, categoriaOutroUsuarioId, "PLANEJADA", "DESPESA",
                "Categoria alheia", null, "10.0000", hoje)
                .statusCode(404).body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));
    }

    @Test
    void permiteRemoverCategoriaQueFoiInativada() {
        var token = autenticar();
        var transacaoId = criarTransacaoEfetivada(token, "DESPESA", LocalDate.now(FUSO_USUARIO), categoriaId)
                .statusCode(201).extract().jsonPath().getUUID("id");
        QuarkusTransaction.requiringNew().run(() -> categoriaRepository.findById(categoriaId).setAtivo(false));

        alterarTransacao(token, transacaoId, contaId, null, "EFETIVADA", "DESPESA",
                "Sem categoria", null, "10.0000", LocalDate.now(FUSO_USUARIO))
                .statusCode(200).body("categoriaId", nullValue());
    }

    @Test
    void rejeitaAlteracaoDeTransacaoVinculadaARecorrencia() {
        var token = autenticar();
        var transacaoId = criarTransacao(token, "2026-07-26").statusCode(201)
                .extract().jsonPath().getUUID("id");
        QuarkusTransaction.requiringNew().run(() -> {
            var transacao = transacaoRepository.findById(transacaoId);
            var grupo = new GrupoRecorrencia();
            grupo.setUsuario(transacao.getUsuario());
            grupo.setDescricao("Recorrencia de teste");
            grupo.setStatus(StatusRecorrencia.ATIVO);
            grupoRecorrenciaRepository.persist(grupo);
            transacao.setGrupoRecorrencia(grupo);
            transacao.setExcecaoRecorrencia(true);
        });

        alterarTransacao(token, transacaoId, contaId, null, "PLANEJADA", "DESPESA",
                "Vinculada", null, "10.0000", LocalDate.now(FUSO_USUARIO))
                .statusCode(422).body("codigo", equalTo("TRANSACAO_NAO_SIMPLES"));
    }

    @Test
    void retornaNaoEncontradoEExigeAutenticacaoParaAlterar() {
        var hoje = LocalDate.now(FUSO_USUARIO);
        alterarTransacao(autenticar(), UUID.randomUUID(), contaId, null, "PLANEJADA", "DESPESA",
                "Inexistente", null, "10.0000", hoje)
                .statusCode(404).body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));

        given().contentType("application/json").body("{}")
                .when().put("/api/transacoes/{id}", UUID.randomUUID())
                .then().statusCode(401);
    }

    private io.restassured.response.ValidatableResponse criarTransacao(String token, String dataFinanceira) {
        return given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                        {
                          "contaId":"%s",
                          "situacao":"PLANEJADA",
                          "tipo":"DESPESA",
                          "descricao":"Mercado",
                          "valor":10.00,
                          "dataFinanceira":"%s"
                        }
                        """.formatted(contaId, dataFinanceira))
                .when().post("/api/transacoes")
                .then();
    }

    private io.restassured.response.ValidatableResponse excluirTransacao(String token, UUID transacaoId) {
        return given()
                .auth().oauth2(token)
                .when().delete("/api/transacoes/{id}", transacaoId)
                .then();
    }

    private io.restassured.response.ValidatableResponse alterarTransacao(
            String token, UUID transacaoId, UUID contaId, UUID categoriaId, String situacao, String tipo,
            String descricao, String observacoes, String valor, LocalDate dataFinanceira) {
        var categoria = categoriaId == null ? "null" : "\"" + categoriaId + "\"";
        var observacoesJson = observacoes == null ? "null" : "\"" + observacoes + "\"";
        return given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                        {
                          "contaId":"%s",
                          "categoriaId":%s,
                          "situacao":"%s",
                          "tipo":"%s",
                          "descricao":"%s",
                          "observacoes":%s,
                          "valor":%s,
                          "dataFinanceira":"%s"
                        }
                        """.formatted(contaId, categoria, situacao, tipo, descricao, observacoesJson, valor,
                        dataFinanceira))
                .when().put("/api/transacoes/{id}", transacaoId)
                .then();
    }

    private io.restassured.response.ValidatableResponse criarTransacaoEfetivada(
            String token, String tipo, LocalDate dataFinanceira, UUID categoriaId) {
        return criarTransacaoEfetivada(token, contaId, tipo, dataFinanceira, categoriaId);
    }

    private io.restassured.response.ValidatableResponse criarTransacaoEfetivada(
            String token, UUID contaId, String tipo, LocalDate dataFinanceira, UUID categoriaId) {
        var categoria = categoriaId == null ? "null" : "\"" + categoriaId + "\"";
        return given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                        {
                          "contaId":"%s",
                          "categoriaId":%s,
                          "situacao":"EFETIVADA",
                          "tipo":"%s",
                          "descricao":"Movimentacao efetivada",
                          "valor":10.00,
                          "dataFinanceira":"%s"
                        }
                        """.formatted(contaId, categoria, tipo, dataFinanceira))
                .when().post("/api/transacoes")
                .then();
    }

    private Categoria novaCategoria(Usuario usuario, String nome, boolean ativa) {
        var categoria = new Categoria();
        categoria.setUsuario(usuario);
        categoria.setNome(nome);
        categoria.setAtivo(ativa);
        categoriaRepository.persist(categoria);
        return categoria;
    }

    private String autenticar() {
        return given()
                .contentType("application/json")
                .body("""
                        {"email":"%s","senha":"senha-segura"}
                        """.formatted(email))
                .when().post("/api/autenticacao/login")
                .then().statusCode(200)
                .extract().path("token");
    }

    private String autenticarOutroUsuario() {
        return given()
                .contentType("application/json")
                .body("""
                        {"email":"%s","senha":"outra-senha"}
                        """.formatted(emailOutroUsuario))
                .when().post("/api/autenticacao/login")
                .then().statusCode(200)
                .extract().path("token");
    }
}

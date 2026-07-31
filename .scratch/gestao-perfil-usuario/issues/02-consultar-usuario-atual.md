# 02 - Consultar o Usuario atual

**What to build:** permitir que o Usuario autenticado recupere seu proprio perfil completo para que o frontend possa reconstruir a sessao sem conservar a resposta original do cadastro.

**Blocked by:** 01 - Cadastrar Usuario com nome e sobrenome.

**Status:** ready-for-agent

- [ ] `GET /api/usuarios/atual` retorna `200` com identificador, nome, sobrenome, e-mail e timezone do Usuario autenticado.
- [ ] A consulta identifica o Usuario exclusivamente pelo subject do JWT e nao aceita identificador arbitrario no caminho, query ou corpo.
- [ ] A resposta usa um DTO HTTP e nao expoe entidade, senha, hash, status ou metadados internos de persistencia.
- [ ] Requisicao sem autenticacao retorna `401`.
- [ ] Subject autenticado sem Usuario correspondente retorna o erro padronizado de recurso nao encontrado.
- [ ] O fluxo segue Resource, mapper, caso de uso e repository, com nomes orientados a intencao e sem `Service` generico.
- [ ] Testes HTTP com PostgreSQL real cobrem consulta autenticada e ausencia de autenticacao sem testar detalhes internos.
- [ ] `.\mvnw.cmd verify -B` e aprovado.

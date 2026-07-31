# 01 - Cadastrar Usuario com nome e sobrenome

**What to build:** permitir que um visitante se cadastre informando nome e sobrenome obrigatorios e receba os dois campos, junto ao token da sessao iniciada, em uma entrega completa que evolui o schema e preserva a cadeia de migrations.

**Blocked by:** None - can start immediately.

**Status:** ready-for-agent

- [ ] O cadastro exige `nome` e `sobrenome`, cada um com no maximo 120 caracteres, e remove espacos externos antes de persistir.
- [ ] Nome ou sobrenome ausente, vazio ou acima do limite produz `400` no formato padrao de validacao.
- [ ] O cadastro bem-sucedido continua retornando `201`, inicia a sessao e apresenta identificador, nome, sobrenome, e-mail e timezone do Usuario.
- [ ] O schema recebe sobrenome obrigatorio e nao vazio por migration incremental, sem alterar a migration inicial compartilhada.
- [ ] A migration pode ser aplicada sobre um schema da versao anterior com Usuario existente, preenche o registro legado com `Nao informado` e nao deixa valor padrao para novos registros.
- [ ] A data de nascimento nao e adicionada ao schema nem ao contrato.
- [ ] Fixtures e fluxos existentes que persistem Usuario continuam validos com o novo campo obrigatorio.
- [ ] Testes HTTP cobrem cadastro valido, normalizacao e erros de nome e sobrenome.
- [ ] Um teste de migration PostgreSQL real cobre a evolucao a partir da versao anterior e as restricoes finais da coluna.
- [ ] `.\mvnw.cmd verify -B` e aprovado.

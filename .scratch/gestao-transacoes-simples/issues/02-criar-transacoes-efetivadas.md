# 02 — Criar Transacoes efetivadas

**What to build:** permitir que o Usuario crie uma receita ou despesa ja efetivada, registrando sua Data financeira e o instante da efetivacao com semantica temporal coerente com o fuso cadastrado.

**Blocked by:** 01 — Alinhar a criacao planejada ao vocabulario canonico.

**Status:** done

- [x] `POST /transacoes` aceita `situacao: EFETIVADA` para receitas e despesas simples.
- [x] Uma Transacao efetivada registra `efetivadoEm` no instante da criacao e o expoe em formato ISO-8601 na resposta.
- [x] Uma Transacao planejada continua retornando `efetivadoEm` nulo.
- [x] Criacao efetivada aceita Data financeira passada ou igual a data atual no fuso do Usuario.
- [x] Criacao efetivada com Data financeira futura no fuso do Usuario e rejeitada com erro de dominio estavel.
- [x] A Data financeira continua obrigada a ser igual ou posterior a data do saldo inicial da Conta financeira.
- [x] Conta financeira e Categoria precisam estar ativas e pertencer ao Usuario na criacao, sem revelar recursos alheios.
- [x] O banco impede incoerencia entre Situacao e `efetivadoEm`.
- [x] Testes HTTP cobrem receitas e despesas planejadas e efetivadas, limites de data, fuso do Usuario, resposta e isolamento.
- [x] A documentacao do ciclo de criacao reflete as duas Situacoes e a diferenca entre Data financeira e instante da efetivacao.
- [x] `mvnw verify -B` passa contra PostgreSQL.

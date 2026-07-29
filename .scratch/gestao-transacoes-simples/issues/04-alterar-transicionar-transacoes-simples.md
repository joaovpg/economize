# 04 — Alterar e transicionar Transacoes simples

**What to build:** permitir que o Usuario substitua atomicamente o estado mutavel de uma receita ou despesa simples, corrigindo seus dados e transitando entre planejada e efetivada sem perder coerencia temporal, propriedade ou integridade financeira.

**Blocked by:** 02 — Criar Transacoes efetivadas; 03 — Excluir Transacoes simples.

**Status:** ready-for-agent

- [ ] `PUT /transacoes/{id}` exige e substitui o estado mutavel completo: Situacao, Conta financeira, Categoria, tipo, descricao, observacoes, valor e Data financeira.
- [ ] A alteracao pode alternar o tipo entre `RECEITA` e `DESPESA`, mas nunca aceita `TRANSFERENCIA`.
- [ ] A transicao de `PLANEJADA` para `EFETIVADA` registra um novo `efetivadoEm` e rejeita Data financeira futura no fuso do Usuario.
- [ ] Alterar uma Transacao que permanece `EFETIVADA` preserva o `efetivadoEm` original, inclusive quando sua Data financeira muda.
- [ ] A transicao de `EFETIVADA` para `PLANEJADA` limpa `efetivadoEm`, e uma efetivacao posterior registra outro instante.
- [ ] Manter a mesma Conta financeira e permitido quando ela foi inativada depois da associacao.
- [ ] Mover para outra Conta financeira exige que ela esteja ativa, pertenca ao Usuario, use a mesma moeda e tenha data do saldo inicial compativel.
- [ ] Uma Categoria inativa ja associada pode ser mantida ou removida; associar outra Categoria exige que ela esteja ativa e pertenca ao Usuario.
- [ ] Identificadores inexistentes ou alheios retornam `404`, enquanto uma Transacao vinculada do proprio Usuario retorna `422` com codigo `TRANSACAO_NAO_SIMPLES`.
- [ ] Qualquer falha preserva integralmente o estado anterior da Transacao.
- [ ] Testes HTTP cobrem todos os campos mutaveis, transicoes, instantes, associacoes ativas e inativas, propriedade, atomicidade e vinculos com outros modulos.
- [ ] Roadmap, glossario, modelo financeiro e apresentacao do estado implementado refletem o ciclo completo entregue e marcam o Marco 2 como concluido.
- [ ] `mvnw verify -B` passa contra PostgreSQL.

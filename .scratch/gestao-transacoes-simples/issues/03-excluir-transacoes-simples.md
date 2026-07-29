# 03 — Excluir Transacoes simples

**What to build:** permitir que o Usuario exclua definitiva e integralmente uma receita ou despesa simples, planejada ou efetivada, sem permitir que lados de Transferencias ou operacoes recorrentes sejam manipulados pelo fluxo errado.

**Blocked by:** 02 — Criar Transacoes efetivadas.

**Status:** ready-for-agent

- [ ] `DELETE /transacoes/{id}` exclui fisicamente uma Transacao simples planejada e retorna `204 No Content`.
- [ ] O mesmo endpoint exclui fisicamente uma Transacao simples efetivada e retorna `204 No Content`.
- [ ] Consultar internamente ou tentar excluir novamente o identificador removido confirma ausencia e produz `404` no segundo `DELETE`.
- [ ] Identificadores inexistentes ou pertencentes a outro Usuario retornam `404` sem revelar sua existencia.
- [ ] Transacoes vinculadas a Transferencia, Recorrencia ou Parcelamento nao sao excluidas e retornam `422` com codigo `TRANSACAO_NAO_SIMPLES` para o proprietario.
- [ ] A exclusao nao oferece lixeira, restauracao, cancelamento nem versionamento financeiro.
- [ ] Excluir a ultima Transacao de uma Conta financeira nao libera seus Dados iniciais.
- [ ] A operacao inteira e transacional e nao deixa vinculos orfaos ou estado parcial diante de falha.
- [ ] Testes HTTP cobrem exclusao planejada, efetivada, repetida, isolamento por Usuario, vinculos e permanencia do bloqueio dos Dados iniciais.
- [ ] A documentacao descreve a exclusao como fisica e definitiva.
- [ ] `mvnw verify -B` passa contra PostgreSQL.

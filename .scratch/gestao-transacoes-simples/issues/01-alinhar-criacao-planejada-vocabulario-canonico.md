# 01 — Alinhar a criacao planejada ao vocabulario canonico

**What to build:** permitir que o Usuario continue criando receitas e despesas planejadas, agora com Situacao explicita e Data financeira nomeadas de forma consistente no contrato, na aplicacao e no schema, removendo com seguranca o estado legado `CANCELADA`.

**Blocked by:** None — can start immediately.

**Status:** done

- [x] `POST /transacoes` exige `situacao: PLANEJADA`, usa `dataFinanceira` e devolve esses nomes no contrato, sem compatibilidade com `status` ou `dataVencimento`.
- [x] HTTP, Java e schema usam Situacao da transacao e Data financeira como vocabulario canonico, incluindo `STR_SITUACAO` e `DAT_FINANCEIRA` no banco.
- [x] O enum e a constraint de Transacoes aceitam somente `PLANEJADA` e `EFETIVADA`, sem alterar a migration inicial compartilhada.
- [x] A migration remove Transacoes simples canceladas e remove uma Transferencia cancelada com seus dois lados como um unico conjunto coerente antes de estreitar a constraint.
- [x] Funcoes, triggers, indices, constraints e mapeamentos afetados pelos renames continuam validos.
- [x] Criacao planejada preserva validacoes existentes de autenticacao, propriedade, Conta financeira ativa, Categoria ativa, tipo, valor, descricao e Data financeira compativel com o saldo inicial.
- [x] Testes HTTP protegem o contrato planejado e testes Flyway em schema temporario protegem renames, constraints e migracao do legado cancelado.
- [x] Um ADR registra a decisao irreversivel de excluir dados cancelados, suas alternativas e consequencias.
- [x] O glossario e o modelo financeiro deixam de descrever `status`, vencimento ou cancelamento como conceitos atuais de Transacoes simples.
- [x] `mvnw verify -B` passa contra PostgreSQL.

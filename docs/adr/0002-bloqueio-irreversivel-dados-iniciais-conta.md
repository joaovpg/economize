# ADR 0002: Bloqueio irreversivel dos Dados iniciais da conta

## Status

Aceita.

## Contexto

Moeda, saldo inicial e data do saldo inicial definem a base sobre a qual operacoes e saldos de uma Conta financeira sao interpretados. Transacoes podem ser excluidas fisicamente, e Recorrencias ou Parcelamentos podem projetar operacoes sem materializa-las como Transacoes.

Consultar somente as Transacoes existentes permitiria liberar esses dados depois de uma exclusao ou enquanto houvesse apenas ocorrencias virtuais. Isso tornaria possivel reinterpretar historico financeiro ou posicionar uma operacao antes da data inicial da conta.

## Decisao

A Conta financeira mantem um marcador interno que bloqueia irreversivelmente seus Dados iniciais no primeiro vinculo com uma operacao financeira, incluindo Transacoes, lados de Transferencias, Segmentos de recorrencia e Parcelamentos. O bloqueio permanece depois da exclusao das operacoes e nao e exposto no contrato HTTP.

A aplicacao valida a regra para produzir erros de dominio. Triggers no PostgreSQL ativam o marcador, impedem sua reversao e protegem alteracoes diretas no banco.

## Consequencias

- Nome e situacao continuam editaveis depois do bloqueio.
- Excluir todas as operacoes nao restaura a edicao dos Dados iniciais.
- Novos tipos de operacao que se vinculem a Conta financeira devem acionar o mesmo marcador.
- A migration inicializa o marcador para Transacoes e Segmentos que ja existiam.

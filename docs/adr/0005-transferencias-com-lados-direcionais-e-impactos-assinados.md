# ADR 0005: Transferencias com lados direcionais e impactos assinados

## Status

Aceita.

## Contexto

O schema inicial representava os dois lados de uma Transferencia com o tipo `TRANSFERENCIA`. Esse tipo identificava a origem da operacao, mas nao informava se o lado aumentava ou reduzia o saldo. O vinculo com `TB007_TRANSFERENCIA` ja identifica de forma estrutural a origem dos registros.

Categorias classificam receitas e despesas simples. Criar uma Categoria sintetica para Transferencias duplicaria sua origem e introduziria regras especiais na gestao de Categorias.

## Decisao

O lado da Conta financeira de origem e persistido como `DESPESA` e o lado da Conta financeira de destino como `RECEITA`. O vinculo com `TB007_TRANSFERENCIA`, e nao um terceiro tipo financeiro, identifica a origem dos dois registros. O tipo `TRANSFERENCIA` deixa de ser valido para Transacoes.

Os dois lados nao possuem Categoria. Na consulta unificada, ambos informam origem `TRANSFERENCIA`; o valor da saida e negativo e o da entrada e positivo. O tipo persistido nao faz parte desse contrato de leitura.

## Consequencias

- Categorias permanecem um conceito uniforme, sem registros reservados ou comportamentos especiais.
- O frontend calcula a evolucao do saldo somando os valores assinados da linha do tempo.
- Os dois lados aparecem na consulta unificada com o mesmo identificador de operacao e informam a Conta contraparte.
- Um filtro de Categoria exclui Transferencias, pois seus lados nao possuem Categoria.
- Escritas permanecem exclusivas do modulo de Transferencias; os IDs internos dos lados nao fazem parte do contrato HTTP.
- A leitura de Transferencias nao possui endpoint proprio.

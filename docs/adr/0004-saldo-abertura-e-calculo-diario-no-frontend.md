# ADR 0004: Saldo de abertura e calculo diario no frontend

## Status

Aceita.

## Contexto

O desenho anterior da consulta de Transacoes previa saldos atuais e projetados por Conta financeira e consolidados no fim do periodo. Essa resposta exigiria carregar movimentos anteriores ao periodo para distinguir Transacoes planejadas de efetivadas e repetiria no backend um calculo que a linha do tempo ja permite ao frontend executar para cada dia exibido.

Para o MVP, `PLANEJADA` e `EFETIVADA` sao controles operacionais. Ambas representam compromissos financeiros existentes e devem produzir o mesmo impacto nas somas. Transacoes excluidas fisicamente deixam de participar naturalmente.

## Decisao

A consulta exige um intervalo inclusivo de um a doze meses civis e devolve um unico Saldo de abertura consolidado no ultimo dia anterior ao primeiro mes. O Saldo de abertura parte dos saldos iniciais ja vigentes e agrega todas as Transacoes anteriores atendidas pelos filtros de Conta financeira e Categoria, sem distinguir Situacao.

O backend devolve todos os itens planejados e efetivados dentro do intervalo. Quando uma Conta financeira inicia dentro do intervalo com saldo diferente de zero, a resposta inclui um item `SALDO_INICIAL_CONTA` na data correspondente. O frontend aplica os itens ao Saldo de abertura para derivar os saldos diarios.

O historico persistido e agregado no banco sem carregar cada Transacao. Quando Recorrencias e Parcelamentos forem implementados, suas ocorrencias virtuais anteriores ao intervalo tambem participarao do Saldo de abertura, e as ocorrencias dentro do intervalo serao devolvidas como itens.

## Alternativas consideradas

- Manter saldo atual e saldo projetado: descartada porque a Situacao deixou de alterar o impacto financeiro e os dois valores perderam significados distintos.
- Manter os saldos finais por Conta financeira: descartada porque o frontend precisa da linha do tempo para exibir saldos diarios e pode deriva-los a partir de uma unica base.
- Persistir snapshots mensais: descartada no MVP por introduzir sincronizacao e invalidacao antes de existir evidencia de que a agregacao no banco e insuficiente.
- Exigir que o frontend combine a consulta com a listagem de Contas financeiras: descartada porque permitiria respostas inconsistentes entre chamadas e espalharia a composicao da linha do tempo.

## Consequencias

- O contrato HTTP nao oferece filtro por Situacao, modos de saldo, saldos por Conta financeira nem consolidado final.
- O frontend passa a ser responsavel por calcular os saldos diarios na ordem dos itens.
- O Saldo de abertura continua respeitando filtros de Conta financeira e Categoria; o filtro de Categoria nao remove a base formada pelos saldos iniciais.
- Itens de saldo inicial usam o identificador da Conta financeira, tipo derivado do sinal e valor absoluto; Situacao, instante de efetivacao e Categoria nao se aplicam.
- A integracao futura de ocorrencias virtuais deve preservar a mesma continuidade antes e dentro do intervalo.

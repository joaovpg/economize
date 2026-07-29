# ADR 0003: Remocao de Transacoes canceladas legadas

## Status

Aceita.

## Contexto

O ciclo de vida inicial permitia `CANCELADA` em Transacoes e Transferencias. O ciclo aprovado para o MVP usa somente `PLANEJADA` e `EFETIVADA`, e a aplicacao ainda nao oferecia comportamento publico para consultar ou restaurar registros cancelados.

Manter esses dados exigiria introduzir um estado sem operacoes correspondentes ou escolher implicitamente entre replanejar e efetivar registros cujo significado financeiro foi encerrado.

## Decisao

A migration que adota o vocabulario canonico exclui definitivamente as Transacoes canceladas legadas. Transferencias canceladas sao excluidas primeiro, como operacoes logicas completas, e seus dois lados cancelados sao removidos em seguida com as demais Transacoes canceladas.

A migration inicial compartilhada permanece inalterada. Depois da limpeza, a constraint de Situacao da transacao passa a aceitar somente `PLANEJADA` e `EFETIVADA`.

## Alternativas consideradas

- Converter registros cancelados em `PLANEJADA`: descartada porque recriaria obrigacoes financeiras que haviam sido canceladas.
- Converter registros cancelados em `EFETIVADA`: descartada porque produziria efeitos em saldos sem evidencia de realizacao.
- Preservar `CANCELADA` como estado legado somente para leitura: descartada porque manteria um terceiro estado no schema e ampliaria contratos futuros sem necessidade de produto.
- Arquivar os registros em tabelas auxiliares: descartada porque nao existe requisito de auditoria ou restauracao para esses dados no MVP.

## Consequencias

- A remocao e irreversivel e deve ser considerada antes de aplicar a migration em ambientes com dados legados.
- IDs, descricoes e demais dados de Transacoes canceladas deixam de existir.
- Transferencias canceladas nao deixam lados orfaos.
- Contas anteriormente vinculadas continuam com os Dados iniciais bloqueados pelo marcador irreversivel ja persistido.

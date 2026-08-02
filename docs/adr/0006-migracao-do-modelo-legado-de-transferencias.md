# ADR 0006: Migracao do modelo legado de Transferencias

## Status

Aceita.

## Contexto

Versoes anteriores do schema aceitavam Transacoes de tipo `TRANSFERENCIA` sem vinculo com uma Transferencia e Transferencias com Situacao `ESTORNADA` e relacionamento de estorno. Nao existe fluxo publico para esses estados, mas uma migration incremental precisa produzir o modelo canonico sem alterar a migration inicial compartilhada.

## Decisao

Lados vinculados sao convertidos de forma deterministica para `DESPESA` na origem e `RECEITA` no destino, e suas Categorias sao removidas. A migration aborta se encontrar Transacoes orfas de tipo `TRANSFERENCIA`, pois nao existe informacao que permita inferir sua direcao e dados financeiros nao devem ser excluidos silenciosamente. O bloqueio irreversivel dos Dados iniciais da Conta financeira permanece.

Transferencias `ESTORNADA` sao convertidas em `EFETIVADA` e o relacionamento de estorno e removido. Essa conversao preserva os movimentos e saldos existentes ao transformar os registros em Transferencias independentes.

Categorias existentes nao sao alteradas. O nome `Transferencias` permanece disponivel como qualquer outro nome escolhido pelo Usuario, sem significado reservado.

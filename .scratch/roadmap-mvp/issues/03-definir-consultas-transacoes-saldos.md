# Definir consultas de transacoes e calculo de saldos

Parent: [Reestruturar o roadmap do MVP por dependencias](../map.md)
Type: grilling
Status: resolved
Blocked by: 01, 02

## Question

Qual contrato de consulta por intervalo, ordenacao, paginacao, inclusao de canceladas e materializacao deve orientar o MVP, e como saldo atual e saldo projetado devem ser calculados por conta diante de transacoes planejadas, efetivadas e dos dois lados de transferencias?

## Answer

O marco deve oferecer consulta de Transacoes por um periodo opcional. Sem periodo informado, usa o mes civil atual. Inicio e fim sao inclusivos, devem pertencer ao mesmo mes civil e, quando fornecidos, devem aparecer juntos. A consulta nao usa paginacao e ordena todas as Transacoes por `dataVencimento` crescente, com `id` crescente como desempate deterministico.

`dataVencimento` e a data financeira canonica de Transacoes planejadas e efetivadas. `efetivadoEm` registra somente o instante auditavel da efetivacao. Ao efetivar, o usuario informa uma data civil igual ou anterior a data atual; essa data substitui o vencimento. Alterar a data de uma Transacao efetivada tambem altera o vencimento, preservando `efetivadoEm` conforme o ciclo de vida definido para Transacoes simples.

A consulta aceita filtros opcionais por uma ou mais Contas financeiras, uma ou mais Categorias e uma ou mais situacoes (`PLANEJADA` e `EFETIVADA`). Sem esses filtros, inclui ambas as situacoes e todas as contas e categorias no periodo. Contas inativas continuam consultaveis e podem ser filtradas explicitamente. Transacoes simples excluidas fisicamente nao aparecem; o tratamento de lados de Transferencias canceladas ou estornadas depende do ciclo de vida de Transferencias.

A listagem unifica Transacoes persistidas e ocorrencias recorrentes virtuais projetadas pelo motor RRULE: receitas, despesas e cada lado de Transferencia. Cada item identifica sua origem como simples, recorrencia ou transferencia e referencia a operacao ou grupo relacionado. Conforme detalhado em [Definir o contrato de repeticao e materializacao RRULE](05-definir-contrato-repeticao-rrule.md), consultar expande recorrencias em memoria, mas nao materializa nem grava as ocorrencias projetadas.

O saldo atual de uma Conta financeira e o saldo inicial somado a todas as receitas efetivadas e subtraido de todas as despesas efetivadas. Como a efetivacao alinha o vencimento a data civil em que o movimento ocorreu, uma efetivada nunca permanece posicionada em data financeira futura. O lado de origem de uma Transferencia subtrai e o lado de destino soma; em uma consolidacao que inclua ambas as contas, os lados se anulam.

O saldo projetado inclui Transacoes planejadas e efetivadas, persistidas ou projetadas virtualmente, e possui dois modos explicitos. `ACUMULADO`, o padrao, parte do saldo inicial e considera todas as Transacoes desde `dataSaldoInicial` ate o fim inclusivo do periodo. `SOMENTE_PERIODO` parte de zero, considera apenas as Transacoes entre inicio e fim inclusivos e exclui saldo inicial e movimentos anteriores. Planejadas vencidas entram na projecao. Os saldos consideram todo o conjunto que atende ao periodo e aos filtros, independentemente da ordenacao da listagem.

O MVP restringe Contas financeiras a `BRL`. A resposta retorna saldos por conta e um total consolidado em BRL. A moeda permanece explicita no modelo e no contrato para permitir evolucao futura sem autorizar soma entre moedas diferentes.

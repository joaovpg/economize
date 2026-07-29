# Definir o ciclo de vida das transferencias

Parent: [Reestruturar o roadmap do MVP por dependencias](../map.md)
Type: grilling
Status: resolved
Blocked by: 01, 02, 03

## Question

Quais fluxos de criacao, consulta, edicao, efetivacao, cancelamento e estorno compoem transferencias simples no MVP, quais invariantes devem ser atomicas entre a operacao e suas duas transacoes e quais lacunas do modelo persistido precisam ser tratadas antes da implementacao?

## Answer

O MVP deve permitir criar uma Transferencia como `PLANEJADA` ou `EFETIVADA`. A Transferencia e suas Transacoes de saida e entrada devem ser criadas, alteradas e excluidas na mesma transacao de banco. Seus dois lados permanecem vinculados e nao podem ser alterados isoladamente.

Contas de origem e destino pertencem ao Usuario autenticado, sao distintas e usam a mesma moeda. Valor, data financeira, descricao, observacoes e situacao sao iguais nos dois lados; somente conta e tipo diferem: a origem recebe uma despesa e o destino uma receita. As regras de propriedade e de data minima das duas Contas financeiras sao validadas em toda criacao ou edicao.

Transferencias `PLANEJADA` e `EFETIVADA` podem ter contas, valor, data, descricao e observacoes editados diretamente, sempre refletindo a mudanca atomicamente nas duas Transacoes. A situacao pode transitar nos dois sentidos. Efetivar atribui o mesmo `efetivadoEm` a operacao e aos dois lados; voltar para `PLANEJADA` limpa os tres instantes; uma nova efetivacao gera um novo instante comum.

A exclusao e fisica e remove atomicamente a Transferencia e suas duas Transacoes, independentemente da situacao. O MVP nao oferece cancelamento nem estorno e, portanto, preserva somente as situacoes `PLANEJADA` e `EFETIVADA`. O modelo persistido precisara remover os estados `CANCELADA` e `ESTORNADA` e o relacionamento de estorno, alem de garantir uma estrategia de cascata compativel com a exclusao atomica.

A consulta propria lista Transferencias de um unico mes civil, usa o mes atual por padrao, nao tem paginacao e permite detalhamento por ID. Os dois lados tambem aparecem na listagem mensal unificada de Transacoes, identificados como partes da mesma Transferencia.

Uma Conta financeira inativa impede novas operacoes, mas nao bloqueia consulta, edicao, efetivacao, replanejamento ou exclusao de Transacoes e Transferencias existentes. Esta decisao revisa a restricao definida anteriormente para operacoes planejadas em contas inativas.

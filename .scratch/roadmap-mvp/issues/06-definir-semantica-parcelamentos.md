# Definir a semantica dos parcelamentos

Parent: [Reestruturar o roadmap do MVP por dependencias](../map.md)
Type: grilling
Status: resolved
Blocked by: 05

## Question

Como o parcelamento mensal e o parcelamento configuravel do modo avancado devem representar o valor informado por parcela, o total derivado, parcela inicial, quantidade restante, numeracao, datas, edicao e cancelamento, reutilizando o motor RRULE sem confundir parcelamento com recorrencia? O valor total nao e dividido: por exemplo, valor por parcela multiplicado por 12 produz o total de um parcelamento em 12 vezes.

## Answer

Parcelamento e um plano financeiro finito cuja autoridade monetaria inicial e o valor por parcela. O contrato recebe o numero da primeira parcela e a quantidade total original; por exemplo, cadastrar 4 de 12 projeta somente 4/12 a 12/12, mas preserva como total contratado original o valor por parcela multiplicado por 12. Quantidade restante, ultima parcela e total original sao derivados, nao repetidos na entrada.

A numeracao e estavel durante toda a vida do plano. Alterar a quantidade encerra o parcelamento atual e cria outro, em vez de renumerar parcelas existentes ou mudar seu denominador. O dominio distingue o total contratado original do total atual, calculado pela soma das parcelas existentes depois de edicoes e cancelamentos.

O modo mensal produz uma parcela por mes. O modo configuravel aceita frequencias diaria, semanal, mensal e anual com intervalo maior ou igual a um, sempre com quantidade finita e uma unica cadencia ancorada no `DTSTART`; nao aceita listas de varios dias semanais ou mensais. Em frequencias mensal e anual, uma data inexistente e ajustada para o ultimo dia do mes, de modo que nenhuma parcela seja omitida. Essa e uma semantica propria de Parcelamento executada sobre o mesmo motor de repeticao, nao a semantica literal das recorrencias avancadas.

Edicao e cancelamento aceitam `ONLY_THIS` e `THIS_AND_FUTURE`. Uma edicao individual cria uma excecao sem alterar a numeracao; uma edicao futura cria novo Segmento no mesmo Grupo e preserva as parcelas efetivadas posteriores como excecoes. Cancelar uma parcela a suprime sem renumerar as demais; cancelar desta em diante encerra o Segmento. Parcelas efetivadas admitem correcao ou exclusao fisica conforme o ciclo de vida das Transacoes simples. Alterar a quantidade, contudo, encerra o plano atual e inicia outro Grupo de parcelamento.

# Ordenar os marcos posteriores ao MVP

Parent: [Reestruturar o roadmap do MVP por dependencias](../map.md)
Type: grilling
Status: resolved

## Question

Em que ordem e com quais dependencias de alto nivel o roadmap deve posicionar orcamentos, cartao de credito, relatorios, analises financeiras assistidas por IA e recursos avancados, sem transforma-los em especificacoes prematuras nem deixa-los ambiguos para agentes futuros?

## Answer

O roadmap apresentara uma ordem principal de prioridade para orientar a evolucao depois do MVP, sem transforma-la em uma sequencia estrita: Orcamentos, Cartao de credito, Relatorios e Analises financeiras assistidas por IA. Dependencias explicitas permitirao antecipar uma capacidade quando seus gates estiverem satisfeitos.

Todos os marcos posteriores partem do MVP concluido. Alem desse gate comum:

- Orcamentos depende de Categorias, consultas de Transacoes e calculo de saldos.
- Cartao de credito depende de Transacoes e Parcelamentos.
- Relatorios depende apenas do MVP e pode evoluir antes dos dois marcos anteriores; deve incorporar dados de Orcamentos e Cartao de credito quando essas capacidades existirem.
- Analises financeiras assistidas por IA depende de Relatorios e permanece consultiva, sem criar, editar ou efetivar operacoes financeiras.

O atual agrupamento Recursos avancados sera desmembrado em capacidades futuras sem prioridade fechada. Conversao entre moedas dependera de uma politica cambial e do calculo de saldos; divisao por categorias dependera de Transacoes e Categorias; compartilhamento de dados dependera de regras proprias de autorizacao e isolamento. Cada capacidade ganhara um marco e uma especificacao somente quando for priorizada.

Cada item posterior ao MVP registrara apenas objetivo, dependencias e dois ou tres resultados de alto nivel. Contratos, cenarios e criterios detalhados de conclusao serao definidos em esforcos de especificacao futuros.

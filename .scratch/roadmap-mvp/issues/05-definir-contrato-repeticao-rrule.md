# Definir o contrato de repeticao e materializacao RRULE

Parent: [Reestruturar o roadmap do MVP por dependencias](../map.md)
Type: grilling
Status: resolved
Blocked by: 02, 03

## Question

Como os modos sem repeticao e avancado devem ser expressos no contrato de receitas e despesas, qual subconjunto de RRULE o MVP aceitara e como validacao, canonicalizacao, expansao, materializacao idempotente e edicoes `ONLY_THIS` e `THIS_AND_FUTURE` devem se comportar?

## Answer

A criacao de receitas e despesas usa um contrato discriminado. O modo sem repeticao cria uma Transacao simples conforme o ciclo de vida ja definido. O modo avancado recebe campos estruturados de negocio e nao aceita RRULE textual do cliente; o backend valida esses campos e gera a RRULE canonica persistida no Segmento de recorrencia. O parcelamento reutilizara o mesmo motor de repeticao, mas permanecera um conceito distinto e sera detalhado em ticket proprio.

O modo avancado aceita as frequencias diaria, semanal, mensal e anual, todas com intervalo maior ou igual a um. A frequencia semanal exige um ou mais dias da semana e adota segunda-feira como inicio fixo da semana. A mensal exige um ou mais dias entre 1 e 31. A anual deriva mes e dia do `DTSTART`. O `DTSTART` deve corresponder ao padrao escolhido e e sempre a primeira ocorrencia. Datas inexistentes, como dia 31 em fevereiro ou 29 de fevereiro em ano nao bissexto, omitem a ocorrencia naquele periodo, sem deslocamento implicito.

O termino e uma uniao discriminada com exatamente uma opcao: quantidade de ocorrencias, contando a primeira; data limite civil e inclusiva; ou sem termino. As validacoes de dominio exigem intervalo e quantidade maiores ou iguais a um, data limite igual ou posterior ao `DTSTART`, listas semanais e mensais nao vazias, valores validos e sem repeticao. Nao ha maximo temporal ou quantitativo arbitrario no dominio; protecoes operacionais contra abuso nao alteram a semantica financeira.

A RRULE e armazenada sem o prefixo `RRULE:`, em formato deterministico. Seus componentes seguem ordem fixa, valores padrao como `INTERVAL=1` sao omitidos, listas sao ordenadas, `UNTIL` usa data civil inclusiva no formato basico `yyyyMMdd` e a semana iniciada na segunda-feira fica explicita como `WKST=MO`. A mesma entrada estruturada sempre produz a mesma RRULE canonica.

Recorrencias nao materializam previamente todas as Transacoes. Grupo e Segmento preservam a regra, enquanto ocorrencias planejadas futuras permanecem virtuais e sao expandidas em memoria pelo motor RRULE durante consultas e calculos de saldo. Nao existe horizonte funcional: consultar janeiro de 2036 deve projetar o mes e calcular o saldo acumulado desde a data de saldo inicial, inclusive para uma repeticao sem termino. A consulta nao grava as ocorrencias que projetou.

Uma ocorrencia passa a ser persistida quando e efetivada, alterada individualmente ou precisa registrar uma excecao. Sua identidade logica e o par Segmento de origem e data original da ocorrencia; essa origem permanece imutavel mesmo quando a excecao muda de vencimento ou deixa de ser governada pelo Segmento. A persistencia deve proteger essa identidade com unicidade para tornar efetivacao e edicao idempotentes. Ao criar uma repeticao ja `EFETIVADA`, somente a primeira ocorrencia e persistida e deve satisfazer as regras de data de uma Transacao efetivada; as seguintes permanecem planejadas e virtuais. Uma repeticao criada `PLANEJADA` persiste inicialmente apenas Grupo e Segmento.

`ONLY_THIS` persiste uma excecao completa no mesmo Grupo, preserva a referencia tecnica ao Segmento e a data original para suprimir a projecao correspondente e permite alterar os mesmos campos financeiros admitidos para uma Transacao simples sem mudar a RRULE. Excluir somente uma ocorrencia virtual registra uma excecao de exclusao sem criar uma Transacao financeira.

`THIS_AND_FUTURE` encerra o Segmento anterior antes da ocorrencia escolhida e cria no mesmo Grupo um novo Segmento cujo `DTSTART` e essa ocorrencia. O novo trecho pode alterar campos financeiros e a regra de repeticao. Ocorrencias efetivadas a partir do corte sao preservadas integralmente como excecoes e suprimem qualquer projecao conflitante; excecoes apenas planejadas substituidas pelo novo trecho sao excluidas. Excluir desta ocorrencia em diante apenas encerra o Segmento anterior, sem criar um novo.

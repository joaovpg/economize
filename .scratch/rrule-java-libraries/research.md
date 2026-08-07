# Bibliotecas Java para RRULE e recorrencias iCalendar

**Data da pesquisa:** 2026-08-05  
**Escopo:** bibliotecas Java/JVM para criar, serializar, validar e expandir regras `RRULE`/recorrencias iCalendar em um backend Quarkus com Java 25.

## Conclusao

Escolho **iCal4j 4.3.0** como base do motor, encapsulado por um adapter proprio do dominio.

Motivos principais:

- e a alternativa com release publicada mais atual entre as tres avaliadas;
- a linha 4.x declara Java 11 como requisito minimo e usa a API moderna `java.time`, incluindo `LocalDate`, `LocalDateTime`, `Instant` e `ZonedDateTime`;
- oferece tanto construcao/serializacao de RRULE quanto expansao por intervalo (`Recur.getDates(...)`), alem de suporte ao modelo de conjunto iCalendar;
- seu contrato e mais proximo das necessidades do projeto: datas financeiras sem horario podem ser representadas diretamente como `LocalDate`, enquanto fuso e horario ficam disponiveis sem impor seu uso;
- as dependencias principais sao convencionais para um servico Quarkus e podem ser mantidas atras de uma fronteira interna, evitando que entidades e casos de uso dependam da API da biblioteca.

Essa escolha nao elimina regras proprias do dominio. Parcelamento nao deve ser implementado apenas delegando a RRULE: a RFC manda ignorar datas invalidas, enquanto o produto exige ajustar parcelas como `31/01 -> 28/02 -> 31/03`. O adapter deve separar a expansao de recorrencia comum da politica de ajuste de datas de parcelamento.

## O que a RFC exige

`RRULE` e um valor iCalendar definido na [RFC 5545, secao 3.3.10](https://www.rfc-editor.org/rfc/rfc5545#section-3.3.10): `FREQ` e obrigatorio; `COUNT` e `UNTIL` sao opcionais, mas nao podem coexistir. `DTSTART` representa a primeira instancia, e `COUNT` conta essa instancia.

Para o nosso contrato financeiro:

- `DTSTART` deve ser persistido separadamente da RRULE, como a RFC modela `DTSTART` e `RRULE` como propriedades distintas;
- datas sem horario sao suportadas pelo tipo `DATE`, descrito na [secao 3.3.4](https://www.rfc-editor.org/rfc/rfc5545#section-3.3.4);
- uma data local sem `TZID` e um horario flutuante, enquanto uma data/hora fixa deve usar UTC ou referencia de fuso, conforme a [secao 3.2.19](https://www.rfc-editor.org/rfc/rfc5545#section-3.2.19) e a [secao 3.3.5](https://www.rfc-editor.org/rfc/rfc5545#section-3.3.5);
- `UNTIL` e inclusivo: se uma ocorrencia coincidir com o limite, ela e a ultima ocorrencia valida;
- datas invalidas, como 30 de fevereiro, devem ser ignoradas e nao contam para `COUNT`, conforme a [secao 3.3.10](https://www.rfc-editor.org/rfc/rfc5545#section-3.3.10) e o exemplo normativo da [secao 3.8.5.3](https://www.rfc-editor.org/rfc/rfc5545#section-3.8.5.3).

O ultimo ponto e deliberadamente diferente da politica de Parcelamento do produto. Para parcelamentos, a aplicacao precisa calcular a data ancorada com ajuste de fim de mes antes de expor a ocorrencia; a RRULE continuara sendo a referencia da cadencia, nao a unica fonte para reconstruir cada parcela.

## Comparacao

| Biblioteca | Versao observada | RFC/RRULE | Expansao por intervalo | DATE sem horario / fuso | Java 25 e Quarkus |
|---|---:|---|---|---|---|
| **iCal4j** | 4.3.0 | Parser, modelo iCalendar, construcao e serializacao; documenta RFC 5545 e extensoes relacionadas | `Recur.getDates(periodStart, periodEnd)` e APIs de conjunto | API 4.x baseada em `java.time`, com `LocalDate`, `LocalDateTime`, `Instant` e `ZonedDateTime` | Melhor evidencia pratica: source/target Java 11; dependencias JVM comuns; sem CDI proprio, deve ser encapsulada |
| **dmfs/lib-recur** | 0.17.1 | Parser RFC 5545/RFC 2445, modos strict/lax, builder de partes `BY*`, `RDATE`/`EXDATE`/`RecurrenceSet` | Iterador `RecurrenceRule.iterator(...)`; `OfRule`, `First` e `While` permitem limitar series infinitas | Usa `rfc5545-datetime`; suporta fuso no valor inicial, mas o adapter precisa controlar explicitamente o tipo da data e o timezone | Deve ser compatível como biblioteca JVM, mas nao ha declaracao de Java 25 nem matriz atual; API e dependencias sao menos alinhadas a `java.time` |
| **biweekly** | 0.6.8 | Biblioteca iCalendar para ler/escrever; README mostra `RRULE` e `Recurrence.Builder` e declara conformidade com especificacoes iCalendar | A documentacao oficial consultada nao apresenta um expansor/iterador de ocorrencias comparavel; exigiria componente adicional ou implementacao propria | Declara suporte a fuso, mas a API e antiga e o POM compila com Java 1.6 | Deve carregar em JVM moderna por usar bytecode antigo, mas e uma base antiga para um motor novo; dependencias e plugins tambem sao antigos |

### 1. iCal4j

**Pontos fortes**

- O repositorio oficial descreve a biblioteca como parser e modelo para ler/escrever iCalendar.
- A linha 4.x documenta explicitamente Java 11 ou posterior como requisito.
- A API 4.x usa tipos da plataforma: `new DtStart<>(LocalDate.now())`, `LocalDateTime`, `Instant` e `ZonedDateTime`.
- A classe `Recur` implementa a gramatica da RFC 5545, valida `FREQ`, `COUNT`/`UNTIL`, aceita `RSCALE`/`SKIP` e possui `getDates(...)` com inicio e fim de periodo.
- A versao publicada observada no Maven Central foi 4.3.0, com `slf4j-api`, `commons-codec`, `commons-lang3` e `threeten-extra` como dependencias principais; cache de timezone, parser de expressoes e DSL Groovy sao opcionais.

**Limitacoes e cuidados**

- A biblioteca e um modelo iCalendar amplo, portanto tem uma superficie maior que o nosso dominio financeiro.
- `Recur` nao deve ser exposta diretamente no contrato HTTP. O adapter deve produzir nossa RRULE canonica e converter resultados para `LocalDate`.
- A biblioteca segue a semantica RFC para datas invalidas. O ajuste de fim de mes de Parcelamento precisa ser uma politica do nosso motor, coberta por testes proprios.
- Nao ha evidencia de suporte especifico a Quarkus ou de certificacao para Java 25; a avaliacao de Java 25 e por compatibilidade de bytecode/API, nao por declaracao do fornecedor.

**Fontes primarias:** [repositorio oficial](https://github.com/ical4j/ical4j), [README e requisitos](https://github.com/ical4j/ical4j#system-requirements), [source de `Recur`](https://github.com/ical4j/ical4j/blob/develop/src/main/java/net/fortuna/ical4j/model/Recur.java), [artefato no Maven Central](https://central.sonatype.com/artifact/org.mnode.ical4j/ical4j).

### 2. dmfs/lib-recur

**Pontos fortes**

- E a biblioteca mais focada especificamente em processamento de recorrencia.
- O README documenta parser RFC 5545/RFC 2445, construcao de regras, iteracao e combinacao de `RRULE`, `RDATE`, `EXDATE` e `EXRULE` por `RecurrenceSet`.
- Possui modos `RFC5545_STRICT` e `RFC5545_LAX`, uteis para validar o limite que o cliente pode enviar.
- O padrao `OfRule` + `First`/`While` se encaixa bem em consultas limitadas, como uma janela de 12 meses ou um saldo de abertura ate uma data de corte.
- O README documenta suporte a quatro escalas de calendario via RSCALE.

**Limitacoes e cuidados**

- O proprio README diz que a interface das classes ainda nao esta finalizada e sujeita a mudancas, inclusive a `RecurrenceRule`.
- O modelo de datas e baseado em `rfc5545-datetime`/`DateTime`, nao diretamente nos tipos `java.time` usados pelo projeto.
- O proprio issue tracker oficial alerta que timezone nao pertence a RRULE; a zona usada na iteracao vem do valor inicial passado ao iterator. Um adapter teria que garantir que nunca se expanda uma data financeira em UTC por acidente quando a regra for local.
- A versao publicada observada foi 0.17.1, com idade indicada pelo Maven Central como aproximadamente dois anos na data da consulta; o projeto tambem lista tarefas pendentes como mais testes de edge cases, validator e um builder imutavel.

**Fontes primarias:** [repositorio oficial e README](https://github.com/dmfs/lib-recur), [classe `RecurrenceRule`](https://github.com/dmfs/lib-recur/blob/main/src/main/java/org/dmfs/rfc5545/recur/RecurrenceRule.java), [issue oficial sobre timezone](https://github.com/dmfs/lib-recur/issues/26), [artefato no Maven Central](https://central.sonatype.com/artifact/org.dmfs/lib-recur), [dependencia oficial `rfc5545-datetime`](https://github.com/dmfs/rfc5545-datetime).

### 3. biweekly

**Por que entra como terceira alternativa**

Biweekly e uma alternativa solida para **ler e escrever iCalendar**: o repositorio oficial declara API documentada, conformidade com as especificacoes iCalendar/vCalendar, suporte a fuso, testes e baixo requisito de Java. O README mostra tanto a leitura de um `VEVENT` com `RRULE` quanto a criacao de uma regra com `Recurrence.Builder`.

**Por que nao foi escolhida para o motor**

Na documentacao oficial consultada, biweekly aparece como parser/writer e modelo de propriedades, mas nao como um processador com iterator ou consulta por intervalo. Portanto, para o requisito central de projetar ocorrencias em `/api/transacoes`, nao ha evidencia suficiente de que ela resolva a expansao sozinha.

Outros sinais pesam contra inicia-la agora:

- a versao publicada observada continua sendo 0.6.8;
- o POM declara compilacao com Java 1.6, o que favorece compatibilidade de carregamento, mas indica uma base API antiga, nao suporte especifico a Java 25;
- sua integracao usa mais o modelo legado de `Date`/timezone do que o `java.time` que queremos manter no dominio.

**Fontes primarias:** [repositorio oficial](https://github.com/mangstadt/biweekly), [README oficial](https://github.com/mangstadt/biweekly#features), [POM oficial](https://github.com/mangstadt/biweekly/blob/master/pom.xml), [artefato no Maven Central](https://central.sonatype.com/artifact/net.sf.biweekly/biweekly).

## Compatibilidade pratica com Java 25

### Evidencia disponivel

- iCal4j 4.x compila com `sourceCompatibility`/`targetCompatibility` Java 11 no build oficial. Por usar bytecode de nivel inferior ao Java 25 e APIs publicas de Java 11, a expectativa pratica e boa em uma aplicacao JVM Java 25.
- lib-recur e biweekly tem artefatos JVM antigos e nao declaram requisito de Java 25. Isso normalmente favorece carregamento em JVM nova, mas nao prova compatibilidade comportamental nem compatibilidade de build com toolchains atuais.
- Nenhuma das tres fontes oficiais consultadas declara uma matriz de testes especifica para Java 25.

### Verificacao local

Nao foi possivel executar um teste real em Java 25 nesta sessao: o `java -version` disponivel foi Java 8 (`1.8.0_501`), nao foi encontrado um JDK 25 instalado nos caminhos usuais e o Maven Wrapper nao iniciou nesse ambiente. Portanto, a compatibilidade com Java 25 registrada aqui e uma conclusao baseada no nivel de compilacao, na API utilizada e nas dependencias publicadas; antes de adicionar a dependencia ao projeto, devemos validar com JDK 25 em uma branch de implementacao.

## Impacto para Quarkus

Para o backend atual, as tres podem ser usadas como bibliotecas normais, sem CDI proprio. A integracao recomendada e:

1. criar um adapter em `recorrencia` que receba `LocalDate`, RRULE e janela de consulta;
2. devolver um tipo interno de ocorrencia, sem vazar classes da biblioteca para Resources, DTOs ou entidades;
3. impor limites de expansao para regras sem `COUNT`/`UNTIL`;
4. validar e normalizar a RRULE no caso de uso;
5. manter Parcelamento fora da semantica pura de `RRULE` quando houver ajuste de fim de mes.

Com iCal4j, devemos observar especialmente o timezone registry/cache e as dependencias transitivas, mas o artefato principal publicado e pequeno o suficiente para uma integracao JVM convencional. Nao foi feita nesta pesquisa uma verificacao de compilacao nativa GraalVM/Quarkus; se o projeto futuramente exigir native image, isso deve virar um teste separado.

## Decisao proposta

Adotar **iCal4j 4.3.0** atras de um adapter interno, sem alterar ainda o `pom.xml` nesta etapa de pesquisa.

O adapter deve oferecer, no minimo:

- parse e serializacao canonica de RRULE;
- expansao limitada por `LocalDate` inicial/final;
- preservacao explicita de `DTSTART` como primeira ocorrencia;
- separacao entre recorrencia comum, que ignora datas invalidas conforme RFC, e parcelamento, que aplica ajuste de fim de mes;
- conversao de `COUNT`/`UNTIL` para as regras de negocio ja decididas;
- testes de contrato com casos como `31/01`, fevereiro nao bissexto, `COUNT`, `UNTIL` inclusivo, `ONLY_THIS`, supressao e regras infinitas.

## Fontes consultadas

- [RFC 5545 no RFC Editor](https://www.rfc-editor.org/rfc/rfc5545)
- [iCal4j no GitHub](https://github.com/ical4j/ical4j)
- [iCal4j 4.3.0 no Maven Central](https://central.sonatype.com/artifact/org.mnode.ical4j/ical4j)
- [dmfs/lib-recur no GitHub](https://github.com/dmfs/lib-recur)
- [lib-recur 0.17.1 no Maven Central](https://central.sonatype.com/artifact/org.dmfs/lib-recur)
- [dmfs/rfc5545-datetime no GitHub](https://github.com/dmfs/rfc5545-datetime)
- [biweekly no GitHub](https://github.com/mangstadt/biweekly)
- [biweekly 0.6.8 no Maven Central](https://central.sonatype.com/artifact/net.sf.biweekly/biweekly)

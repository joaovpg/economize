# iCal4j atras de port de recorrencia

**Status:** aceita

## Contexto

O projeto precisa expandir RRULEs para projetar Ocorrencias recorrentes, mas os casos de uso e o contrato financeiro devem permanecer independentes de uma biblioteca iCalendar. A semantica RFC de datas invalidas tambem nao cobre a regra de Parcelamento que ajusta uma parcela mensal ou anual para o ultimo dia do mes.

## Decisao

Adotar iCal4j 4.3.0 somente no adapter `Ical4jMotorRecorrencia`, implementando o port de dominio `MotorRecorrencia`. O adapter recebe `RegraRecorrencia`, usa `LocalDate` e devolve apenas `OcorrenciaRecorrencia`, sem expor tipos da biblioteca.

`ExpansorRecorrencia` permanece como fachada do dominio. Para a politica `PADRAO`, delega a expansao ao port. Para `AJUSTAR_ULTIMO_DIA_MES`, aplica apenas a politica especifica de Parcelamento para recorrencias mensais e anuais ancoradas no dia de inicio; as demais regras continuam no motor RFC.

## Consequencias

- A dependencia externa fica isolada em um unico adapter.
- A numeracao das ocorrencias continua sendo calculada pelo contrato interno, inclusive em janelas posteriores.
- A politica de ajuste de fim de mes permanece explicita e coberta por testes do dominio.
- Uma futura troca de biblioteca exige alterar o adapter, preservando os casos de uso e os tipos financeiros internos.

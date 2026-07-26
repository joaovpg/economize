# Modelo financeiro

O MVP registra e cataloga movimentacoes financeiras pessoais. Cada registro financeiro pertence a um unico usuario.

## Entidades

| Tabela | Entidade | Responsabilidade |
| --- | --- | --- |
| `TB001_USUARIO` | Usuario | Identidade, credencial e isolamento dos dados |
| `TB002_CONTA_FINANCEIRA` | Conta financeira | Nome, moeda, saldo inicial e disponibilidade operacional |
| `TB003_CATEGORIA` | Categoria | Classificacao hierarquica opcional de receitas e despesas |
| `TB004_GRUPO_RECORRENCIA` | Grupo de recorrencia | Rastreabilidade da origem comum de segmentos e excecoes |
| `TB005_SEGMENTO_RECORRENCIA` | Segmento de recorrencia | Trecho independente governado por uma RRULE |
| `TB006_TRANSACAO` | Transacao | Receita, despesa, ocorrencia recorrente ou lado de transferencia |
| `TB007_TRANSFERENCIA` | Transferencia | Operacao logica que vincula uma saida e uma entrada |

## Convencoes fisicas

Tabelas usam `TB<numero>_<NOME>`. Colunas usam os prefixos `ID_`, `STR_`, `DEC_`, `INT_`, `BOL_`, `DAT_`, `DHR_` e `VER_`. Indices usam `IX<tabela>_<sequencia>_<finalidade>`, e constraints usam `PK`, `FK`, `UK` ou `CK` seguidos do numero da tabela.

Os numeros de tabela sao permanentes e nao devem ser reutilizados. Identificadores SQL e Java permanecem em portugues sem acentos. Termos da RFC 5545, como RRULE e DTSTART, mantem a nomenclatura oficial.

## Saldos e transacoes

O saldo atual nao e armazenado. Ele e calculado a partir do saldo inicial da conta e das transacoes efetivadas desde `DAT_SALDO_INICIAL`. Valores de transacoes sao sempre positivos; o tipo determina se o valor entra ou sai da conta. Transacoes canceladas nao participam do saldo e ficam fora das consultas por padrao.

Contas e categorias podem ser ativadas e inativadas repetidamente sem perder historico. Categorias nao possuem tipo e podem classificar receitas e despesas. Sua hierarquia admite profundidade arbitraria; a deteccao de ciclos indiretos e a verificacao de que pai e filha pertencem ao mesmo usuario devem ocorrer no futuro servico de categorias.

## Recorrencia

Um grupo de recorrencia preserva somente a origem comum. Cada segmento possui dados financeiros proprios, DTSTART e uma RRULE canonica sem o prefixo `RRULE:`. O MVP planejado aceita `DAILY`, `WEEKLY`, `MONTHLY` e `YEARLY`, alem de `INTERVAL`, `COUNT`, `UNTIL`, `BYDAY` e `BYMONTHDAY`.

Editar somente uma ocorrencia a transforma em excecao: ela permanece no grupo, mas deixa de pertencer ao segmento. Editar a partir de uma ocorrencia encerra o segmento anterior e cria outro, com numeracao independente. A implementacao da expansao, validacao completa da RFC e edicao de segmentos pertence ao marco do motor de recorrencia.

## Transferencias

Uma transferencia possui dois lancamentos: saida na conta de origem e entrada na conta de destino. Data, valor e descricao pertencem a operacao logica e devem ser alterados atomicamente por um futuro servico. Os lancamentos vinculados nao podem ser editados isoladamente.

No MVP, as contas devem pertencer ao mesmo usuario, ser diferentes, estar ativas e usar a mesma moeda. Transferencias planejadas podem ser canceladas. Uma transferencia efetivada e revertida por outra transferencia inversa, preservando o historico.

## Limites desta entrega

Esta entrega fornece migrations, entidades, repositorios, constraints estruturais, cadastro de usuario, autenticacao JWT e criacao de receitas e despesas planejadas. Gestao completa de usuarios, contas e categorias, demais operacoes de transacao, calculo de saldo, deteccao de ciclos, operacoes atomicas de transferencia e expansao ou edicao de recorrencias permanecem para os proximos marcos.

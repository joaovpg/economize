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

O saldo atual nao e armazenado. Ele e calculado a partir do saldo inicial da conta e das transacoes efetivadas desde `DAT_SALDO_INICIAL`. Valores de transacoes sao sempre positivos; o tipo determina se o valor entra ou sai da conta. A Situacao de uma Transacao e `PLANEJADA` ou `EFETIVADA`, e sua Data financeira e persistida em `DAT_FINANCEIRA`. Na criacao, uma Transacao planejada nao possui instante de efetivacao. Uma Transacao efetivada exige Data financeira igual ou anterior a data atual no fuso do Usuario e registra separadamente em `DHR_EFETIVACAO` o instante em que foi criada como efetivada.

Contas e categorias podem ser ativadas e inativadas repetidamente sem perder historico. Categorias nao possuem tipo e podem classificar receitas e despesas. Sua hierarquia admite profundidade arbitraria; os casos de uso de categorias detectam ciclos indiretos e verificam que pai e filha pertencem ao mesmo usuario.

## Contas financeiras

Uma Conta financeira pertence a um Usuario e possui nome, moeda, saldo inicial, data do saldo inicial e situacao. O nome e armazenado sem espacos nas extremidades e e unico por Usuario sem diferenciar caixa, inclusive entre contas inativas. O MVP aceita somente `BRL` e representa o saldo inicial em `NUMERIC(19,4)`, permitindo valores positivos, negativos ou zero.

Moeda, saldo inicial e data do saldo inicial formam os Dados iniciais da conta. Eles ficam irreversivelmente bloqueados no primeiro vinculo com uma operacao financeira persistida ou com uma fonte de operacoes virtuais. Transacoes e lados de Transferencias acionam o bloqueio por meio de `TB006_TRANSACAO`; Segmentos de recorrencia e futuros Parcelamentos o acionam pela estrutura que projeta suas ocorrencias. Excluir posteriormente essas operacoes nao libera os Dados iniciais.

A data de uma Transacao ou o inicio de um Segmento nao pode anteceder a data do saldo inicial. Novas associacoes exigem Conta financeira ativa; a inativacao nao altera nem impede a manutencao das operacoes que ja estavam associadas.

Categorias sao cadastradas ativas. Nome e unico sem diferenciar caixa entre categorias irmas, inclusive entre categorias raiz e independentemente da situacao. Uma categoria so pode ser cadastrada ou movida para um pai ativo. A ativacao exige todos os ancestrais ativos, enquanto a inativacao exige que nao existam descendentes ativos. Edicoes podem alterar dados, posicao e situacao atomicamente e nunca podem formar ciclos.

## Recorrencia

Um grupo de recorrencia preserva somente a origem comum. Cada segmento possui dados financeiros proprios, DTSTART e uma RRULE canonica sem o prefixo `RRULE:`. O MVP planejado aceita `DAILY`, `WEEKLY`, `MONTHLY` e `YEARLY`, alem de `INTERVAL`, `COUNT`, `UNTIL`, `BYDAY` e `BYMONTHDAY`.

Editar somente uma ocorrencia a transforma em excecao: ela permanece no grupo, mas deixa de pertencer ao segmento. Editar a partir de uma ocorrencia encerra o segmento anterior e cria outro, com numeracao independente. A implementacao da expansao, validacao completa da RFC e edicao de segmentos pertence ao marco do motor de recorrencia.

## Transferencias

Uma transferencia possui dois lancamentos: saida na conta de origem e entrada na conta de destino. Data, valor e descricao pertencem a operacao logica e devem ser alterados atomicamente por um futuro servico. Os lancamentos vinculados nao podem ser editados isoladamente.

No MVP, as contas devem pertencer ao mesmo usuario, ser diferentes, estar ativas e usar a mesma moeda. O ciclo de vida e as operacoes atomicas de Transferencias serao definidos no marco correspondente. O modelo atual nao oferece cancelamento ou estorno de Transacoes simples. Receitas e despesas simples, planejadas ou efetivadas, podem ser excluidas fisica e definitivamente; a exclusao nao libera os Dados iniciais da Conta financeira.

## Limites desta entrega

Esta entrega fornece migrations, entidades, repositorios, constraints estruturais, cadastro de usuario, autenticacao JWT, gestao de categorias, gestao de Contas financeiras e criacao e exclusao definitiva de receitas e despesas planejadas ou efetivadas com Situacao explicita e Data financeira canonica. Gestao completa de usuarios, alteracao de transacoes, calculo de saldo, operacoes atomicas de transferencia e expansao ou edicao de recorrencias permanecem para os proximos marcos.

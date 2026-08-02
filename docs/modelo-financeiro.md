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

O saldo financeiro nao e armazenado. Ele e derivado do saldo inicial da conta e de todas as Transacoes desde `DAT_SALDO_INICIAL`. Valores persistidos de Transacoes sao sempre positivos; o tipo determina se cada registro entra ou sai da conta. A Situacao de uma Transacao e `PLANEJADA` ou `EFETIVADA` e serve somente ao controle operacional: ambas produzem o mesmo impacto nos saldos derivados. Uma Transacao planejada nao possui instante de efetivacao. Uma Transacao efetivada exige Data financeira igual ou anterior a data atual no fuso do Usuario e registra separadamente em `DHR_EFETIVACAO` o instante em que foi efetivada.

A consulta de Transacoes exige os meses inicial e final no formato `yyyy-MM`. O intervalo e inclusivo, admite de um a doze meses civis e pode filtrar por Contas financeiras e Categorias exatas. Nao existe filtro por Situacao: Transacoes planejadas e efetivadas sempre aparecem e participam das somas. Identificadores de filtros inexistentes ou alheios sao tratados como recurso nao encontrado.

A resposta retorna a linha do tempo e o Saldo de abertura consolidado em `BRL`. Cada item informa sua origem e um valor de impacto assinado, positivo para entradas e negativo para saidas; o contrato nao expoe o tipo persistido. O Saldo de abertura usa como corte o ultimo dia anterior ao primeiro mes consultado e soma os saldos iniciais ja vigentes com todos os movimentos anteriores atendidos pelos filtros. O filtro de Categoria seleciona os movimentos sem remover a base formada pelos saldos iniciais das Contas financeiras selecionadas e exclui Transferencias, que nao possuem Categoria.

Quando a data do saldo inicial de uma Conta financeira cai dentro do intervalo, um Item de saldo inicial e inserido nessa data antes das Transacoes do mesmo dia. O item usa a Conta financeira como identificador da operacao, preserva o sinal do saldo inicial e e omitido quando esse saldo e zero. O frontend parte do Saldo de abertura e soma os valores dos itens em ordem para derivar os saldos diarios. Esses itens nunca sao persistidos.

Receitas e despesas simples podem substituir atomicamente Situacao, Conta financeira, Categoria, tipo, descricao, observacoes, valor e Data financeira. Efetivar uma planejada registra um novo instante; corrigir uma efetivada preserva esse instante; replanejar o limpa. Uma Conta financeira ou Categoria inativada depois da associacao pode ser mantida, mas uma nova associacao exige recurso ativo do mesmo Usuario. A movimentacao para outra Conta financeira tambem exige a mesma moeda e Data financeira compativel com o saldo inicial.

Contas podem ser ativadas e inativadas repetidamente sem perder historico. Categorias tambem podem ser ativadas e inativadas e classificam receitas e despesas simples. Sua hierarquia admite profundidade arbitraria; os casos de uso de categorias detectam ciclos indiretos e verificam que pai e filha pertencem ao mesmo usuario. Transferencias nao possuem Categoria.

## Contas financeiras

Uma Conta financeira pertence a um Usuario e possui nome, moeda, saldo inicial, data do saldo inicial e situacao. O nome e armazenado sem espacos nas extremidades e e unico por Usuario sem diferenciar caixa, inclusive entre contas inativas. O MVP aceita somente `BRL` e representa o saldo inicial em `NUMERIC(19,4)`, permitindo valores positivos, negativos ou zero.

Moeda, saldo inicial e data do saldo inicial formam os Dados iniciais da conta. Eles ficam irreversivelmente bloqueados no primeiro vinculo com uma operacao financeira persistida ou com uma fonte de operacoes virtuais. Transacoes e lados de Transferencias acionam o bloqueio por meio de `TB006_TRANSACAO`; Segmentos de recorrencia e futuros Parcelamentos o acionam pela estrutura que projeta suas ocorrencias. Excluir posteriormente essas operacoes nao libera os Dados iniciais.

A data de uma Transacao ou o inicio de um Segmento nao pode anteceder a data do saldo inicial. Novas associacoes exigem Conta financeira ativa; a inativacao nao altera nem impede a manutencao das operacoes que ja estavam associadas.

Categorias sao cadastradas ativas. Nome e unico sem diferenciar caixa entre categorias irmas, inclusive entre categorias raiz e independentemente da situacao. Uma categoria so pode ser cadastrada ou movida para um pai ativo. A ativacao exige todos os ancestrais ativos, enquanto a inativacao exige que nao existam descendentes ativos. Edicoes podem alterar dados, posicao e situacao atomicamente e nunca podem formar ciclos.

## Recorrencia

Um grupo de recorrencia preserva somente a origem comum. Cada segmento possui dados financeiros proprios, DTSTART e uma RRULE canonica sem o prefixo `RRULE:`. O MVP planejado aceita `DAILY`, `WEEKLY`, `MONTHLY` e `YEARLY`, alem de `INTERVAL`, `COUNT`, `UNTIL`, `BYDAY` e `BYMONTHDAY`.

Editar somente uma ocorrencia a transforma em excecao: ela permanece no grupo, mas deixa de pertencer ao segmento. Editar a partir de uma ocorrencia encerra o segmento anterior e cria outro, com numeracao independente. A implementacao da expansao, validacao completa da RFC e edicao de segmentos pertence ao marco do motor de recorrencia.

## Transferencias

Uma Transferencia possui duas Transacoes sem Categoria: uma despesa na Conta financeira de origem e uma receita na Conta financeira de destino. Situacao, Data financeira, valor, descricao, observacoes e instante de efetivacao pertencem a operacao logica, permanecem iguais nos tres registros e sao alterados atomicamente. Os lados vinculados nao podem ser editados ou excluidos isoladamente.

No MVP, as contas pertencem ao mesmo Usuario, sao diferentes e usam a mesma moeda. A criacao e uma nova associacao exigem Conta financeira ativa; manter uma associacao existente continua permitido depois da inativacao. `PLANEJADA` e `EFETIVADA` sao as unicas Situacoes. Efetivar exige Data financeira igual ou anterior a data atual no fuso do Usuario, registra um unico instante nos tres registros e replanejar o limpa.

Transferencias sao criadas, substituidas ou excluidas por contratos proprios de escrita, sempre com valor nominal positivo. Sua leitura ocorre somente na consulta unificada de Transacoes. Cada lado usa o identificador da Transferencia como identificador da operacao, informa origem `TRANSFERENCIA`, Conta contraparte, Categoria nula e impacto financeiro proprio: negativo na origem e positivo no destino. Quando ambos participam do calculo, seus valores se anulam.

## Limites desta entrega

Esta entrega fornece migrations, entidades, repositorios, constraints estruturais, cadastro de usuario, autenticacao JWT, gestao de categorias, gestao de Contas financeiras, o ciclo completo de receitas e despesas simples e a consulta unificada com Saldo de abertura. A expansao ou edicao de recorrencias permanece para o proximo marco. Quando esse modulo estiver disponivel, ocorrencias virtuais anteriores ao intervalo participarao do Saldo de abertura e ocorrencias dentro do intervalo aparecerao na linha do tempo.

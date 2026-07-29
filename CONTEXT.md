# Contexto do Economize

## Glossario

- **Usuario**: identidade que possui credencial e e proprietaria dos dados financeiros.
- **Cadastro de usuario**: criacao autonoma de um Usuario ativo com inicio imediato de uma sessao autenticada.
- **Conta financeira**: local ao qual transacoes pertencem e que define moeda e saldo inicial.
- **Situacao da conta financeira**: disponibilidade operacional de uma Conta financeira, representada por `ATIVA` ou `INATIVA`.
- **Dados iniciais da conta**: moeda, saldo inicial e data do saldo inicial definidos no cadastro de uma Conta financeira.
- **Categoria**: classificacao hierarquica opcional de receitas e despesas.
- **Situacao da categoria**: disponibilidade de uma Categoria, representada por `ATIVA` ou `INATIVA`.
- **Transacao**: registro financeiro de receita, despesa ou lado interno de uma transferencia.
- **Transacao simples**: receita ou despesa sem vinculo com Transferencia, Grupo de recorrencia, Segmento de recorrencia ou Parcelamento, mantida diretamente pelo modulo de Transacoes.
- **Situacao da transacao**: condicao de uma Transacao simples, representada no MVP por `PLANEJADA` ou `EFETIVADA`.
- **Data financeira**: data civil que posiciona uma Transacao na linha do tempo financeira, independentemente do instante em que ela foi efetivada.
- **Instante de efetivacao**: instante em que uma Transacao passou para `EFETIVADA`; e preservado em correcoes enquanto ela permanece efetivada e limpo ao replanejar.
- **Transferencia**: operacao atomica que vincula uma saida e uma entrada entre contas.
- **Situacao da transferencia**: condicao compartilhada pela Transferencia e suas duas Transacoes, representada no MVP por `PLANEJADA` ou `EFETIVADA`.
- **Grupo de recorrencia**: origem comum preservada para segmentos e excecoes recorrentes.
- **Segmento de recorrencia**: trecho independente de uma recorrencia governado por uma RRULE.
- **Ocorrencia recorrente**: instancia financeira prevista por um Segmento de recorrencia, virtual enquanto apenas projetada e persistida quando efetivada ou individualizada.
- **Excecao recorrente**: ocorrencia persistida ou supressao que deixa de seguir os dados do Segmento sem perder a identidade de sua origem.
- **Parcelamento**: plano financeiro finito composto por parcelas numeradas, com valor por parcela e quantidade total original definidos na criacao.
- **Parcela**: ocorrencia numerada de um Parcelamento, virtual enquanto apenas projetada e persistida quando efetivada ou individualizada.
- **Total contratado original**: valor por parcela multiplicado pela quantidade total original de um Parcelamento, preservado mesmo depois de edicoes ou cancelamentos.
- **Total atual do parcelamento**: soma derivada das parcelas existentes de um Parcelamento depois de edicoes e cancelamentos.

## Convencoes de linguagem

- Use `Transacao`, e nao `Lancamento`, para o registro financeiro persistido em `TB006_TRANSACAO`.
- Use nomes de casos de uso orientados a intencao, como `CriarTransacao` e `EfetivarTransacao`.
- `Service` nao nomeia um conceito do dominio e nao deve substituir uma intencao especifica.
- Use `CadastrarCategoria`, `EditarCategoria` e `ListarCategorias` para as intencoes de gestao de categorias.
- Use `CadastrarConta`, `EditarConta` e `ListarContas` para as intencoes de gestao de Contas financeiras.

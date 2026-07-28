# Contexto do Economize

## Glossario

- **Usuario**: identidade que possui credencial e e proprietaria dos dados financeiros.
- **Cadastro de usuario**: criacao autonoma de um Usuario ativo com inicio imediato de uma sessao autenticada.
- **Conta financeira**: local ao qual transacoes pertencem e que define moeda e saldo inicial.
- **Categoria**: classificacao hierarquica opcional de receitas e despesas.
- **Situacao da categoria**: disponibilidade de uma Categoria, representada por `ATIVA` ou `INATIVA`.
- **Transacao**: registro financeiro de receita, despesa ou lado interno de uma transferencia.
- **Transferencia**: operacao atomica que vincula uma saida e uma entrada entre contas.
- **Grupo de recorrencia**: origem comum preservada para segmentos e excecoes recorrentes.
- **Segmento de recorrencia**: trecho independente de uma recorrencia governado por uma RRULE.

## Convencoes de linguagem

- Use `Transacao`, e nao `Lancamento`, para o registro financeiro persistido em `TB006_TRANSACAO`.
- Use nomes de casos de uso orientados a intencao, como `CriarTransacao` e `EfetivarTransacao`.
- `Service` nao nomeia um conceito do dominio e nao deve substituir uma intencao especifica.
- Use `CadastrarCategoria`, `EditarCategoria` e `ListarCategorias` para as intencoes de gestao de categorias.

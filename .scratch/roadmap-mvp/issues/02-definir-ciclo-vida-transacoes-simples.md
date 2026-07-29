# Definir o ciclo de vida das transacoes simples

Parent: [Reestruturar o roadmap do MVP por dependencias](../map.md)
Type: grilling
Status: resolved
Blocked by: 01

## Question

Quais operacoes e transicoes de estado devem compor a gestao de receitas e despesas sem repeticao no MVP, quais campos podem ser alterados em cada estado e como edicao, efetivacao e cancelamento preservam o historico financeiro?

## Answer

O marco de gestao de Transacoes simples deve entregar `CriarTransacao`, `AlterarTransacao` e `ExcluirTransacao`. Consultas pertencem ao marco seguinte. Esses fluxos tratam somente receitas e despesas sem Grupo ou Segmento de recorrencia e sem vinculo com Transferencia; ocorrencias recorrentes e lados de Transferencias sao administrados pelos respectivos modulos.

A criacao aceita a situacao inicial `PLANEJADA` ou `EFETIVADA`. `AlterarTransacao` pode mudar atomicamente situacao, Conta financeira, Categoria, tipo, descricao, observacoes, valor e vencimento. Uma Transacao pode transitar livremente entre `PLANEJADA` e `EFETIVADA`: ao nascer efetivada ou passar de planejada para efetivada, uma data civil igual ou anterior a data atual deve ser informada e passa a ser seu vencimento, enquanto `efetivadoEm` recebe o instante atual para auditoria; alterar a data de uma efetivada atualiza seu vencimento e preserva o instante original; ao voltar para planejada, `efetivadoEm` e limpo; uma efetivacao posterior registra um novo instante.

O tipo pode alternar entre `RECEITA` e `DESPESA`, mas nunca para `TRANSFERENCIA`. A troca de Conta financeira exige que origem e destino pertencam ao Usuario, estejam ativas, usem a mesma moeda e que o vencimento nao anteceda a data do saldo inicial da conta de destino. Uma Categoria inativa ja associada pode ser mantida ou removida durante outra alteracao, mas qualquer nova associacao exige Categoria ativa e pertencente ao Usuario.

Contas inativas bloqueiam criacao e alteracao, inclusive mudanca de situacao, mas permitem excluir Transacoes existentes. Em todos os fluxos, recursos identificados sao buscados no escopo do Usuario autenticado e a API nao revela se um recurso pertence a outro Usuario.

A exclusao e fisica, definitiva e permitida para Transacoes `PLANEJADAS` e `EFETIVADAS`; nao ha lixeira, restauracao nem historico de versoes financeiras no MVP. Excluir ou alterar uma Transacao efetivada muda naturalmente o saldo derivado, como a correcao de uma linha em uma planilha. `CANCELADA` deixa de integrar o ciclo de vida das Transacoes simples e o modelo persistido e a documentacao deverao ser adequados antes da implementacao.

O marco esta concluido quando os tres fluxos possuem contrato HTTP, casos de uso, isolamento por proprietario, validacao das invariantes de conta, categoria, tipo, valor, vencimento e situacao, exclusao fisica protegida contra registros de recorrencia ou Transferencia e testes de integracao. O saldo nao e armazenado e nao exige atualizacao explicita nesses fluxos.

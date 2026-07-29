# Definir o contrato de gestao de contas financeiras

Parent: [Reestruturar o roadmap do MVP por dependencias](../map.md)
Type: grilling
Status: resolved

## Question

Quais fluxos, regras de propriedade e ciclo de vida, dados editaveis, efeitos da ativacao ou inativacao e criterios de conclusao o marco de gestao de contas financeiras deve estabelecer para desbloquear com seguranca transacoes, saldos e transferencias?

## Answer

O marco de gestao de contas financeiras deve entregar os fluxos de cadastrar, listar, editar e alterar situacao. Nao havera consulta individual, exclusao fisica nem calculo de saldo neste marco. O cadastro cria a conta ativa; a listagem retorna contas ativas e inativas por padrao, aceita filtro opcional por situacao, ordena de forma deterministica por nome sem diferenciar caixa e nao usa paginacao.

Todos os fluxos derivam o proprietario do usuario autenticado. Operacoes identificadas buscam a conta por `contaId + usuarioId` e tratam conta inexistente ou pertencente a outro usuario da mesma forma, sem revelar sua existencia.

O nome e obrigatorio, normalizado e unico por usuario sem diferenciar caixa, inclusive entre contas inativas. Usuarios diferentes podem reutilizar o mesmo nome. O MVP aceita somente a moeda `BRL`, preservada explicitamente no modelo e nas respostas para evolucao futura; cadastro e edicao rejeitam qualquer outra moeda. O saldo inicial aceita valores positivos, negativos ou zero dentro da precisao monetaria persistida. A data do saldo inicial deve ser igual ou anterior a data atual no fuso do usuario.

Nome pode ser editado a qualquer momento. Moeda, saldo inicial e data do saldo inicial somente podem ser alterados enquanto nunca tiver existido uma Transacao na conta. Qualquer Transacao persistida conta como historico para esse bloqueio, incluindo planejadas, efetivadas, canceladas e os lados de Transferencias.

Uma Transacao, lado de Transferencia ou ocorrencia materializada nao pode ter data anterior a data do saldo inicial da conta. Essa regra deve ser protegida pelos casos de uso que criam ou alteram operacoes financeiras.

Contas podem ser ativadas e inativadas repetidamente. A inativacao nao cancela nem altera Transacoes ou Transferencias em cascata e nao e impedida pela existencia de operacoes planejadas. Enquanto inativa, a conta nao admite novas operacoes financeiras, mas operacoes existentes continuam disponiveis para consulta, edicao, mudanca de situacao e exclusao conforme seus proprios ciclos de vida. Esta regra foi revisada por [Definir o ciclo de vida das transferencias](04-definir-ciclo-vida-transferencias.md).

O marco esta concluido quando os quatro fluxos possuem contrato HTTP, casos de uso, isolamento por proprietario, invariantes protegidas na aplicacao e no banco quando aplicavel e testes de integracao. Consultas de Transacoes e calculo de saldo pertencem ao marco dependente.

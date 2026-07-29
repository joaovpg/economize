# Gestao de Transacoes simples

Status: ready-for-agent

## Problem Statement

O Usuario atualmente consegue criar apenas receitas e despesas planejadas. O contrato e o modelo persistente ainda usam conceitos legados como `status`, `dataVencimento` e `CANCELADA`, que divergem do vocabulario e do ciclo de vida definidos para o MVP.

Nao e possivel criar uma Transacao efetivada, corrigir uma Transacao existente, alternar sua Situacao ou exclui-la definitivamente. Tambem faltam protecoes completas para impedir que o fluxo de Transacoes simples altere operacoes pertencentes a Transferencias, Recorrencias ou Parcelamentos.

## Solution

Completar o ciclo de vida de Transacoes simples, permitindo criacao explicita como `PLANEJADA` ou `EFETIVADA`, alteracao completa e atomica, transicoes entre as duas Situacoes e exclusao fisica.

O contrato HTTP, o codigo Java, o banco e a documentacao usarao o vocabulario canonico Situacao da transacao e Data financeira. `CANCELADA` sera removida do modelo de Transacoes, e a propriedade dos recursos e os vinculos com outros modulos serao protegidos.

Uma Transacao simples e uma receita ou despesa sem vinculo com Transferencia, Grupo de recorrencia, Segmento de recorrencia ou Parcelamento.

## User Stories

1. As an Usuario, I want criar uma receita planejada, so that eu registre uma entrada financeira futura.
2. As an Usuario, I want criar uma despesa planejada, so that eu registre uma saida financeira futura.
3. As an Usuario, I want criar uma receita efetivada, so that eu registre uma entrada que ja aconteceu.
4. As an Usuario, I want criar uma despesa efetivada, so that eu registre uma saida que ja aconteceu.
5. As an Usuario, I want escolher explicitamente a Situacao ao criar uma Transacao, so that eu nao dependa de comportamento implicito.
6. As an Usuario, I want informar a Data financeira de uma Transacao, so that ela seja posicionada corretamente na linha do tempo.
7. As an Usuario, I want ver quando uma Transacao foi efetivada, so that eu diferencie a Data financeira do instante da efetivacao.
8. As an Usuario, I want alterar atomicamente os dados de uma Transacao, so that ela nunca permaneca parcialmente atualizada.
9. As an Usuario, I want mudar uma Transacao de planejada para efetivada, so that eu registre sua realizacao.
10. As an Usuario, I want replanejar uma Transacao efetivada, so that eu corrija uma efetivacao indevida.
11. As an Usuario, I want efetivar novamente uma Transacao replanejada, so that um novo instante de efetivacao seja registrado.
12. As an Usuario, I want corrigir a Data financeira de uma Transacao efetivada sem perder seu instante original, so that o historico de efetivacao seja preservado.
13. As an Usuario, I want alternar uma Transacao entre receita e despesa, so that eu corrija sua natureza financeira.
14. As an Usuario, I want mover uma Transacao para outra Conta financeira valida, so that eu corrija sua associacao.
15. As an Usuario, I want manter uma Transacao em uma Conta financeira posteriormente inativada, so that eu preserve e corrija meu historico.
16. As an Usuario, I want manter ou remover uma Categoria posteriormente inativada, so that eu corrija a Transacao sem perder uma classificacao existente.
17. As an Usuario, I want associar somente Categorias ativas a novas relacoes, so that sua disponibilidade operacional seja respeitada.
18. As an Usuario, I want excluir definitivamente uma Transacao planejada, so that uma operacao indevida deixe de existir.
19. As an Usuario, I want excluir definitivamente uma Transacao efetivada, so that meu historico e os saldos derivados sejam corrigidos.
20. As an Usuario, I want que operacoes de outros Usuarios permanecam ocultas, so that minha privacidade financeira seja preservada.
21. As an Usuario, I want receber uma explicacao ao tentar editar uma Transacao pertencente a outro fluxo, so that eu use o modulo correto.
22. As an Usuario, I want que Transacoes efetivadas rejeitem datas futuras, so that eventos realizados mantenham coerencia temporal.
23. As an Usuario, I want que a data atual considere meu fuso cadastrado, so that nao ocorram divergencias perto da meia-noite.
24. As an Usuario, I want que nenhuma Transacao anteceda o saldo inicial da Conta financeira, so that a base de calculo seja preservada.
25. As an Usuario, I want que valores sejam positivos e monetariamente precisos, so that a integridade financeira seja mantida.
26. As an mantenedor, I want remover estados e nomes legados, so that os proximos marcos usem um modelo consistente.
27. As an mantenedor, I want migrar registros cancelados segundo uma politica explicita, so that o ciclo de vida seja estreitado sem deixar dados invalidos.
28. As an mantenedor, I want constraints alinhadas a aplicacao, so that gravacoes invalidas sejam impedidas por qualquer caminho.

## Implementation Decisions

- O modulo de Transacoes continuara organizado por dominio, preservando a separacao entre HTTP, aplicacao e persistencia.
- Entidades JPA permanecerao modelos passivos e nao serao expostas pela API.
- O vocabulario canonico sera Situacao da transacao e Data financeira.
- `status` sera renomeado para `situacao` em HTTP e Java.
- `dataVencimento` sera renomeado para `dataFinanceira` em HTTP e Java.
- As colunas correspondentes serao renomeadas incrementalmente para `STR_SITUACAO` e `DAT_FINANCEIRA`.
- A migration inicial compartilhada nao sera alterada.
- O enum de Situacao da transacao contera somente `PLANEJADA` e `EFETIVADA`.
- A criacao exigira `situacao` explicitamente.
- O `POST /transacoes` aceitara somente `RECEITA` e `DESPESA`.
- Criar como `PLANEJADA` persistira `efetivadoEm` nulo.
- Criar como `EFETIVADA` exigira `dataFinanceira` igual ou anterior a data atual no fuso do Usuario e registrara o instante atual em `efetivadoEm`.
- As respostas de criacao e alteracao exporao `efetivadoEm` em ISO-8601, nulo para Transacoes planejadas.
- A alteracao sera feita por `PUT /transacoes/{id}` e exigira o estado mutavel completo.
- O `PUT` podera alterar atomicamente Situacao, Conta financeira, Categoria, tipo, descricao, observacoes, valor e Data financeira.
- A transicao de `PLANEJADA` para `EFETIVADA` registrara um novo `efetivadoEm`.
- Alterar uma Transacao que continua efetivada preservara o `efetivadoEm` original, inclusive quando a Data financeira mudar.
- A transicao de `EFETIVADA` para `PLANEJADA` limpara `efetivadoEm`.
- Efetivar novamente uma Transacao replanejada registrara outro instante.
- Uma Conta financeira existente podera ser mantida mesmo inativa.
- Mover a Transacao exigira uma nova Conta financeira ativa, do mesmo Usuario, da mesma moeda e com data do saldo inicial compativel.
- Uma Categoria existente podera ser mantida mesmo inativa ou removida.
- Uma nova associacao com Categoria exigira uma Categoria ativa do mesmo Usuario.
- Recursos inexistentes ou pertencentes a outro Usuario retornarao `404` sem revelar sua existencia.
- Transacoes vinculadas a outros modulos nao poderao ser alteradas ou excluidas pelo fluxo simples.
- O uso do fluxo simples sobre uma Transacao vinculada do proprio Usuario retornara `422` com codigo estavel `TRANSACAO_NAO_SIMPLES`.
- A exclusao sera feita por `DELETE /transacoes/{id}` e retornara `204 No Content`.
- Uma nova tentativa apos a exclusao fisica retornara `404`.
- Nao havera lixeira, restauracao ou versionamento financeiro.
- A exclusao nao desbloqueara os Dados iniciais da conta.
- A migration excluira fisicamente Transacoes legadas `CANCELADA`.
- Quando uma Transacao cancelada for lado de uma Transferencia cancelada, a Transferencia e seus dois lados serao removidos como conjunto antes das demais Transacoes canceladas.
- A politica destrutiva sobre dados cancelados sera documentada em ADR.
- As funcoes, triggers, indices e constraints que referenciam as colunas renomeadas serao adequados na mesma migration.
- A coerencia entre Situacao e `efetivadoEm` permanecera protegida no banco.
- O modelo financeiro sera corrigido para remover descricoes legadas de cancelamento e estorno incompativeis com o roadmap.
- O roadmap sera atualizado para marcar o Marco 2 como concluido somente apos implementacao e verificacao integral.

## Testing Decisions

- O principal seam sera o contrato HTTP com testes de integracao `@QuarkusTest`.
- Os testes HTTP observarao respostas, persistencia resultante e regras do dominio, sem testar detalhes internos de resources, mappers ou entidades.
- A criacao planejada existente fornece o precedente para autenticacao, isolamento, validacao e estrutura das respostas.
- Serao cobertas criacoes planejadas e efetivadas para receitas e despesas.
- Serao testadas datas efetivadas passadas, atuais e futuras usando o fuso do Usuario.
- Sera testada a coerencia de `efetivadoEm` em criacao, correcao, efetivacao, replanejamento e nova efetivacao.
- Serao testadas alteracoes de todos os campos mutaveis e sua atomicidade diante de falhas.
- Serao cobertos Conta financeira e Categoria inexistentes, alheias, ativas e inativas.
- Sera testada a diferenca entre manter uma associacao inativa existente e criar uma nova associacao.
- Serao testadas Data financeira anterior ao saldo inicial e mudanca para Conta financeira incompativel.
- Serao testados isolamento por Usuario e ausencia de revelacao de recursos alheios.
- Serao testadas tentativas de alterar ou excluir Transacoes vinculadas.
- Serao testadas exclusao planejada, exclusao efetivada, ausencia posterior e segundo `DELETE`.
- O seam complementar sera a execucao Flyway sobre schema temporario, seguindo o precedente dos testes de migration existentes.
- Os testes de migration validarao renomeacoes, constraints, coerencia entre Situacao e efetivacao e remocao do legado cancelado.
- A migration sera testada com Transacao cancelada simples e Transferencia cancelada com seus dois lados.
- Nao serao criados testes de getters, setters, mappers gerados ou comportamento proprio do framework.
- Testes diretos de repository serao adicionados somente se uma query customizada nao puder ser protegida adequadamente pelos seams superiores.
- A entrega final exigira `mvnw verify -B` aprovado contra PostgreSQL.

## Out of Scope

- Consulta individual de Transacao.
- Listagem e filtros de Transacoes.
- Calculo de saldo atual ou projetado.
- Transferencias simples e seu novo ciclo de vida, alem da adequacao minima necessaria para remover Transacoes canceladas.
- Criacao ou manutencao de Recorrencias.
- Parcelamentos.
- Cancelamento, restauracao, lixeira ou versionamento financeiro.
- Estornos.
- Exclusao logica.
- Transacoes do tipo `TRANSFERENCIA` pelo endpoint de Transacoes simples.
- Atualizacao parcial por `PATCH`.
- Compatibilidade retroativa com os campos HTTP `status` e `dataVencimento`.
- Alteracao da migration inicial compartilhada.

## Further Notes

- A implementacao sera dividida em quatro fatias: adequacao de vocabulario e schema preservando a criacao planejada; criacao efetivada; exclusao fisica; e alteracao com transicoes.
- A adequacao de vocabulario e schema bloqueia as demais fatias.
- A criacao efetivada estabelece a semantica temporal usada pelos testes de exclusao e pelo fluxo de alteracao.
- O documento de modelo financeiro contem afirmacoes legadas sobre Transacoes canceladas e Transferencias canceladas ou estornadas que deverao ser corrigidas.

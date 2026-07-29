# Reestruturar o roadmap do MVP por dependencias

## Destination

Um `docs/roadmap.md` revisado que torne explicitos o escopo, a ordem obrigatoria, as dependencias, as regras criticas, os dados envolvidos e os criterios de conclusao do MVP, mantendo as capacidades futuras em nivel suficiente para orientar `/to-spec`, `/to-tickets` e agentes de implementacao.

## Notes

- Dominio: backend financeiro pessoal em Java 25 e Quarkus, organizado como monolito modular.
- Consultar `CONTEXT.md`, `docs/modelo-financeiro.md`, `docs/roadmap.md`, `docs/adr/` e o comportamento real do codigo antes de resolver cada ticket.
- Usar `/grilling` e `/domain-modeling` nos tickets de decisao. Registrar termos consolidados no glossario quando necessario.
- O roadmap sera a autoridade sobre ordem, dependencias, escopo resumido e criterios de saida. O modelo financeiro preservara entidades e invariantes; especificacoes futuras detalharao contratos e cenarios; tickets futuros descreverao trabalho executavel e bloqueios.
- Usar os termos `Cadastro de usuario`, `Conta financeira`, `Categoria`, `Transacao` e `Transferencia` conforme o glossario. Evitar `criacao de conta` para cadastro de usuario e evitar `conta bancaria` quando o conceito for mais amplo.
- Estado confirmado: persistencia do nucleo financeiro, cadastro de usuario, login e gestao de categorias estao implementados; contas financeiras possuem apenas persistencia; transacoes possuem apenas criacao planejada de receita e despesa; consultas, saldos, transferencias operacionais, recorrencias e parcelamentos nao estao implementados.
- Categorias e contas financeiras formam um gate obrigatorio antes da continuidade da implementacao de transacoes, ainda que categoria seja opcional em uma transacao.
- O MVP inclui transferencias simples, mas nao transferencias recorrentes ou parceladas.
- Categorias e contas sao inativadas. Transacoes simples podem ser excluidas fisicamente como correcao direta dos registros; o ciclo de vida de Transferencias sera definido em ticket proprio.
- Consultas de Transacoes serao limitadas a um unico mes civil, usarao o mes atual por padrao e nao terao paginacao.
- A criacao de receitas e despesas usara uma request discriminada com os modos sem repeticao, parcelamento mensal e avancado. Transferencias terao contrato proprio.
- O modo avancado permitira intervalo configuravel e termino por parcelas, quantidade de ocorrencias ou repeticao indefinida.
- O escopo futuro incluira analises financeiras assistidas por IA em modo consultivo, sem criar, editar ou efetivar operacoes financeiras.
- Esta iniciativa produz decisoes e uma rota clara. Ela nao implementa as funcionalidades do MVP.

## Decisions so far

- [Definir o contrato de gestao de contas financeiras](issues/01-definir-contrato-gestao-contas-financeiras.md) — O marco entrega cadastro, listagem, edicao e ativacao/inativacao com isolamento por proprietario; a inativacao impede novas operacoes, mas nao altera o ciclo das existentes.
- [Definir o ciclo de vida das transacoes simples](issues/02-definir-ciclo-vida-transacoes-simples.md) — Receitas e despesas podem nascer planejadas ou efetivadas, ser integralmente alteradas entre essas situacoes e ser excluidas fisicamente, sem versionamento financeiro no MVP.
- [Definir consultas de transacoes e calculo de saldos](issues/03-definir-consultas-transacoes-saldos.md) — A consulta mensal sem paginacao unifica Transacoes persistidas e ocorrencias recorrentes virtuais e calcula saldos atual e projetado por conta e consolidados em BRL.
- [Definir o ciclo de vida das transferencias](issues/04-definir-ciclo-vida-transferencias.md) — Transferencias e seus dois lados sao criados, editados, efetivados, replanejados e excluidos atomicamente, com consulta mensal e apenas as situacoes planejada e efetivada.
- [Definir o contrato de repeticao e materializacao RRULE](issues/05-definir-contrato-repeticao-rrule.md) — O modo avancado gera RRULE canonica a partir de campos estruturados e projeta ocorrencias virtuais sem horizonte, persistindo somente efetivacoes e excecoes identificadas pela origem.
- [Definir a semantica dos parcelamentos](issues/06-definir-semantica-parcelamentos.md) — Parcelamentos sao planos finitos com valor por parcela, numeracao e total original estaveis, cadencia unica que nao omite meses e edicao ou cancelamento por ocorrencia ou trecho futuro.
- [Ordenar os marcos posteriores ao MVP](issues/07-ordenar-marcos-posteriores-mvp.md) — O pos-MVP tera prioridade sugerida com gates por capacidade, Relatorios e IA desacoplados de bloqueios artificiais e recursos avancados separados para priorizacao futura.
- [Levantar impactos documentais e de schema](issues/08-levantar-impactos-documentais-schema.md) — As decisoes requerem correcoes coordenadas na documentacao e migrations incrementais para ciclos de vida, integridade de Transferencias, identidade e supressao recorrente e metadados estaveis de Parcelamentos.
- [Definir a decomposicao final dos marcos do MVP](issues/09-definir-decomposicao-final-marcos-mvp.md) — O roadmap separara a base implementada e ordenara seis marcos por dependencias, com Transferencias e Recorrencias paralelizaveis, estrutura uniforme e um gate tecnico global para concluir o MVP.

## Not yet specified

Nenhuma no momento.

## Out of scope

- Implementar endpoints, casos de uso, migrations ou outras funcionalidades do produto durante o wayfinding.
- Definir no roadmap contratos HTTP campo a campo ou cenarios exaustivos; esse detalhamento pertencera as especificacoes geradas posteriormente.
- Implementar ou especificar o frontend usado como referencia para os tres modos de repeticao.
- Detalhar neste esforco orcamentos, cartao de credito, relatorios, conversao entre moedas, divisao por categorias, compartilhamento de dados ou analises por IA alem do necessario para posiciona-los no roadmap futuro.

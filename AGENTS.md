# Instruções para agentes

Este arquivo governa todo o repositório. Instruções explícitas da tarefa prevalecem sobre ele. Fora disso, siga estas regras em todo código novo ou diretamente alterado.

## Contexto do projeto

O Economize é um backend financeiro pessoal em Java 25 e Quarkus, estruturado como monólito modular por domínio. A implementação atual cobre o núcleo de persistência: entidades JPA, repositórios Panache, migrations Flyway e testes de integração. Não presuma que já existam endpoints ou casos de uso de negócio.

Antes de alterar o domínio financeiro, consulte:

- [`docs/modelo-financeiro.md`](docs/modelo-financeiro.md), fonte das entidades, invariantes e convenções físicas;
- [`docs/roadmap.md`](docs/roadmap.md), fonte do escopo e da ordem das entregas.

Use português brasileiro nos conceitos do negócio e na documentação. Preserve em inglês termos técnicos, APIs e padrões consagrados. Identificadores Java e SQL permanecem sem acentos.

## Fluxo de trabalho

1. Inspecione o código, os testes e a documentação relacionados antes de propor alterações.
2. Confirme o comportamento real no repositório; não invente comandos, endpoints ou ferramentas.
3. Faça a menor mudança coerente que resolva a tarefa.
4. Preserve alterações preexistentes e não relacionadas no worktree.
5. Atualize documentação quando mudar comandos, configuração, arquitetura, comportamento público, modelo financeiro ou roadmap.
6. Execute verificações proporcionais ao risco e informe claramente qualquer verificação não executada.

Não faça limpeza, atualização de dependências ou refatoração ampla fora do escopo. Registre problemas adjacentes em vez de corrigi-los silenciosamente.

## Arquitetura

Organize o código por módulo de domínio, não em pacotes globais por camada. Cada módulo pode conter suas próprias entradas HTTP, aplicação, domínio e persistência quando houver código real para essas responsabilidades. Não crie pacotes, interfaces ou abstrações vazias como preparação especulativa.

Módulos começam em um package plano. Quando o volume prejudicar a navegação, subdivida somente as responsabilidades existentes em `http`, `application`, `domain` e `persistence`. Em `http`, DTOs podem ser agrupados em `dto/request` e `dto/response`. DTOs e mappers nunca acessam repositories; mappers permanecem junto ao adapter que traduzem e não contêm regras de negócio.

A direção das dependências é:

```text
HTTP e infraestrutura -> aplicação e domínio
```

O domínio não depende de REST, DTOs ou integrações externas. Módulos colaboram por contratos públicos pequenos. Não acesse detalhes internos de outro módulo. Coloque em `shared` apenas conceitos realmente transversais; não o transforme em um pacote genérico de utilitários.

Use a menor visibilidade possível. Torne público somente o que precisa atravessar uma fronteira real.

### HTTP

- Resources validam somente o contrato HTTP, obtêm o contexto de autenticação, mapeiam a entrada, invocam um caso de uso e traduzem o resultado para HTTP.
- Resources não acessam repositories diretamente e não controlam transações.
- Resources não executam regras de negócio, verificações de propriedade de recursos nem composição de entidades.
- Entidades JPA nunca são contratos de entrada ou saída da API.
- Prefira `record` para DTOs imutáveis quando compatível com Jackson, Bean Validation e MapStruct.
- DTOs HTTP pertencem ao módulo e ao adapter HTTP, não a um pacote global `dto`.
- Retorne erros em formato consistente, com código estável, mensagem legível e detalhes de campos quando aplicável.
- Nunca exponha stack traces, SQL, entidades ou nomes internos de classes.

### Aplicação e domínio

- Nomeie casos de uso pela intenção, como `CriarConta`, `RegistrarDespesa` ou `TransferirSaldo`. Evite `Service` quando o nome não explicar a responsabilidade.
- Casos de uso coordenam o fluxo, validam invariantes, verificam autorização sobre recursos, compõem entidades e delimitam transações.
- Entidades JPA são modelos passivos de persistência: representam estado e mapeamento, sem factories, validações ou regras de negócio.
- Não force DDD cerimonial: use código direto e crie abstrações somente diante de uma fronteira real ou ganho concreto de testabilidade.
- Use exceções específicas para falhas de domínio ou aplicação e traduza-as para HTTP em uma fronteira central.
- Não capture `Exception` genericamente nem use exceções para fluxo normal.

### Persistência

- Repositories pertencem ao módulo e são usados por casos de uso ou componentes de persistência, nunca por resources.
- Relacionamentos JPA devem ser `LAZY` por padrão.
- Persista enums como strings.
- Use UUID para identificadores, `BigDecimal` para dinheiro, `LocalDate` para datas financeiras e `Instant` para eventos e timestamps.
- Nunca use `float` ou `double` para valores monetários.
- Evite `null` em contratos públicos. Use `Optional` apenas como retorno que representa ausência, não em campos, parâmetros ou DTOs.
- Use injeção por construtor para dependências obrigatórias; evite field injection.
- Use Lombok somente para reduzir boilerplate de entidades JPA, principalmente com `@Getter`, `@Setter` e `@NoArgsConstructor`.
- Não use `@Data` ou `@Builder` em entidades JPA: preserve o controle sobre identidade, relacionamentos e composição de estados pela aplicação.
- Prefira `record` para DTOs, comandos e resultados imutáveis, e mantenha construtores explícitos para injeção de dependências obrigatórias.

## Banco e migrations

Flyway é a autoridade sobre o schema. Hibernate deve validá-lo, nunca criá-lo ou atualizá-lo automaticamente.

- Não altere uma migration que já foi compartilhada ou aplicada fora do ambiente local; crie uma nova migration versionada.
- Uma migration comprovadamente local e ainda não compartilhada pode ser corrigida diretamente.
- Preserve dados, constraints e compatibilidade com o modelo JPA.
- Mantenha as convenções de tabelas, colunas, índices e constraints descritas no modelo financeiro.
- Os números de tabela são permanentes e não podem ser reutilizados.
- Proteja invariantes na aplicação para obter bons fluxos e mensagens, e no banco para garantir integridade em qualquer caminho de escrita.

## Bibliotecas planejadas

MapStruct, REST Client e Micrometer fazem parte da direção do projeto. Use-os somente quando uma funcionalidade concreta precisar deles.

- Use MapStruct nas fronteiras entre DTOs HTTP e comandos ou resultados da aplicação, inclusive em mapeamentos pequenos. Nunca coloque regras de negócio em mappers.
- Declare mappers MapStruct como beans CDI, injete-os por construtor e use nomes técnicos em inglês, como `toCommand`, `toResponse` e `toEntity`.
- Encapsule cada REST Client no módulo responsável, com configuração externa, timeouts explícitos e tradução de falhas externas.
- Crie métricas para operações relevantes, com nomes estáveis e tags de baixa cardinalidade. Nunca use identificadores de usuário, conta ou transação como tags.

Não remova essas dependências como limpeza incidental e não crie usos fictícios apenas para justificá-las.

## Clean Code

- Prefira nomes que expressem o vocabulário do domínio.
- Mantenha regras próximas dos dados que protegem e efeitos externos nas bordas.
- Evite parâmetros booleanos ambíguos, estados parcialmente válidos e dependências ocultas.
- Não extraia helpers, factories ou interfaces sem reutilização, fronteira ou simplificação concreta.
- Comentários explicam decisões, invariantes ou motivos não óbvios; não narram o código.
- Use JavaDoc somente em contratos públicos cujo uso não seja evidente.
- Não imponha limites arbitrários de linhas, métodos ou classes. Coesão e legibilidade orientam a decomposição.

## Testes e verificação

TDD não é obrigatório. Não produza testes mecanicamente antes da solução nem testes genéricos para cumprir processo ou cobertura.

Avalie o risco depois de entender ou implementar a mudança. Adicione testes quando protegerem:

- regras de negócio não triviais;
- regressões de bugs;
- contratos HTTP;
- queries customizadas e mapeamentos críticos;
- migrations, constraints e integração com PostgreSQL.

Não teste getters, setters, código gerado pelo Lombok ou comportamento pertencente ao framework. Prefira objetos reais para o domínio. Use mocks somente em fronteiras externas ou para tornar um cenário determinístico.

Não existe meta obrigatória de cobertura. Cobertura é um sinal auxiliar, não um objetivo.

Comandos disponíveis:

```shell
./mvnw test
./mvnw verify -B
```

No Windows, use `./mvnw.cmd` ou `.\mvnw.cmd`. Execute testes relevantes durante mudanças de código e `verify -B` antes de concluir alterações de código, persistência ou configuração. Mudanças exclusivamente documentais não exigem build Maven.

Se Docker, rede ou outra ferramenta necessária estiver indisponível, não declare que a verificação passou. Informe o comando não executado, a causa e o risco residual.

## Segurança e observabilidade

- Negue acesso por padrão e valide que cada recurso pertence ao usuário autenticado.
- Não confie em identificadores enviados pelo cliente sem a autorização correspondente.
- Nunca registre ou versione senhas, tokens, credenciais, payloads financeiros completos ou dados pessoais desnecessários.
- Produza logs úteis nas fronteiras operacionais, sem duplicar ruído ou expor detalhes internos.

## Git

Comandos Git de leitura, como `status`, `diff`, `log`, `show` e `remote`, podem ser usados para compreender o repositório.

Sem autorização explícita do usuário, não:

- crie commits;
- execute push;
- faça merge;
- crie tags;
- abra pull requests;
- altere histórico, branches ou o índice Git.

Uma autorização pode cobrir um conjunto claramente solicitado, como “faça commit e push destas alterações”. Não extrapole essa autorização para operações posteriores.

Nunca reverta, sobrescreva ou inclua em uma mudança alterações preexistentes feitas por outra pessoa. Se houver conflito direto com a tarefa, pare e peça orientação.

## Proibições essenciais

- Não exponha entidades JPA pela API.
- Não acesse repositories em resources.
- Não edite migrations já compartilhadas.
- Não use ponto flutuante para dinheiro.
- Não registre segredos ou dados financeiros desnecessários.
- Não crie abstrações, eventos ou módulos especulativos.
- Não faça operações Git de escrita ou publicação sem autorização explícita.

## Agent skills

### Issue tracker

Issues são mantidas como arquivos Markdown locais em `.scratch/`. Consulte `docs/agents/issue-tracker.md`.

### Triage labels

O projeto usa os cinco labels canônicos de triage. Consulte `docs/agents/triage-labels.md`.

### Domain docs

O projeto usa o layout de domínio single-context. Consulte `docs/agents/domain.md`.

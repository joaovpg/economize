# Issue tracker: Markdown local

Issues e especificações deste repositório são mantidas como arquivos Markdown em `.scratch/`.

## Convenções

- Uma funcionalidade por diretório: `.scratch/<feature-slug>/`.
- A especificação fica em `.scratch/<feature-slug>/spec.md`.
- Issues de implementação ficam em arquivos individuais em `.scratch/<feature-slug>/issues/<NN>-<slug>.md`, numerados a partir de `01`; nunca use um único arquivo combinado de tickets.
- O estado de triage é registrado em uma linha `Status:` próxima ao início de cada issue. Consulte `triage-labels.md` para os valores.
- Comentários e o histórico da conversa são adicionados ao final do arquivo, sob o título `## Comments`.

## Quando uma skill disser "publicar no issue tracker"

Crie um arquivo em `.scratch/<feature-slug>/`, criando o diretório quando necessário.

## Quando uma skill disser "buscar o ticket relevante"

Leia o arquivo no caminho referenciado. Normalmente, o usuário informará diretamente o caminho ou o número da issue.

## Operações de wayfinding

Usadas por `/wayfinder`. O **mapa** é um arquivo com um arquivo **filho** por ticket.

- **Mapa**: `.scratch/<effort>/map.md`, contendo Notes, Decisions-so-far e Fog.
- **Ticket filho**: `.scratch/<effort>/issues/NN-<slug>.md`, numerado a partir de `01`, com a pergunta no corpo. Uma linha `Type:` registra o tipo (`research`, `prototype`, `grilling` ou `task`); uma linha `Status:` registra `claimed` ou `resolved`.
- **Bloqueio**: uma linha `Blocked by: NN, NN` próxima ao início. Um ticket está desbloqueado quando todos os arquivos listados estão com `Status: resolved`.
- **Fronteira**: examine `.scratch/<effort>/issues/` em busca de arquivos abertos, desbloqueados e não atribuídos; o primeiro por número vence.
- **Assumir**: defina `Status: claimed` e salve antes de iniciar qualquer trabalho.
- **Resolver**: acrescente a resposta sob o título `## Answer`, defina `Status: resolved` e adicione uma referência de contexto, com gist e link, a Decisions-so-far em `map.md`.

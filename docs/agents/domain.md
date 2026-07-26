# Documentação de domínio

Estas regras definem como as engineering skills devem consumir a documentação de domínio ao explorar o repositório.

## Antes de explorar

- Leia `CONTEXT.md` na raiz do repositório.
- Se existir `CONTEXT-MAP.md` na raiz, use-o para localizar o `CONTEXT.md` de cada contexto relevante.
- Leia em `docs/adr/` as ADRs relacionadas à área que será alterada. Em repositórios multi-context, verifique também `src/<context>/docs/adr/`.

Se algum desses arquivos não existir, prossiga silenciosamente. Não sinalize a ausência nem sugira sua criação antecipada. A skill `/domain-modeling`, acessada por `/grill-with-docs` e `/improve-codebase-architecture`, cria esses documentos sob demanda quando termos ou decisões são resolvidos.

## Estrutura de arquivos

Este repositório usa o layout single-context:

```text
/
├── CONTEXT.md
├── docs/adr/
│   ├── 0001-exemplo-de-decisao.md
│   └── 0002-outra-decisao.md
└── src/
```

O layout multi-context é identificado pela presença de `CONTEXT-MAP.md` na raiz:

```text
/
├── CONTEXT-MAP.md
├── docs/adr/                          <- decisões sistêmicas
└── src/
    ├── contexto-a/
    │   ├── CONTEXT.md
    │   └── docs/adr/                  <- decisões do contexto
    └── contexto-b/
        ├── CONTEXT.md
        └── docs/adr/
```

## Use o vocabulário do glossário

Quando uma saída nomear um conceito de domínio, como em título de issue, proposta de refatoração, hipótese ou nome de teste, use o termo definido em `CONTEXT.md`. Não adote sinônimos que o glossário evite explicitamente.

Se um conceito necessário ainda não estiver no glossário, reconsidere se a linguagem está sendo inventada ou registre a lacuna para `/domain-modeling`.

## Sinalize conflitos com ADRs

Se uma saída contradisser uma ADR existente, sinalize o conflito explicitamente em vez de sobrescrever silenciosamente a decisão:

> Contradiz ADR-0007, mas vale reconsiderar porque...

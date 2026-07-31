# 03 - Editar o Usuario atual

**What to build:** permitir que o Usuario autenticado substitua seu nome, sobrenome e timezone e receba o perfil atualizado, mantendo o e-mail como identidade de login somente leitura.

**Blocked by:** 02 - Consultar o Usuario atual.

**Status:** ready-for-agent

- [ ] `PUT /api/usuarios/atual` exige nome, sobrenome e timezone e retorna `200` com o perfil completo atualizado.
- [ ] Nome e sobrenome sao obrigatorios, aceitam no maximo 120 caracteres e sao persistidos sem espacos externos.
- [ ] O timezone e validado pelo mesmo criterio usado no cadastro.
- [ ] O contrato nao aceita e-mail, senha, status ou identificador do Usuario; propriedades desconhecidas continuam rejeitadas.
- [ ] O e-mail permanece inalterado depois da edicao.
- [ ] Campos invalidos produzem `400` no formato padrao e nao persistem alteracoes parciais.
- [ ] Requisicao sem autenticacao retorna `401`.
- [ ] A operacao identifica e altera exclusivamente o Usuario indicado pelo subject do JWT.
- [ ] Uma consulta posterior pelo endpoint do ticket 02 confirma que nome, sobrenome e timezone foram persistidos e que o e-mail foi preservado.
- [ ] Testes HTTP com PostgreSQL real cobrem edicao valida, normalizacao, validacoes, atomicidade observavel e autenticacao.
- [ ] `.\mvnw.cmd verify -B` e aprovado.

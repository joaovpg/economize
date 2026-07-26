# Roadmap

## 1. Persistencia do nucleo financeiro

- Schema PostgreSQL versionado por Flyway.
- Entidades e repositorios para usuarios, contas, categorias, recorrencias, transacoes e transferencias.
- Testes de migrations, constraints e representabilidade do modelo.

## 2. Autenticacao e CRUD do MVP

- Login por e-mail e senha com Argon2 e token JWT. (implementado)
- Gestao de usuario, contas e categorias.
- Criacao de receitas e despesas planejadas. (implementado)
- Edicao, efetivacao, cancelamento e consulta de transacoes.
- Filtro por qualquer intervalo, padrao de um mes e maximo de doze meses.

## 3. Motor de recorrencia e parcelamentos

- Validacao e expansao do subconjunto aprovado da RFC 5545.
- Materializacao idempotente conforme o periodo consultado.
- Edicoes `ONLY_THIS` e `THIS_AND_FUTURE`.
- Segmentacao, renumeracao e preservacao de ocorrencias efetivadas.

## 4. Orcamentos

- Limites por categoria e periodo.
- Acompanhamento de realizado e previsto.
- Alertas de consumo.

## 5. Cartao de credito

- Cartoes, limites, compras, parcelamentos, faturas e pagamentos.
- Fechamento e vencimento de faturas.

## 6. Relatorios

- Graficos, filtros e comparacoes por periodo.
- Consolidacao por conta e categoria.
- Exportacao de dados.

## 7. Recursos avancados

- Conversao entre moedas.
- Divisao de uma transacao entre categorias.
- Compartilhamento de dados financeiros.

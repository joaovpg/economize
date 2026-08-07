# Logs HTTP sem dados financeiros

**Status:** aceita

A API registra requisições HTTP no console, usando texto legível em desenvolvimento e JSON em produção. Cada evento contém somente método, rota template, status, duração, `traceId` e, quando disponível, o `usuarioId`; query strings, valores de path, corpos, credenciais e dados financeiros não são registrados. Respostas 4xx usam `WARN`, erros 5xx usam `ERROR` com stack trace, e requisições acima do limite configurável usam um `WARN` adicional, preservando a coleta por stdout/stderr para que a plataforma de execução controle armazenamento e retenção.

# Gestao do perfil do Usuario

Status: ready-for-agent

## Problem Statement

O cadastro atual representa o nome do Usuario em um unico campo e nao oferece uma operacao autenticada para consultar ou editar seus proprios dados.

Isso dificulta a personalizacao da interface e impede que o Usuario corrija seu nome, sobrenome ou timezone depois do cadastro. O frontend tambem precisa conservar localmente os dados retornados no cadastro, pois nao consegue recupera-los novamente usando a sessao autenticada.

## Solution

Separar o nome do Usuario em `nome` e `sobrenome`, ambos obrigatorios, e disponibilizar operacoes autenticadas para consultar e editar o proprio perfil.

O cadastro e as respostas de Usuario passarao a apresentar os dois campos. A edicao permitira alterar nome, sobrenome e timezone. O e-mail continuara sendo a identidade de autenticacao e permanecera somente leitura nesta entrega.

A data de nascimento nao sera coletada porque ainda nao possui finalidade no MVP.

## User Stories

1. As a visitante, I want to informar meu nome e sobrenome separadamente durante o cadastro, so that meu perfil seja representado corretamente.
2. As a visitante, I want to receber uma mensagem de validacao quando nao informar meu nome, so that eu possa corrigir o cadastro.
3. As a visitante, I want to receber uma mensagem de validacao quando nao informar meu sobrenome, so that eu possa corrigir o cadastro.
4. As a visitante, I want to ter os espacos externos removidos do meu nome e sobrenome, so that meus dados sejam armazenados de maneira consistente.
5. As a visitante, I want to receber meus dados completos depois do cadastro, so that eu possa iniciar a aplicacao com o perfil atualizado.
6. As a Usuario autenticado, I want to consultar meus dados, so that o frontend possa reconstruir minha sessao sem depender da resposta original do cadastro.
7. As a Usuario autenticado, I want to visualizar meu identificador, nome, sobrenome, e-mail e timezone, so that eu possa conferir os dados associados a minha conta.
8. As a Usuario autenticado, I want to editar meu nome, so that eu possa corrigir ou atualizar a forma como sou identificado.
9. As a Usuario autenticado, I want to editar meu sobrenome, so that eu possa corrigir ou atualizar meu perfil.
10. As a Usuario autenticado, I want to editar meu timezone, so that operacoes dependentes de data civil usem minha localizacao atual.
11. As a Usuario autenticado, I want to receber o perfil atualizado apos uma edicao, so that a interface reflita imediatamente os novos valores.
12. As a Usuario autenticado, I want to manter meu e-mail inalterado durante a edicao do perfil, so that uma troca acidental da minha identidade de login seja evitada.
13. As a Usuario autenticado, I want to receber erros de validacao consistentes para nome, sobrenome ou timezone invalidos, so that eu possa corrigir os campos correspondentes.
14. As a Usuario autenticado, I want to impedir que uma edicao invalida altere parcialmente meu perfil, so that a consistencia dos dados seja preservada.
15. As a Usuario autenticado, I want to acessar somente meu proprio perfil, so that a privacidade entre Usuarios seja preservada.
16. As a visitante nao autenticado, I want to ser impedido de consultar um perfil, so that dados pessoais nao sejam expostos.
17. As a visitante nao autenticado, I want to ser impedido de editar um perfil, so that dados pessoais nao sejam modificados indevidamente.
18. As a cliente da API, I want to ter um contrato estavel e explicito para o perfil, so that eu possa integrar o frontend sem depender de entidades de persistencia.
19. As a pessoa desenvolvedora, I want to evoluir o schema de forma incremental, so that o historico do Flyway continue valido.
20. As a pessoa desenvolvedora, I want to migrar registros criados por versoes anteriores, so that eles nao impecam a aplicacao da migration.
21. As a pessoa desenvolvedora, I want to exigir sobrenome em novos registros, so that o novo contrato nao produza perfis incompletos.
22. As a pessoa mantenedora, I want to preservar o fluxo Resource, mapper, caso de uso e repository, so that a arquitetura do projeto permaneca consistente.
23. As a pessoa mantenedora, I want to validar o comportamento pela API com PostgreSQL real, so that o fluxo seja testado da mesma forma que sera utilizado.
24. As a pessoa mantenedora, I want to validar separadamente a evolucao do schema, so that migrations incompativeis com versoes anteriores sejam detectadas.

## Implementation Decisions

- A funcionalidade permanecera no modulo `usuario`.
- O campo atual `nome` passara a representar o nome do Usuario, e nao mais o nome completo.
- Sera adicionado o campo obrigatorio `sobrenome`.
- Nome e sobrenome aceitarao ate 120 caracteres cada.
- Nome e sobrenome serao obrigatorios, nao poderao conter somente espacos e serao armazenados sem espacos externos.
- O timezone continuara obrigatorio e sujeito a validacao de identificador de zona ja utilizada no cadastro.
- A data de nascimento nao sera adicionada porque nao possui finalidade definida no MVP.
- O contrato de cadastro continuara recebendo nome, e-mail, senha e timezone, passando tambem a exigir sobrenome.
- A resposta do cadastro continuara iniciando uma sessao autenticada e passara a devolver nome e sobrenome separadamente.
- A resposta comum de Usuario contera identificador, nome, sobrenome, e-mail e timezone.
- Sera disponibilizada uma operacao autenticada `GET /api/usuarios/atual`.
- A consulta usara exclusivamente o identificador do Usuario presente no JWT.
- Nao havera identificador de Usuario no caminho ou nos parametros da consulta.
- Sera disponibilizada uma operacao autenticada `PUT /api/usuarios/atual`.
- A edicao usara semantica de substituicao completa para os campos editaveis.
- A edicao recebera obrigatoriamente nome, sobrenome e timezone.
- A edicao nao aceitara e-mail, senha, status ou qualquer identificador de Usuario.
- A edicao devolvera a representacao completa e atualizada do Usuario.
- O e-mail permanecera somente leitura e continuara sendo usado no login.
- Alteracao de e-mail exigira um fluxo proprio no futuro, com decisoes sobre confirmacao e seguranca.
- A consulta e a edicao exigirao o papel autenticado `usuario`.
- Usuario inexistente para o identificador do JWT sera tratado com o erro padronizado de recurso nao encontrado.
- Requisicoes sem autenticacao continuarao usando o comportamento de seguranca existente.
- Serao criados casos de uso orientados as intencoes de consultar o Usuario atual e editar o Usuario atual.
- Resources continuarao responsaveis apenas pelo contrato HTTP, extracao do JWT e traducao das representacoes.
- Casos de uso serao responsaveis pela busca e alteracao do Usuario.
- Entidades JPA nao receberao validacoes ou regras de negocio.
- DTOs HTTP nao dependerao da entidade ou do repository.
- MapStruct sera usado para converter requests em comandos e resultados em responses.
- A edicao sera transacional.
- O controle otimista existente na entidade base continuara protegendo atualizacoes concorrentes.
- O schema sera alterado por uma migration incremental posterior a versao atual.
- A migration inicial compartilhada permanecera imutavel.
- A tabela de Usuario recebera uma coluna fisica de sobrenome com ate 120 caracteres.
- A nova coluna sera obrigatoria e protegida contra valores vazios.
- A coluna fisica atual de nome sera preservada, passando a representar o campo `nome` do novo contrato.
- Para manter a migration aplicavel sobre schemas historicos contendo Usuarios, registros legados receberao temporariamente o valor `Nao informado` no sobrenome.
- Qualquer valor padrao usado para preencher registros legados sera removido ao final da migration.
- Novos Usuarios nunca poderao depender do preenchimento legado e deverao informar sobrenome explicitamente.
- Como nao existem dados reais que precisem ser preservados, nao sera criada logica na aplicacao para interpretar ou dividir nomes completos antigos.
- Fixtures que criam Usuarios diretamente deverao informar sobrenome.
- O contrato anterior, no qual `nome` podia conter o nome completo, nao tera compatibilidade adicional.
- O glossario sera atualizado apenas se necessario para deixar explicita a nova semantica de nome e sobrenome.
- A decisao nao exige uma ADR porque e localizada, reversivel e nao altera a arquitetura ou uma invariante financeira.

## Testing Decisions

- O principal seam de testes sera a API HTTP executada com `@QuarkusTest`, REST Assured e PostgreSQL real iniciado pelo Quarkus Dev Services.
- Os testes verificarao comportamento observavel, sem testar diretamente implementacao de mappers, records, metodos internos ou chamadas ao repository.
- O cadastro sera testado com nome e sobrenome validos.
- O cadastro verificara a remocao de espacos externos.
- O cadastro verificara nome ausente, vazio e acima do limite.
- O cadastro verificara sobrenome ausente, vazio e acima do limite.
- A resposta do cadastro sera verificada quanto a nome e sobrenome separados.
- A consulta autenticada verificara identificador, nome, sobrenome, e-mail e timezone.
- A consulta sem JWT devera retornar `401`.
- A edicao valida verificara nome, sobrenome e timezone atualizados.
- Uma consulta posterior a edicao devera apresentar os valores persistidos.
- A edicao devera retornar o perfil atualizado.
- A edicao verificara a remocao de espacos externos.
- A edicao com nome invalido devera retornar `400`.
- A edicao com sobrenome invalido devera retornar `400`.
- A edicao com timezone invalido devera retornar `400`.
- Uma edicao invalida nao devera persistir alteracoes parciais.
- O contrato de edicao nao aceitara e-mail; propriedades desconhecidas continuarao sendo rejeitadas pela configuracao existente do Jackson.
- O e-mail sera consultado depois da edicao para confirmar que permaneceu inalterado.
- O segundo seam sera um teste de migration Flyway em schema PostgreSQL proprio.
- O teste de migration partira da versao anterior a inclusao do sobrenome.
- O teste inserira um Usuario no schema antigo antes de aplicar as migrations restantes.
- O teste verificara que a migration completa e aplicada com sucesso.
- O teste verificara o preenchimento do sobrenome no registro legado.
- O teste verificara que a coluna final nao aceita `NULL`.
- O teste verificara que novos registros nao recebem silenciosamente um sobrenome padrao.
- Os testes de migrations anteriores deverao continuar migrando ate a versao atual.
- Os testes existentes que persistem Usuarios diretamente serao atualizados para atender ao novo schema.
- O cadastro seguira como prior art para contratos e validacoes de Usuario.
- Os Resources de Conta, Categoria e Transacao servirao como prior art para autenticacao, extracao do JWT e organizacao de casos de uso.
- Os testes atuais de migrations de Conta financeira e Transacao servirao como prior art para schemas temporarios e migracao a partir de versoes anteriores.
- A verificacao final sera executada com `.\mvnw.cmd verify -B`.

## Out of Scope

- Coleta ou armazenamento de data de nascimento.
- Validacao de idade minima.
- Alteracao ou confirmacao de e-mail.
- Alteracao, recuperacao ou redefinicao de senha.
- Avatar ou fotografia de perfil.
- Nome social, nome preferido ou nome legal separado.
- Campos opcionais adicionais de perfil.
- Edicao parcial com `PATCH`.
- Consulta ou edicao de outro Usuario.
- Operacoes administrativas sobre Usuarios.
- Bloqueio, exclusao ou reativacao de Usuario.
- Compatibilidade do cadastro com clientes que ainda enviem somente o antigo nome completo.
- Divisao automatica e confiavel de nomes completos legados.
- Alteracoes em Contas financeiras, Categorias, Transacoes, Transferencias ou Recorrencias.
- Uso de nome ou sobrenome em regras financeiras.
- Uso de timezone alem dos comportamentos ja existentes.

## Further Notes

A implementacao sera dividida em tres tickets lineares: cadastro com nome e sobrenome, consulta do Usuario atual e edicao do Usuario atual. A separacao mantem cada entrega pequena e permite que a migration receba revisao especifica antes do desenvolvimento dos endpoints de perfil.

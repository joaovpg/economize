# Economize

[![CI](https://github.com/joaovpg/economize/actions/workflows/ci.yml/badge.svg)](https://github.com/joaovpg/economize/actions/workflows/ci.yml)

Backend de um sistema pessoal para registrar e catalogar receitas, despesas, transferências e recorrências financeiras.

> [!IMPORTANT]
> O projeto está em desenvolvimento. A entrega atual implementa o núcleo de persistência, autenticação JWT e a criação de transações planejadas.

## Estado atual

O repositório contém:

- schema PostgreSQL versionado com Flyway;
- entidades JPA e repositórios Panache para usuários, contas, categorias, recorrências, transações e transferências;
- constraints e uma trigger diferida para proteger invariantes financeiras;
- testes de integração da persistência com banco real via Quarkus Dev Services;
- login com e-mail e senha, emissão de JWT e criação autenticada de receitas e despesas planejadas;
- infraestrutura REST e OpenAPI configurada, além de dependências planejadas para mapeamentos, integrações e métricas.

O próximo marco continua o CRUD do MVP com gestão de contas e categorias e demais operações de transações. Consulte o [roadmap](docs/roadmap.md) para todas as entregas planejadas.

## Modelo financeiro

Cada registro financeiro pertence a um único usuário. O domínio atual é dividido em:

| Módulo | Responsabilidade |
| --- | --- |
| `usuario` | Identidade e isolamento dos dados |
| `conta` | Contas financeiras e saldo inicial |
| `categoria` | Classificação hierárquica de receitas e despesas |
| `recorrencia` | Grupos e segmentos de recorrência |
| `transacao` | Receitas, despesas e lados de transferências |
| `transferencia` | Operação que vincula uma saída e uma entrada |

As regras, convenções físicas e limitações desta entrega estão em [`docs/modelo-financeiro.md`](docs/modelo-financeiro.md).

## Tecnologias

- Java 25 LTS e Quarkus 3;
- Maven Wrapper;
- PostgreSQL, Hibernate ORM with Panache e Flyway;
- Quarkus REST, Jackson e SmallRye OpenAPI;
- MapStruct, REST Client e Micrometer;
- JUnit 5, Quarkus Test e REST Assured.

## Arquitetura

O Economize é um monólito modular organizado por domínio. Atualmente, os módulos implementam principalmente entidades e repositórios. As próximas funcionalidades devem acrescentar entradas HTTP, casos de uso e regras de domínio dentro do módulo responsável, sem criar pacotes globais por camada.

Cada módulo começa plano e só ganha subpacotes como `http`, `aplicacao`, `dominio` e `persistencia` quando responsabilidades concretas justificarem a divisão. DTOs HTTP permanecem próximos ao adapter, sob `http/dto`, e nunca dependem de entidades JPA ou repositórios.

```text
src/main/java/com/joaovpg/economize/
├── categoria/
├── compartilhado/persistencia/
├── conta/
├── recorrencia/
├── transacao/
├── transferencia/
└── usuario/
```

Consulte [`AGENTS.md`](AGENTS.md) para as regras de arquitetura e implementação.

## Pré-requisitos

- JDK 25 com `JAVA_HOME` configurado;
- Docker ou outro runtime de contêiner compatível, usado pelo Quarkus Dev Services;
- Git.

Não é necessário instalar Maven separadamente: o repositório inclui o Maven Wrapper.

## Desenvolvimento local

O Quarkus Dev Services inicia e configura um PostgreSQL em contêiner quando nenhum datasource é fornecido.

Linux e macOS:

```shell
./mvnw quarkus:dev
```

Windows PowerShell:

```powershell
.\mvnw.cmd quarkus:dev
```

A aplicação fica disponível em <http://localhost:8080>. Durante o desenvolvimento, a Dev UI do Quarkus fica em <http://localhost:8080/q/dev/>.

## Testes

Para executar a suíte de testes:

```shell
./mvnw test
```

No Windows:

```powershell
.\mvnw.cmd test
```

A validação usada pela CI é:

```shell
./mvnw verify -B
```

Os testes de persistência dependem de um runtime de contêiner disponível para iniciar o PostgreSQL.

## Configuração

No desenvolvimento e nos testes, o datasource pode ser fornecido pelo Dev Services. No profile `prod`, configure:

| Variável | Descrição | Exemplo |
| --- | --- | --- |
| `DB_URL` | URL JDBC do PostgreSQL | `jdbc:postgresql://localhost:5432/economize` |
| `DB_USERNAME` | Usuário do banco | `economize` |
| `DB_PASSWORD` | Senha do banco | Não versionar este valor |
| `JWT_CHAVE_PUBLICA` | Localização da chave pública RSA usada para validar tokens | `/run/secrets/jwt-public.pem` |
| `JWT_CHAVE_PRIVADA` | Localização da chave privada RSA usada para assinar tokens | `/run/secrets/jwt-private.pem` |
| `JWT_EXPIRACAO_SEGUNDOS` | Validade do token de acesso | `900` |

O Flyway aplica as migrations na inicialização e o Hibernate apenas valida o schema. O projeto não carrega arquivos `.env` por conta própria; forneça as variáveis pelo ambiente ou pela plataforma de execução.

### Execução local com o profile `prod`

O arquivo `.env.example` contém o contrato de configuração local. Crie o `.env` a partir dele e ajuste `DB_USERNAME` e `DB_PASSWORD` para o PostgreSQL disponível em `localhost`. O `.env` é ignorado pelo Git.

No PowerShell 7, gere uma chave RSA privada PKCS#8 e a chave pública correspondente. O diretório `.certs/` também é ignorado pelo Git:

```powershell
New-Item -ItemType Directory -Path .certs -Force | Out-Null
$rsa = [System.Security.Cryptography.RSA]::Create(2048)
[System.IO.File]::WriteAllText('.certs/jwt-private.pem', $rsa.ExportPkcs8PrivateKeyPem())
[System.IO.File]::WriteAllText('.certs/jwt-public.pem', $rsa.ExportSubjectPublicKeyInfoPem())
$rsa.Dispose()
```

Importe o `.env` somente na sessão atual e inicie a aplicação com o profile `prod`:

```powershell
Get-Content .env |
    Where-Object { $_ -and -not $_.StartsWith('#') } |
    ForEach-Object {
        $nome, $valor = $_.Split('=', 2)
        Set-Item -Path "Env:$nome" -Value $valor
    }
.\mvnw.cmd quarkus:dev -Dquarkus.profile=prod
```

A aplicação ficará disponível em <http://localhost:8080>. Esse fluxo não altera o profile `%test`, que continua usando o Quarkus Dev Services quando necessita de PostgreSQL.

## Build JVM

Linux e macOS:

```shell
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

Windows PowerShell:

```powershell
.\mvnw.cmd package
java -jar target/quarkus-app/quarkus-run.jar
```

## Build nativo

O build nativo é opcional e requer uma distribuição GraalVM/Mandrel compatível. Para produzir o executável usando um contêiner:

```shell
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

Consulte a [documentação de build nativo do Quarkus](https://quarkus.io/guides/building-native-image) para os requisitos adicionais.

## Documentação

- [Modelo financeiro](docs/modelo-financeiro.md)
- [Roadmap](docs/roadmap.md)
- [Instruções para agentes](AGENTS.md)

Este repositório ainda não declara uma licença de uso.

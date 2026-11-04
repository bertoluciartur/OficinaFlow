# OficinaFlow

Sistema de gerenciamento de oficina mecânica, desenvolvido na disciplina de Programação Orientada a Objetos 2
(UTFPR, Campus Cornélio Procópio, Engenharia da Computação). Professora: Gisele Alves Santana.

Autores: Artur Gouvea Bertoluci, Matheus Akira Omori e Pedro Henrique Romano Gedminas.

## Funcionalidades

- Cadastro de clientes (com situação Ativo/Inativo), veículos, mecânicos e peças em estoque.
- Ordens de serviço: abertura com cliente, veículo e mecânicos; itens de quatro tipos (peça, mão de obra, serviço fixo e
  diagnóstico eletrônico); política de desconto; adição de itens depois de aberta; controle de status com transições
  validadas.
- Estoque: baixa automática, devolução ao cancelar a OS e alerta de estoque mínimo.
- Consultas por cliente, veículo, mecânico, status e período; orçamento e nota de fechamento imprimíveis.
- Relatórios: ordens concluídas por período, peças abaixo do mínimo e visão geral por status.

## Tecnologias

Java 17, Spring Boot 3.3 (Spring MVC), Thymeleaf, JDBC (driver `org.postgresql`), PostgreSQL 16, Maven,
JUnit 5, Docker e Docker Compose.

## Como executar (só precisa do Docker)

```
docker compose up --build
```

Abra http://localhost:8080 e entre com o usuário `utfpr` e a senha `12345`.
O login é único e de demonstração; para trocá-lo, defina as variáveis `OFICINA_LOGIN_USUARIO` e `OFICINA_LOGIN_SENHA`.

O build acontece dentro do contêiner (e já roda os testes) e o PostgreSQL cria as tabelas a partir de `sql/schema.sql`.

- Parar: `docker compose down`
- Recriar o banco do zero, apagando os dados: `docker compose down -v`

### Sem Docker

Com JDK 17, Maven e um PostgreSQL com o banco `oficinaflow` criado a partir de `sql/schema.sql`:

```
mvn spring-boot:run
```

Conexão padrão: `localhost:5432/oficinaflow`, usuário e senha `postgres`. Para mudar, use as variáveis
`OFICINA_DB_URL`, `OFICINA_DB_USER` e `OFICINA_DB_PASS`.

## Testes

```
mvn test
```

São 9 testes JUnit 5 (cálculo de valores e descontos, transições de status, regras de estoque, validação de cliente
inativo e login). Eles não precisam de banco e também rodam durante o `docker compose up --build`.

## Estrutura (MVC + DAO)

- `model`: entidades e regras de negócio (OS, itens cobráveis, políticas de desconto, status).
- `dao`: acesso a dados com JDBC (`@Repository`, `DataSource` injetado pelo Spring).
- `service`: validação, estoque, regras de emissão e orquestração (`@Service`).
- `web` e `resources/templates`: controllers Spring MVC e telas Thymeleaf.
- `sql/schema.sql`: script de criação do banco, executado na criação do contêiner.

## Boas práticas

- **OCP**: novo tipo de item = nova classe que implementa `ItemCobravel` + 1 linha em `ItemFactory`. Nova política de
  desconto = nova classe que implementa `PoliticaDesconto` + 1 linha em `PoliticasDesconto`. `OrdemServico` não muda.
- **SRP**: `ValidadorOrdemServico`, `EstoqueService`, `EmissorDocumentoOrdemServico`, `OrdemServicoService` e
  `OrdemServicoDAO` têm uma responsabilidade cada.

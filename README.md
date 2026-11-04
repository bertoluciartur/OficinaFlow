# OficinaFlow

Gerenciamento de oficina mecânica: Java 17, Spring Boot 3 (MVC + Thymeleaf), JDBC, PostgreSQL, Docker.

## Executar (só precisa do Docker)

```
docker compose up --build
```

Abra http://localhost:8080 e entre com o usuário `utfpr` e a senha `12345` (login único de demonstração; para trocar, use as variáveis `OFICINA_LOGIN_USUARIO` e `OFICINA_LOGIN_SENHA`). O build roda no container (inclui os testes unitários) e o PostgreSQL
cria as tabelas a partir de `sql/schema.sql`. Para recriar o banco do zero: `docker compose down -v`.

Banco já criado antes da coluna `cliente.ativo` e dos índices (preserva os dados):

```
docker compose exec db psql -U postgres -d oficinaflow -c "ALTER TABLE cliente ADD COLUMN IF NOT EXISTS ativo BOOLEAN NOT NULL DEFAULT TRUE; CREATE INDEX IF NOT EXISTS idx_veiculo_cliente ON veiculo(cliente_id); CREATE INDEX IF NOT EXISTS idx_os_cliente ON ordem_servico(cliente_id); CREATE INDEX IF NOT EXISTS idx_os_veiculo ON ordem_servico(veiculo_id); CREATE INDEX IF NOT EXISTS idx_os_status ON ordem_servico(status); CREATE INDEX IF NOT EXISTS idx_os_abertura ON ordem_servico(data_abertura); CREATE INDEX IF NOT EXISTS idx_os_item_os ON os_item(os_id); CREATE INDEX IF NOT EXISTS idx_os_mecanico_mec ON os_mecanico(mecanico_id);"
```

Para rodar fora do Docker (JDK 17 + Maven + PostgreSQL): `mvn spring-boot:run`.
Banco padrão `localhost:5432/oficinaflow`, usuário e senha `postgres`; altere com
`OFICINA_DB_URL`, `OFICINA_DB_USER` e `OFICINA_DB_PASS`.

## Estrutura (MVC + DAO)

- `model`: entidades e regras (OS, itens, políticas de desconto, status).
- `dao`: acesso JDBC (`@Repository`, `DataSource` injetado).
- `service`: validação, estoque, documentos e orquestração (`@Service`).
- `web` + `resources/templates`: controllers Spring MVC e telas Thymeleaf.

## Boas práticas

- **OCP**: novo item cobrável = nova classe de `ItemCobravel` + 1 linha em `ItemFactory`. Nova política = nova classe de `PoliticaDesconto` + 1 linha em `PoliticasDesconto`. `OrdemServico` não muda.
- **SRP**: `ValidadorOrdemServico`, `EstoqueService`, `EmissorDocumentoOrdemServico`, `OrdemServicoDAO` e `OrdemServicoService` têm uma responsabilidade cada.

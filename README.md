# Vinheria Agnello

Projeto acadêmico da Fase 4 com duas frentes complementares:

- `Android`: aplicativo mobile para gerenciamento local do estoque com Kotlin, Jetpack Compose e Room.
- `Backend`: API CRUD em ASP.NET Core com Entity Framework Core e SQLite.

O domínio do trabalho é o controle de estoque da Vinheria Agnello. Em ambos os módulos, o sistema registra vinhos, mantém quantidade disponível e permite operações completas de cadastro, consulta, edição e exclusão.

## Objetivo do projeto

O sistema foi construído para demonstrar, na prática:

- persistência local no Android usando Room sobre SQLite;
- arquitetura em camadas no app mobile com `UI -> ViewModel -> Repository -> DAO -> Room`;
- criação de API REST com C# e ASP.NET Core;
- persistência relacional no backend usando Entity Framework Core com SQLite;
- aplicação de regras básicas de integridade para dados de estoque.

## Estrutura do repositório

```text
VinheriaAgnello
|- Android/
|  |- app/
|  |- docs/
|  `- README.md
`- Backend/
   |- Controllers/
   |- DTOs/
   |- Data/
   |- Mappings/
   |- Migrations/
   |- Services/
   `- Program.cs
```

## Como o sistema gerencia os dados de estoque

Embora sejam entregas separadas, os dois módulos seguem a mesma ideia central: o estoque é tratado como uma coleção de vinhos persistidos em banco, com validações mínimas para evitar inconsistências.

### No Android

Cada item de estoque é representado por `WineEntity`, com os campos:

- `id`
- `name`
- `country`
- `region`
- `grape`
- `type`
- `stockQuantity`

O fluxo de dados é o seguinte:

1. o usuário interage com as telas Compose;
2. a `StockViewModel` valida os campos do formulário;
3. o `WineRepository` aplica regras de domínio simples;
4. o `WineDao` executa o CRUD no Room;
5. o Room persiste os dados no arquivo SQLite local `vinheria_stock.db`.

Pontos importantes:

- a listagem do estoque é reativa via `Flow<List<WineEntity>>`;
- qualquer inclusão, edição ou exclusão atualiza a interface automaticamente;
- o nome do vinho não pode ficar em branco;
- a quantidade em estoque não pode ser negativa;
- update e delete verificam se exatamente um registro foi afetado;
- há consulta preparada para busca por nome e para identificação de baixo estoque;
- o banco exporta schema em `Android/app/schemas/`, útil para acompanhar mudanças estruturais;
- a base Room está configurada com `fallbackToDestructiveMigration`, então alterações incompatíveis de schema recriam a base local nesta versão acadêmica.

### No backend

Cada item é representado pela entidade `Wine`, com os campos:

- `Id`
- `Name`
- `Winery`
- `Type`
- `Country`
- `Year`
- `Quantity`
- `Price`
- `CreatedAt`

O fluxo é:

1. a requisição chega ao `WinesController`;
2. os DTOs recebem e validam a entrada;
3. o `WineService` executa a regra de negócio do CRUD;
4. o `AppDbContext` persiste os dados via EF Core;
5. o SQLite grava tudo no arquivo `vinheria.db`.

Pontos importantes:

- o backend executa `Database.Migrate()` na inicialização;
- se a base ainda não existir, ela é criada automaticamente com as migrations;
- a API já sobe com dados iniciais de exemplo no estoque;
- os endpoints retornam um envelope padronizado em `ApiResponse<T>`;
- validações impedem quantidade negativa, preço negativo e campos obrigatórios vazios;
- a listagem é retornada ordenada por nome.

## Tecnologias utilizadas

### Android

- Kotlin
- Jetpack Compose
- Android ViewModel
- Kotlin Coroutines e Flow
- Room
- SQLite
- KSP

### Backend

- C#
- ASP.NET Core Web API
- Entity Framework Core
- SQLite
- Swagger / OpenAPI

## Requisitos para execução

### Android

- Android Studio
- JDK 11 ou compatível com o projeto Android
- Android SDK 36.1
- emulador ou dispositivo com API 26+

### Backend

- .NET SDK 10.0

## Como executar

## 1. Executando o app Android

No diretório `Android`, é possível abrir o projeto no Android Studio ou usar o Gradle Wrapper.

### Opção A: Android Studio

1. abra a pasta `Android` no Android Studio;
2. aguarde o sync do Gradle;
3. selecione um emulador ou dispositivo;
4. execute o app.

### Opção B: terminal

```powershell
cd Android
.\gradlew.bat :app:assembleDebug
```

Para análise estática:

```powershell
cd Android
.\gradlew.bat :app:lintDebug
```

Saída esperada do APK:

```text
Android/app/build/outputs/apk/debug/app-debug.apk
```

## 2. Executando o backend

No diretório `Backend`:

```powershell
cd Backend
dotnet restore
dotnet run
```

Ao iniciar em ambiente de desenvolvimento, a API expõe Swagger.

URLs configuradas no projeto:

- `http://localhost:5008`
- `https://localhost:7138`
- Swagger: `https://localhost:7138/swagger` ou `http://localhost:5008/swagger`

O banco SQLite é criado automaticamente com o nome:

```text
Backend/vinheria.db
```

## Endpoints principais da API

Base:

```text
/api/wines
```

Operações disponíveis:

- `GET /api/wines` lista todos os vinhos
- `GET /api/wines/{id}` busca um vinho por ID
- `POST /api/wines` cadastra um vinho
- `PUT /api/wines/{id}` atualiza um vinho existente
- `DELETE /api/wines/{id}` remove um vinho

### Exemplo de payload para criação

```json
{
  "name": "Cabernet Reserva",
  "winery": "Vinheria Agnello",
  "type": "Tinto",
  "country": "Chile",
  "year": 2022,
  "quantity": 15,
  "price": 79.90
}
```

### Exemplo de resposta

```json
{
  "success": true,
  "message": "Vinho cadastrado com sucesso.",
  "data": {
    "id": "00000000-0000-0000-0000-000000000000",
    "name": "Cabernet Reserva",
    "winery": "Vinheria Agnello",
    "type": "Tinto",
    "country": "Chile",
    "year": 2022,
    "quantity": 15,
    "price": 79.90,
    "createdAt": "2026-06-21T00:00:00Z"
  },
  "errors": []
}
```

## Regras de estoque observadas no projeto

- o estoque é persistido localmente nos dois módulos usando SQLite;
- cada vinho possui um identificador próprio, evitando ambiguidades nas operações;
- a quantidade nunca deve ser negativa;
- o cadastro exige informações essenciais para identificação do item;
- a atualização substitui os dados atuais do produto persistido;
- a exclusão remove o item do estoque e atualiza a visualização;
- no Android, a UI reage automaticamente às mudanças do banco;
- no backend, a API centraliza validação e persistência em camadas separadas.

## Diferencial técnico relevante para avaliação

- o Android não grava dados apenas em memória: o estoque permanece salvo no dispositivo com Room;
- o app usa `Flow`, então a lista de produtos reflete o estado do banco em tempo real;
- o backend aplica migration automaticamente ao subir;
- o backend já entrega dados seed para facilitar teste e demonstração;
- os dois módulos mostram domínio coerente: estoque de vinhos com CRUD completo.

## Arquivos relevantes para análise

### Android

- `Android/app/src/main/java/br/com/fiap/vinheriaagnello/data/local/WineEntity.kt`
- `Android/app/src/main/java/br/com/fiap/vinheriaagnello/data/local/WineDao.kt`
- `Android/app/src/main/java/br/com/fiap/vinheriaagnello/data/local/VinheriaDatabase.kt`
- `Android/app/src/main/java/br/com/fiap/vinheriaagnello/data/repository/WineRepository.kt`
- `Android/app/src/main/java/br/com/fiap/vinheriaagnello/ui/stock/StockViewModel.kt`
- `Android/app/src/main/java/br/com/fiap/vinheriaagnello/ui/stock/StockScreen.kt`
- `Android/docs/`

### Backend

- `Backend/Program.cs`
- `Backend/Controllers/WinesController.cs`
- `Backend/Services/WineService.cs`
- `Backend/Data/AppDbContext.cs`
- `Backend/Data/Models/Wine.cs`
- `Backend/DTOs/Wines/`
- `Backend/Migrations/`

## Observações finais

Este repositório reúne duas abordagens de persistência aplicadas ao mesmo contexto de negócio da Vinheria Agnello:

- no mobile, a persistência é local ao dispositivo, adequada para uso offline e controle simples de estoque;
- no backend, a API oferece uma base para centralizar o cadastro de vinhos em uma aplicação servidora.

Para análise acadêmica, o ponto principal é que o projeto demonstra o ciclo completo de armazenamento, recuperação, atualização e remoção de dados de estoque, com separação de responsabilidades e uso real de banco de dados em ambas as implementações.

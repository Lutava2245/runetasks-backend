# 🎮 Runetasks API

![Java 21](https://img.shields.io/badge/Java-21-blue)
![Spring Boot 3.5.11](https://img.shields.io/badge/Spring%20Boot-3.5.11-brightgreen)
![MySQL](https://img.shields.io/badge/Database-MySQL-orange)

---

## 📑 Tabela de Conteúdos

1. [Visão Geral](#-visão-geral)
2. [Tecnologias e Ferramentas](#-tecnologias-e-ferramentas)
3. [Arquitetura e Padrões](#-arquitetura-e-padrões)
4. [Estrutura de Diretórios](#-estrutura-de-diretórios)
5. [Decisões de Design e Negócio (ADRs)](#-decisões-de-design-e-negócio-adrs)
6. [Segurança e Autenticação](#-segurança-e-autenticação)
7. [Guia de Início Rápido](#-guia-de-início-rápido)
8. [Testes](#-testes)
9. [Documentação da API](#-documentação-da-api)
10. [Modelagem do Banco de Dados](#-modelagem-do-banco-de-dados)

---

## 📋 Visão Geral

O **Runetasks API** é um sistema backend gamificado focado no gerenciamento de tarefas diárias. O propósito central do projeto é **conscientizar e engajar as pessoas a organizarem melhor seus cronogramas e rotinas**, transformando a produtividade em um processo motivador e divertido. 

A plataforma permite que os usuários gerenciem suas obrigações, desenvolvam "Habilidades" (Skills) e ganhem pontos de experiência (XP) e moedas virtuais ao concluírem seus objetivos. Com esses recursos, os usuários podem subir de nível, comprar Avatares personalizados na loja e resgatar Recompensas criadas por eles mesmos.

---

## 🚀 Tecnologias e Ferramentas

O projeto foi desenvolvido utilizando **Java 21** e **Spring Boot 3.5.11**. Abaixo estão as principais bibliotecas e as justificativas para suas escolhas:

* **Spring Data JPA + MySQL:** Escolhidos para persistência robusta de dados relacionais e fácil integração com o ecossistema Spring.
* **Flyway:** Utilizado para gerenciar as migrações do banco de dados de forma confiável, garantindo que o esquema seja criado e populado consistentemente.
* **Spring Security + JJWT:** Implementação de autenticação *stateless* via JSON Web Tokens (JWT), garantindo o acesso seguro aos endpoints.
* **Lombok:** Reduz o código repetitivo (Getters, Setters, Construtores), mantendo os modelos de domínio e DTOs limpos.
* **Springdoc OpenAPI (Swagger):** Utilizado para documentação interativa e testes manuais da API.
* **JUnit 5 + Mockito:** Pilha de testes utilizada para garantir que a lógica de negócios funcione corretamente e de forma isolada.

---

## 🏛️ Arquitetura e Padrões

O projeto segue a **Arquitetura em Camadas** (Layered Architecture), separando as responsabilidades de forma clara:

1. **Domain (Core):** Contém as Entidades (`User`, `Task`, `Skill`, `Reward`, `Avatar`, `Role`) e Enums que representam o núcleo dos dados.
2. **Repositories:** Interfaces que estendem `JpaRepository` para o acesso aos dados.
3. **Services:** Contém a lógica de negócio, orquestração e cálculos de progressão.
4. **Controllers (API):** Pontos de entrada REST que recebem as requisições, validam os DTOs e repassam para os serviços.

### Padrões Utilizados
* **DTO (Data Transfer Object):** Separação estrita entre os Modelos do banco de dados e os contratos da API (Requests/Responses), garantindo a proteção de dados sensíveis.
* **Arquitetura Orientada a Eventos:** Uso de eventos do Spring (ex: `UserBalanceChangedEvent`) para desacoplar ações financeiras da lógica de liberação de recompensas.
* **Injeção de Dependências:** Adoção rigorosa do container nativo do Spring via construtores (e a anotação `@RequiredArgsConstructor` do Lombok). Essa é uma prática altamente recomendada no ecossistema Spring para manter instâncias únicas (Singletons), **evitando a recriação desnecessária de objetos** toda vez que a classe é acionada, além de garantir o baixo acoplamento e facilitar o isolamento em testes unitários.
* **Exceções Customizadas de Domínio:** O projeto implementa diversas exceções próprias (como `InsufficientCoinsException`, `LockedTaskException` e `WeakPasswordException`). Isso permite capturar violações exatas das regras de negócio de forma semântica, evitando o uso de exceções genéricas (ex: `RuntimeException`). 
* **Tratamento Global de Exceções:** Aliado às exceções customizadas, usamos o padrão `@ControllerAdvice` (`GlobalExceptionHandler`) que centraliza a captura desses erros de domínio e os converte em respostas HTTP padronizadas e amigáveis para o Frontend.

---

## 📂 Estrutura de Diretórios

```text
runetasks-backend/
├── src/main/java/com/fatec/runetasks/
│   ├── config/          # Configurações de Segurança, OpenAPI e DataLoader
│   ├── controller/      # Controladores REST da API
│   ├── domain/
│   │   ├── dto/         # DTOs de Request e Response
│   │   ├── model/       # Entidades JPA e Enums (TaskDifficulty, RepeatType, etc.)
│   │   └── repository/  # Repositórios do Spring Data JPA
│   ├── event/           # Eventos da Aplicação (ex: UserBalanceChangedEvent)
│   ├── exception/       # Exceções Customizadas de Domínio
│   ├── handler/         # Tratamento Global de Exceções (GlobalExceptionHandler)
│   ├── service/         # Interfaces e Implementações da Regra de Negócio
│   └── util/            # Utilitários e Filtros do JWT
├── src/main/resources/
│   ├── db/migration/    # Scripts SQL do Flyway (V1__create, V2__insert)
│   └── application.properties # Configurações da Aplicação e Banco
└── src/test/            # Testes Unitários utilizando Mockito
```

--- 

## 🧠 Decisões de Design e Negócio (ADRs)

### 1. Motor de Gamificação (XP e Moedas)
* **O Problema:** Como determinar o valor das tarefas e a progressão dos usuários de maneira justa e engajadora?
* **A Decisão:** A dificuldade da tarefa (`TaskDifficulty` - EASY, MEDIUM, HARD) determina o valor das recompensas base.
  * **Tarefas:** Dão diferentes quantias de XP (20, 40, 60) e Moedas (5, 15, 25) quando concluídas com sucesso.
  * **Fórmula de Nível:** O nível geral do usuário sobe automaticamente quando o progresso de XP alcança a fórmula `30 + (90 * level)`. Para o avanço individual de cada Habilidade (Skill), a fórmula matemática utilizada é `20 + (10 * level)`. Isso garante uma curva de dificuldade progressiva, onde níveis mais altos exigem mais dedicação.

### 2. Eventos de Economia e Recompensas
* **O Problema:** Quando o saldo de moedas do usuário se altera (ganhando moedas ao completar tarefas ou gastando ao comprar avatares), a loja deve refletir instantaneamente a possibilidade de comprar recompensas ou bloqueá-las (marcando como muito caras).
* **A Decisão:** Implementamos o padrão arquitetural de Arquitetura Orientada a Eventos (Event-Driven). O serviço `RewardServiceImpl` escuta ativamente o evento `UserBalanceChangedEvent` via anotação `@EventListener`. Dessa forma, ele atualiza de forma completamente assíncrona o status das recompensas para `AVAILABLE` (Disponível) ou `EXPENSIVE` (Cara) de acordo com o novo saldo do usuário, mantendo os serviços desacoplados e performáticos.

### 3. Gerenciamento de Tarefas Recorrentes
* **O Problema:** Como permitir que os usuários não precisem recadastrar manualmente suas tarefas de rotina diárias, semanais ou mensais todos os dias?
* **A Decisão:** Criou-se um Cron Job utilizando o agendador nativo do Spring (`@Scheduled(cron = "0 0 0 * * *")`) dentro da classe `TaskServiceImpl`. Todos os dias, exatamente à meia-noite, a aplicação varre o banco de dados buscando tarefas com o atributo `RepeatType` ativo que já estejam com o status `COMPLETED`. O sistema então as recria, resetando o status para `PENDING` e avançando matematicamente a data de vencimento para a sua próxima ocorrência, garantindo a continuidade do cronograma do usuário sem intervenção manual.

### 4. Precificação Fixa de Recompensas
* **O Problema:** Como os usuários têm a liberdade de criar suas próprias recompensas customizadas (ex: "Assistir a um filme", "Comer um doce"), eles poderiam burlar o sistema criando itens extremamente valiosos custando apenas 1 moeda, o que destruiria o balanceamento da economia do jogo.
* **A Decisão:** Os usuários não informam e não decidem o valor financeiro direto da recompensa. Em vez disso, eles escolhem um "nível de desejo" (`likeLevel` de 1 a 5). O sistema interpreta esse nível e determina automaticamente um preço tabelado variando de 30 a 150 moedas. Essa abstração mantém o controle do balanço econômico nas mãos do backend.

---

## 🔒 Segurança e Autenticação

A API é estritamente protegida através do padrão **JWT (JSON Web Token)**.

1. **Senhas Criptografadas:** O sistema utiliza o algoritmo `BCryptPasswordEncoder` para aplicar o hash (encriptar) as credenciais antes da persistência no banco. Além disso, aplicamos regras rígidas de criação de senha: é exigido um mínimo de 8 caracteres, presença de letras maiúsculas, minúsculas e números. Tentativas que não cumpram esses requisitos são imediatamente barradas pela exceção customizada `WeakPasswordException`.
2. **Controle de Acesso por Papéis (RBAC - Role-Based Access Control):** Os endpoints da API exigem níveis de permissões específicos, controlados pela anotação `@PreAuthorize`.
   * Perfis administrativos (`ADMIN`) possuem visão global do sistema e acesso a rotas sensíveis de deleção.
   * Para os usuários padrão (`USER`), implementamos segurança em nível de objeto (Object-Level Security). Expressões SpEL (Spring Expression Language), como `@taskServiceImpl.isOwner(#id, principal.id)`, são ativamente validadas em tempo de execução para garantir que os usuários comuns só possam visualizar, editar e concluir estritamente as suas próprias tarefas, habilidades e itens, prevenindo ataques de acesso direto a objetos (IDOR).

---

## ⚡ Guia de Início Rápido

Siga detalhadamente os passos abaixo para configurar, compilar e executar a API localmente em sua máquina de desenvolvimento.

### 🛠️ Requisitos Pré-estabelecidos

Certifique-se de ter as seguintes ferramentas instaladas no seu ambiente:
* **Java 21** (recomenda-se fortemente a utilização da distribuição Temurin ou OpenJDK)
* **Maven 3.9+** (para o gerenciamento completo das dependências)
* **MySQL 8.0+** (como mecanismo de persistência de dados)

### 🚀 Configuração e Execução (Passo a Passo)

1.  **Clone o projeto do GitHub:**
    Abra o terminal de sua preferência e execute a clonagem do repositório remoto:
    ```bash
    git clone [https://github.com/Lutava2245/runetasks-backend.git](https://github.com/Lutava2245/runetasks-backend.git)
    cd runetasks-backend
    ```

2.  **Configure o seu Banco de Dados (MySQL):**
    Acesse seu cliente MySQL (via CLI ou ferramenta visual como MySQL Workbench) e execute os seguintes comandos DDL e DCL para provisionar a base e o usuário da aplicação:
    ```sql
    CREATE DATABASE runetasksDB;
    CREATE USER 'runetasksUSER'@'localhost' IDENTIFIED BY 'RuneTasks-2026';
    GRANT ALL PRIVILEGES ON runetasksDB.* TO 'runetasksUSER'@'localhost';
    FLUSH PRIVILEGES;
    ```

3.  **Defina variáveis de ambiente (Opcional, porém recomendado):**
    O projeto foi arquitetado utilizando *placeholders* no arquivo `application.properties` (exemplo: `jwt.secret=${JWT_SECRET:sua-chave-padrao}`). Portanto, definir essas variáveis de ambiente no seu terminal é opcional para conseguir rodar o projeto localmente da primeira vez, mas se torna fundamental para uma implantação em produção.
    
    *No Linux / macOS:*
    ```bash
    export DB_USER=runetasksUSER
    export DB_PASSWORD=RuneTasks-2026
    export JWT_SECRET=uma_chave_secreta_muito_longa_e_segura_para_geracao_dos_tokens
    ```
    *No Windows (CMD/PowerShell):* Utilize o comando `set` em vez de `export`.

4.  **Rode o projeto (Perfil Padrão):**
    Inicie a aplicação utilizando o plugin do Spring Boot pelo Maven:
    ```bash
    mvn spring-boot:run
    ```

### 🧑‍💻 Rodando no Modo Desenvolvimento (Profile `dev`)

Para evitar poluir as variáveis de ambiente globais do seu sistema operacional e ter maior controle local, você pode executar o projeto isoladamente utilizando o perfil (profile) de desenvolvimento oficial do Spring Boot:

1.  **Crie o arquivo de propriedades local:**
    Dentro do diretório `src/main/resources/`, crie um novo arquivo nomeado especificamente como `application-dev.properties` e adicione as suas credenciais locais de conexão:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/runetasksDB
    spring.datasource.username=seu_usuario
    spring.datasource.password=sua_senha
    jwt.secret=sua_chave_secreta_local_criptografada
    ```

2.  **Inicie o projeto ativando o profile `dev`:**
    ```bash
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
    ```

Assim que a aplicação sinalizar a inicialização bem-sucedida, a documentação OpenAPI/Swagger interativa da API estará plenamente disponível em:
👉 **http://localhost:8080/swagger-ui.html**

---

## 🧪 Testes

O projeto consolida uma robusta suíte de testes unitários desenvolvida meticulosamente para assegurar a integridade e o comportamento determinístico da lógica de domínio nos serviços de negócio.

### Como Executar a Suíte

No diretório raiz do projeto, execute o comando nativo do Maven:
```bash
mvn test
```

### Cobertura e Escopo

* **Testes de Serviço Isoldados (`JUnit 5` + `Mockito`):** Focados intensamente na validação da camada de *Services* (exemplos: `TaskServiceTest`, `RewardServiceTest`, `UserServiceTest`, `StoreServiceTest`). O banco de dados não é acionado (usando *Mocks*), garantindo execução extremamente rápida e isolamento absoluto de infraestrutura.
* **Cenários Validados:** A bateria valida desde os fluxos críticos de avanço temporal — como recalcular o vencimento de tarefas recorrentes em atraso ou bloquear alterações indevidas —, até proibições econômicas da loja capturadas de forma elegante via exceções customizadas (`InsufficientCoinsException`), finalizando com checagens estritas das métricas de complexidade de senha na etapa de registro por meio da intercepção da `WeakPasswordException`.

---

## 📖 Documentação da API

A documentação completa, compreendendo os DTOs (Data Transfer Objects), esquemas de autenticação, parâmetros exigidos e todos os contratos REST da API está documentada dinamicamente via **Swagger UI** (`/swagger-ui.html`). Abaixo, listamos uma visão macroclássica dos recursos e responsabilidades disponíveis:

### 🔐 Autenticação e Usuários (`/api/users` & `/api/auth`)
* `POST /api/auth/login`: Ponto de entrada de autenticação. Avalia as credenciais em base de dados, checa os papéis e retorna o token JWT assinado para a navegação segura do cliente.
* `POST /api/users/register`: Ponto de entrada público destinado ao cadastramento ágil e seguro de novas contas na plataforma.
* `GET /api/users/profile`: Recupera os detalhes operacionais e as métricas de progresso do usuário logado (XP total, nível atual, contagem de moedas e perfil do avatar).
* `PATCH /api/users/avatar/{avatarName}`: Equipa um avatar cosmético, validando rigorosamente e de forma prévia se o usuário efetivamente comprou o item na loja de avatares.

### 📋 Tarefas (`/api/tasks`)
* `POST /api/tasks/register`: Realiza a vinculação de uma nova meta de rotina no escopo do usuário, atrelada obrigatoriamente a uma Skill ativa do catálogo pessoal.
* `PATCH /api/tasks/{id}/complete`: Confirma o sucesso e execução da tarefa. É a rota mais complexa do domínio operacional, disparando o cálculo e transferência fracionada de pontos XP (tanto para o Perfil global quanto para a Habilidade específica) e efetuando o rendimento matemático das devidas moedas de troca.
* `PATCH /api/tasks/{id}/block`: Modifica o estado do registro para congelado (Locked), proibindo permanentemente de maneira estrita edições ou deleções posteriores nesta tarefa.

### 🧠 Habilidades / Skills (`/api/skills`)
* `POST /api/skills/register`: Dá início a um novo caminho (path) de foco a longo prazo (ex: "Aprender Programação", "Treinar Inglês").
* `GET /api/skills/user/{id}`: Enumera o arsenal de Habilidades do indivíduo, despachando em conjunto de maneira formatada as porcentagens de alcance e o nível atual focado e evoluído em cada uma delas.

### 🏆 Recompensas e Loja / Store (`/api/rewards` & `/api/store`)
* `POST /api/rewards/register`: Ferramenta para formulação de Recompensas inteiramente customizáveis elaboradas de acordo com a preferência ("likeLevel") delimitada e sinalizada pelo próprio jogador (cujo custo é devidamente e autonomamente auto-calculado pelas lógicas de negócio do backend).
* `GET /api/store/avatars`: Retorna todo o catálogo fixo e global da plataforma contendo os ícones adquiríveis e os seus respectivos valores balizados de inflação de moedas do jogo.
* `PATCH /api/store/buy/avatar/{avatarId}`: Processo transacional seguro de aquisição em loja. Avalia de forma precisa o saldo da conta, efetua a dedução (débito) matemática do capital digital do jogador e grava imediatamente o vínculo de compra, garantindo de fato o registro histórico inalterável do item na carteira da conta vinculada.
* `PATCH /api/store/buy/reward/{rewardId}`: Trata-se do resgate físico e concreto da recompensa projetada outrora, baixando formalmente a flag relacional do banco de dados de status de *AVAILABLE* para o status permanente e irrevogável de *REDEEMED*.

---

# 🗄 Modelagem do Banco de Dados

O esquema relacional subjacente do banco de dados MySQL é administrado, versionado de forma coesa e populado em tempo de inicialização através da poderosa ferramenta automatizada de migrações **Flyway** (`V1__create_tables.sql` para formatação da estrutura em tabelas e referências; e `V2__insert_essentials.sql` para injeção mandatória de entidades vitais pré-requisito, como os Avatares nativos da loja e as hierarquias de Permissões de Usuário). Abaixo, temos um resumo estrutural e focado do modelo lógico conceitual implementado no mecanismo corporativo:

```sql
-- Estrutura Administrativa e de Catálogo Base
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, 
    name VARCHAR(255) UNIQUE
);

CREATE TABLE avatars (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, 
    title VARCHAR(50) UNIQUE, 
    icon_name VARCHAR(255) UNIQUE, 
    price INT
);

-- Estrutura Central (Jogadores/Usuários)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    avatar_id BIGINT,
    total_xp INT DEFAULT 0,
    total_coins INT DEFAULT 0,
    level INT DEFAULT 1,
    progress_xp INT DEFAULT 0,
    created_at DATE NOT NULL
);

-- Tabelas de Associação (Relacionamentos Muitos-para-Muitos)
CREATE TABLE user_avatars (
    user_id BIGINT, 
    avatar_id BIGINT
); 
CREATE TABLE user_roles (
    user_id BIGINT, 
    role_id BIGINT
);

-- Módulo de Progresso Gamificado (Habilidades)
CREATE TABLE skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    icon_name VARCHAR(255) NOT NULL,
    total_xp INT DEFAULT 0,
    level INT DEFAULT 1,
    progress_xp INT DEFAULT 0,
    user_id BIGINT NOT NULL
);

-- Módulo Operacional e Rotineiro (Tarefas e Obrigações)
CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    date DATE NOT NULL,
    repeat_type VARCHAR(20),
    user_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL
);

-- Módulo Motivacional de Prêmios Customizados
CREATE TABLE rewards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    price INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    user_id BIGINT NOT NULL
);

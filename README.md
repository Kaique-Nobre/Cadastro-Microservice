# Cadastro Microservice

Projeto educacional desenvolvido para praticar conceitos de **arquitetura de microsserviços**, **comunicação assíncrona com RabbitMQ** e **processamento de eventos**.

A aplicação é composta por dois microsserviços: um responsável pelo cadastro dos usuários e outro responsável pelo processamento do evento de cadastro e envio de notificações por e-mail.

O projeto possui uma arquitetura propositalmente simples, tendo como principal objetivo o aprendizado e a prática de conceitos relacionados a microsserviços e mensageria.

---

## Sobre o Projeto

O fluxo principal da aplicação funciona da seguinte maneira:

1. O cliente envia uma requisição para o `User-service`.
2. O usuário é persistido no PostgreSQL.
3. O `User-service` publica um evento no RabbitMQ.
4. O `NotificationService` consome o evento.
5. O `NotificationService` envia um e-mail utilizando o MailHog.
6. Caso ocorra uma exceção durante o processamento da mensagem, são utilizados mecanismos de **Retry**, **Dead Letter Exchange (DLX)** e **Dead Letter Queue (DLQ)**.

O projeto foi desenvolvido como um ambiente de estudo para compreender, na prática, como serviços independentes podem se comunicar por meio de eventos e como lidar com falhas no processamento de mensagens.

---

## Arquitetura

A aplicação possui dois microsserviços:

### User-service

Responsável pelo cadastro dos usuários.

Principais responsabilidades:

* Receber requisições de cadastro;
* Validar os dados recebidos;
* Persistir os usuários no PostgreSQL;
* Publicar o evento de cadastro no RabbitMQ.

Endpoint disponível:

```http
POST /users
```

O cadastro recebe:

```json
{
  "name": "João Silva",
  "email": "joao@example.com"
}
```

Após o cadastro, o serviço publica o evento no exchange:

```text
user.events
```

### NotificationService

Responsável por consumir os eventos de cadastro e enviar notificações por e-mail.

Principais responsabilidades:

* Consumir o evento de cadastro;
* Processar a mensagem recebida;
* Enviar o e-mail através do MailHog;
* Realizar novas tentativas em caso de falha;
* Encaminhar mensagens para a DLQ quando o processamento não for concluído.

A fila principal utilizada pelo serviço é:

```text
notification.user.registered
```

Em caso de falhas que não possam ser resolvidas através das tentativas de processamento, a mensagem é encaminhada para a Dead Letter Queue.

---

## Fluxo de Mensageria

O fluxo de comunicação entre os serviços utiliza RabbitMQ:

```text
                   ┌─────────────────┐
                   │   User-service  │
                   └────────┬────────┘
                            │
                            │ Publica evento
                            ▼
                    ┌───────────────┐
                    │  user.events  │
                    └───────┬───────┘
                            │
                            ▼
              ┌───────────────────────────┐
              │ notification.user.registered │
              └────────────┬──────────────┘
                           │
                           ▼
                 ┌────────────────────┐
                 │ NotificationService│
                 └─────────┬──────────┘
                           │
                           ▼
                       ┌───────┐
                       │MailHog│
                       └───────┘

              Em caso de falha no processamento
                           │
                           ▼
                 ┌─────────────────┐
                 │notification.dlx │
                 └────────┬────────┘
                          │
                          ▼
                 ┌─────────────────────┐
                 │notification.user.dlq│
                 └─────────────────────┘
```

O projeto utiliza os seguintes componentes de mensageria:

| Componente           | Nome                           |
| -------------------- | ------------------------------ |
| Exchange principal   | `user.events`                  |
| Queue principal      | `notification.user.registered` |
| Dead Letter Exchange | `notification.dlx`             |
| Dead Letter Queue    | `notification.user.dlq`        |

---

## Tratamento de Falhas

O `NotificationService` possui mecanismos para lidar com falhas durante o processamento das mensagens.

### Retry

Quando ocorre uma exceção durante o processamento de uma mensagem, o serviço realiza novas tentativas de processamento através do mecanismo de **Retry**.

### Dead Letter Exchange

Caso a mensagem não possa ser processada após as tentativas configuradas, ela é encaminhada para o:

```text
notification.dlx
```

### Dead Letter Queue

A mensagem é posteriormente direcionada para a:

```text
notification.user.dlq
```

Esse fluxo permite que mensagens que não puderam ser processadas não sejam simplesmente perdidas, possibilitando seu isolamento para análise posterior.

---

## Tecnologias

### Backend

* Java 21
* Spring Boot 4.1
* Spring Web
* Spring Data JPA
* Spring AMQP
* Bean Validation
* Lombok

### Banco de Dados

* PostgreSQL

### Mensageria

* RabbitMQ

### Desenvolvimento

* Docker
* Docker Compose
* MailHog

---

## Pré-requisitos

Para executar o projeto, é necessário ter instalado:

* Java 21
* Docker
* Docker Compose

Os dois microsserviços são executados separadamente pela IDE.

---

## Executando o Projeto

### 1. Subir a infraestrutura

Na raiz do projeto, execute:

```bash
docker compose up
```

Esse comando inicia os serviços de infraestrutura utilizados pela aplicação:

* PostgreSQL;
* RabbitMQ;
* MailHog.

### 2. Iniciar o User-service

Execute o `User-service` pela IDE.

O serviço será responsável por disponibilizar o endpoint de cadastro e publicar os eventos no RabbitMQ.

### 3. Iniciar o NotificationService

Execute o `NotificationService` pela IDE.

O serviço ficará responsável por consumir os eventos publicados pelo `User-service` e processar as notificações.

---

## Utilizando a Aplicação

Após iniciar os serviços, é possível realizar um cadastro através do endpoint:

```http
POST /users
```

Exemplo de requisição:

```http
POST /users
Content-Type: application/json
```

```json
{
  "name": "João Silva",
  "email": "joao@example.com"
}
```

O usuário será persistido no PostgreSQL e um evento de cadastro será publicado no RabbitMQ.

O `NotificationService` consumirá esse evento e enviará a notificação para o MailHog.

---

## MailHog

O MailHog é utilizado no projeto para simular o envio de e-mails durante o desenvolvimento.

Após realizar um cadastro, a caixa de entrada pode ser acessada através de:

[http://localhost:8025](http://localhost:8025?utm_source=chatgpt.com)

A interface permite visualizar os e-mails enviados pelo `NotificationService` sem a necessidade de utilizar um servidor de e-mail real.

---

## RabbitMQ

O RabbitMQ também disponibiliza uma interface de gerenciamento para visualizar exchanges, filas e mensagens.

A interface pode ser acessada em:

[http://localhost:15672](http://localhost:15672?utm_source=chatgpt.com)

Credenciais:

```text
Usuário: app
Senha: app
```

Através da interface é possível visualizar os componentes utilizados pelo projeto, incluindo:

* `user.events`;
* `notification.user.registered`;
* `notification.dlx`;
* `notification.user.dlq`.

---

## Banco de Dados

O PostgreSQL é utilizado exclusivamente pelo `User-service` para persistência dos usuários.

As tabelas necessárias são criadas automaticamente pela aplicação.

O `NotificationService` não possui banco de dados próprio.

---

A estrutura interna de cada serviço pode ser consultada diretamente no repositório.

---

## Testes

O projeto atualmente não possui testes automatizados.

Por se tratar de um projeto pequeno e educacional, o foco principal está na prática de:

* arquitetura de microsserviços;
* comunicação assíncrona;
* RabbitMQ;
* publicação e consumo de eventos;
* Retry;
* Dead Letter Exchange;
* Dead Letter Queue;
* integração entre serviços.

---

## Objetivo do Projeto

O principal objetivo deste projeto é servir como ambiente de aprendizado para compreender os fundamentos de uma arquitetura baseada em microsserviços e mensageria.

Apesar de possuir uma regra de negócio simples, o projeto permite praticar conceitos importantes, como:

* separação de responsabilidades entre serviços;
* comunicação assíncrona;
* publicação e consumo de eventos;
* processamento de mensagens;
* tratamento de falhas;
* Retry;
* Dead Letter Exchange;
* Dead Letter Queue;
* execução de infraestrutura através de Docker Compose.

A simplicidade do domínio é intencional, permitindo concentrar os estudos nos conceitos de **microsserviços e mensageria** sem adicionar complexidade desnecessária à regra de negócio.

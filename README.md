# 🛒 E-commerce Backend Core API com Spring Boot

Este projeto é a espinha dorsal de um sistema de e-commerce completo, construído com Java e Spring Boot. Ele oferece as funcionalidades essenciais para gerenciar produtos, carrinhos de compras, pedidos e um sistema robusto de autenticação e autorização.

## ✨ Funcionalidades Implementadas

* **Autenticação e Autorização:**
    * Registro e Login de usuários (`/api/auth/register`, `/api/auth/authenticate`).
    * Utiliza **JWT (JSON Web Tokens)** para segurança stateless.
    * **Spring Security** configurado para proteger endpoints com base em perfis (`ROLE_ADMIN`, `ROLE_CLIENT`).
    * `ROLE_ADMIN`: Acesso total (CRUD em Categorias, Produtos, Pedidos, Usuários).
    * `ROLE_CLIENT`: Acesso a listagens, perfil próprio, carrinho e pedidos próprios.
* **Gerenciamento de Perfis de Usuário (`/api/users/me`):**
    * Visualização e atualização de dados de perfil para o usuário logado.
* **Gerenciamento de Categorias (`/api/categories`):**
    * Operações CRUD (Create, Read, Update, Delete) para categorias de produtos.
* **Gerenciamento de Produtos (`/api/products`):**
    * Operações CRUD (Create, Read, Update, Delete) para produtos, associados a categorias.
* **Carrinho de Compras (`/api/cart`):**
    * Adição, atualização de quantidade e remoção de itens no carrinho do usuário.
    * Visualização do carrinho atual com detalhes de subtotal e total.
* **Gerenciamento de Pedidos (`/api/orders`):**
    * Criação de pedidos a partir do carrinho de compras.
    * Listagem de pedidos por usuário e listagem geral (para admin).
    * Atualização de status de pedidos (para admin).
* **Simulação de Gateway de Pagamento (`/api/orders/process-payment`):**
    * Simulação de processamento de pagamento, atualizando o status do pedido.
* **Tratamento Global de Exceções:**
    * Respostas de erro padronizadas e informativas (400 Bad Request, 404 Not Found, 500 Internal Server Error).

## 🚀 Tecnologias Utilizadas

* **Java 24**
* **Spring Boot 3.2.x**
* **Spring Data JPA** com **Hibernate**
* **PostgreSQL** como banco de dados relacional
* **Spring Security** para autenticação e autorização
* **JWT (JSON Web Tokens)** para segurança
* **Lombok** para reduzir código boilerplate
* **Maven** para gerenciamento de dependências

## ⚙️ Como Rodar o Projeto Localmente

1.  **Pré-requisitos:**
    * JDK 21 ou superior instalado.
    * Maven instalado.
    * PostgreSQL instalado e rodando (ou Docker Desktop para usar via Docker Compose).

2.  **Configuração do Banco de Dados:**
    * Crie um banco de dados PostgreSQL chamado `ecommerce_db`.
    * Configure as credenciais (usuário: `postgres`, senha: `DEUCE#03tk` ou sua própria senha).
    * As tabelas serão criadas automaticamente pelo Hibernate ao iniciar a aplicação pela primeira vez (com `spring.jpa.hibernate.ddl-auto=update` no `application.properties`). No código atual, `ddl-auto` está `none`, então o banco já deve estar criado com as tabelas de um run anterior.

3.  **Configuração da Aplicação:**
    * Clone este repositório para sua máquina local.
    * Abra o projeto em sua IDE favorita (IntelliJ IDEA, Eclipse).
    * Verifique o arquivo `src/main/resources/application.properties` para as configurações do banco de dados. Por padrão, ele usa variáveis de ambiente ou fallback para configurações locais.

4.  **Executar a Aplicação:**
    * No IntelliJ, localize a classe `EcommerceApplication.java` (em `src/main/java/com/kayllanne/ecommerce/EcommerceApplication.java`).
    * Clique com o botão direito e selecione "Run 'EcommerceApplication.main()'".
    * A aplicação estará disponível em `http://localhost:8080`.

## 🧪 Testando a API

Você pode testar a API usando ferramentas como [Postman](https://www.postman.com/) ou a própria interface do [Swagger UI](http://localhost:8080/swagger-ui.html) (após a aplicação estar rodando).

### Fluxo Básico de Teste:

1.  **Registrar Usuário:** `POST /api/auth/register`
    * Body: `{"username": "testuser", "email": "test@example.com", "password": "password"}`
2.  **Login:** `POST /api/auth/authenticate`
    * Body: `{"username": "testuser", "password": "password"}`
    * Copie o `accessToken` retornado.
3.  **Criar Categoria (como ADMIN):** (Verifique se o usuário "testuser" tem `ROLE_ADMIN` no DB, ou crie um admin)
    * `POST /api/categories`
    * Headers: `Authorization: Bearer <accessToken>`
    * Body: `{"name": "Eletrônicos"}`
4.  **Listar Produtos (público/cliente):**
    * `GET /api/products`
    * Headers: `Authorization: Bearer <accessToken>`
5.  **Adicionar ao Carrinho:**
    * `POST /api/cart/items`
    * Headers: `Authorization: Bearer <accessToken>`, `Content-Type: application/json`
    * Body: `{"productId": <ID_DO_PRODUTO>, "quantity": 1}`
6.  **Criar Pedido:**
    * `POST /api/orders`
    * Headers: `Authorization: Bearer <accessToken>`

## 🚧 Melhorias Futuras (Opcionais)

* Integração com um Gateway de Pagamento real.
* Gestão de estoque avançada.
* Funcionalidades de busca e filtros mais robustas.
* Testes automatizados (Unitários e de Integração).
* Implementação de Refresh Tokens (além do básico que já temos).
* Contêineres Docker para deployment simplificado (via `docker compose up`).
* Frontend para consumir esta API.

---
**Desenvolvido com 💖 por Kayllanne Farias.**

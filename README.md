# BANDTADS - README de Inicialização

## Visão Geral

Este documento descreve o processo de inicialização do sistema BANDTADS, que consiste em quatro microsserviços (implementados com Spring Boot e Java), um serviço de orquestração Saga (também implementado com Spring Boot e Java), um API Gateway e um frontend em Angular.

### Arquitetura do Sistema

- **Microsserviços**: 4 serviços independentes implementados em Spring Boot.
- **Serviço de Orquestração Saga**: Gerencia a coordenação entre os microsserviços.
- **API Gateway**: Gerencia as requisições e rotea para os microsserviços apropriados.
- **Frontend**: Aplicação Angular que interage com o sistema.

### Passos para Inicialização

#### 1. Criação das Imagens Docker e Inicialização do Sistema

Foi criado um script para facilitar o processo de criação das imagens Docker e inicialização do sistema. Siga as instruções abaixo:

##### **Para usuários de Linux/Mac (ou Windows utilizando um terminal Linux como WSL ou Git Bash):**

1. **Certifique-se de que o Docker e o Docker Compose estão instalados e em execução.**

2. **Clone o diretório e navegue até o diretório do projeto que contém o script `init.bash`**:
   ```bash
   cd ./trabalho-DAC
   ```

3. **Torne o script executável (se necessário)**:
   ```bash
   chmod +x init.bash
   ```

4. **Execute o script**:
   ```bash
   ./init.bash
   ```

Este script automatiza a criação das imagens Docker para todos os microsserviços e o serviço de orquestração Saga, além de iniciar os containers do sistema.

#### 2. Acesso ao Sistema

1. **Frontend**: Após a inicialização, o frontend estará disponível em `http://localhost:4200`.
   
2. **Login Inicial**: Acesse o sistema utilizando as credenciais padrão:
   - **Usuário**: `admin@email.com`
   - **Senha**: `123`

#### 3. Configuração Inicial

1. **Criação de Gerentes**:
   - Após o login, navegue até a seção de gerenciamento e crie os gerentes conforme necessário.

2. **Cadastro Automático de Clientes**:
   - Após a criação dos gerentes, faça o autocadastro como cliente.

---

UNIVERSIDADE FEDERAL DO PARANÁ

TECNOLOGIA EM ANÁLISE E DESENVOLVIMENTO DE SISTEMAS

DISCIPLINA: DESENVOLVIMENTO DE APLICAÇÕES CORPORATIVAS

ALUNOS(AS):
- Gabriel Mendes Tulio
- João José Moreira Ramos Neto
- Luigi Ledermann Girardi
- Maria Eduarda Muncinelli
- Melissa Silva de Oliveira

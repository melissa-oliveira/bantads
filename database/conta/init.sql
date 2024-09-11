-- Criação do banco de dados
CREATE DATABASE "ms-conta-cud";
\c "ms-conta-cud";

-- Tabela cliente
CREATE TABLE cliente (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255),
    cpf VARCHAR(14)
);

-- Tabela gerente
CREATE TABLE gerente (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255),
    cpf VARCHAR(14),
    qnt_clientes INT
);

-- Tabela conta
CREATE TABLE conta (
    id BIGSERIAL PRIMARY KEY,
    numero_conta VARCHAR(100) UNIQUE,
    data_criacao VARCHAR(20),
    limite NUMERIC(15,2),
    saldo NUMERIC(15,2),
    status_conta VARCHAR(50), 
    id_cliente INT REFERENCES cliente(id),
    id_gerente INT REFERENCES gerente(id),
    gerente_id INT REFERENCES gerente(id)
);

-- Tabela historico_movimentacao
CREATE TABLE historico_movimentacao (
    id BIGSERIAL PRIMARY KEY,
    dataHora VARCHAR(50),
    tipo_movimentacao VARCHAR(50), 
    valor NUMERIC(15,2),
    id_cliente_origem INT,
    id_cliente_destino INT
);


-- Criação do banco de dados
CREATE DATABASE "ms-conta-r";
\c "ms-conta-r";

-- Tabela clienteR
CREATE TABLE clienteR (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255),
    cpf VARCHAR(14)
);

-- Tabela gerenteR
CREATE TABLE gerenteR (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255),
    cpf VARCHAR(14),
    qnt_clientes INT
);

-- Tabela contaR
CREATE TABLE contaR (
    id BIGSERIAL PRIMARY KEY,
    numero_conta VARCHAR(100) UNIQUE,
    data_criacao VARCHAR(20),
    limite NUMERIC(15,2),
    saldo NUMERIC(15,2),
    status_conta VARCHAR(50),
    id_cliente INT REFERENCES clienteR(id),
    id_gerente INT REFERENCES gerenteR(id)
);

-- Tabela historico_movimentacaoR
CREATE TABLE historico_movimentacao (
    id BIGSERIAL PRIMARY KEY,
    dataHora VARCHAR(50),
    tipo_movimentacao VARCHAR(50), 
    valor NUMERIC(15,2),
    id_cliente_origem INT,
    id_cliente_destino INT
);

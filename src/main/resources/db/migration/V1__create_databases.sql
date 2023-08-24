-- Active: 1692448226517@@127.0.0.1@5432@minhasfinancas@financas

CREATE SCHEMA IF NOT EXISTS financas;

CREATE TABLE IF NOT EXISTS financas.usuarios (
    id UUID NOT NULL PRIMARY KEY,
    nome VARCHAR,
    email VARCHAR UNIQUE,
    senha VARCHAR,
    data_cadastro DATE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS financas.lancamentos (
    id UUID NOT NULL PRIMARY KEY,
    descricao VARCHAR NOT NULL,
    mes INTEGER NOT NULL,
    ano INTEGER NOT NULL,
    valor NUMERIC(16,2) NOT NULL,
    tipo VARCHAR CHECK (tipo IN ('RECEITA', 'DESPESA')) NOT NULL,
    status VARCHAR CHECK(status IN ('PENDENTE', 'CANCELADO', 'EFETIVADO')) NOT NULL,
    data_cadastro DATE DEFAULT NOW(),
    id_usuario UUID REFERENCES financas.usuarios(id)
);

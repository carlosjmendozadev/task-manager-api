-- Criar banco (se não existir)
CREATE DATABASE IF NOT EXISTS minha-app-db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Usar banco
USE minha-app-db;

-- Tabela de exemplo
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed TINYINT(1) DEFAULT FALSE
);
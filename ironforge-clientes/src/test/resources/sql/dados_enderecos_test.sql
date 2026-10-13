-- Limpa a tabela usando os nomes corretos mapeados pelas entidades (tb_endereco e tb_cliente)
DELETE FROM tb_endereco;
DELETE FROM tb_cliente;

-- Insere o cliente associado
INSERT INTO tb_cliente (id, nome, cpf, email, keycloak_id, data_criacao, ativo)
VALUES (
           'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d',
           'Cliente Teste Endereco',
           '12345678900',
           'cliente.endereco@teste.com',
           'kc-user-123',
           CURRENT_TIMESTAMP,
           true
       );

-- Insere o endereço vinculado ao cliente
INSERT INTO tb_endereco (id, logradouro, numero, complemento, bairro, cidade, estado, cep, cliente_id)
VALUES (
           'f47ac10b-58cc-4372-a567-0e02b2c3d4e5',
           'Rua das Flores',
           '123',
           'Apto 101',
           'Centro',
           'Goiânia',
           'GO',
           '74000000',
           'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d'
       );
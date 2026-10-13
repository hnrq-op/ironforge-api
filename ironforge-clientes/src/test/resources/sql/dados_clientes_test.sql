DELETE FROM tb_endereco;
DELETE FROM tb_cliente;

INSERT INTO tb_cliente (id, nome, cpf, email, telefone, keycloak_id, ativo, data_criacao)
VALUES ('e8f292d3-1234-4a2e-8901-b2c3d4e5f6a1', 'Cliente Teste Um', '12345678900', 'cliente@teste.com', '64999999999', 'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', true, CURRENT_TIMESTAMP);

INSERT INTO tb_cliente (id, nome, cpf, email, telefone, keycloak_id, ativo, data_criacao)
VALUES ('f9a303e4-5678-4b3f-9012-c3d4e5f6a7b2', 'Cliente Teste Dois', '98765432100', 'admin@teste.com', '64888888888', 'b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e', true, CURRENT_TIMESTAMP);
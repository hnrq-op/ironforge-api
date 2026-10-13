-- Limpa a tabela antes de inserir para garantir um ambiente isolado em cada execução
DELETE FROM tb_nota_fiscal;

-- Inserção de registro de teste
INSERT INTO tb_nota_fiscal (id, pedido_id, numero_nota, chave_acesso, pdf_url, data_emissao)
VALUES (
           'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
           'c3d4e5f6-7a8b-9c0d-1e2f-3a4b5c6d7e8f',
           '000000001',
           '35261012345678000195550010000000011000000001',
           'http://localhost:9000/ironforge-faturas/notafiscal_pedido_c3d4e5f6-7a8b-9c0d-1e2f-3a4b5c6d7e8f.pdf',
           '2026-10-05 10:00:00+00'
       );
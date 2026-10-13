-- Produto 1: Ativo / Suplementos
INSERT INTO tb_produto (id, sku, nome, marca, descricao, categoria, preco, ativo, data_criacao)
VALUES ('123e4567-e89b-12d3-a456-426614174000', 'CREATINA-500G', 'Creatina 500g', 'IronForge', 'Pura', 'Suplementos', 99.90, true, CURRENT_TIMESTAMP);

-- Produto 2: Inativo / Suplementos
INSERT INTO tb_produto (id, sku, nome, marca, descricao, categoria, preco, ativo, data_criacao)
VALUES ('223e4567-e89b-12d3-a456-426614174001', 'WHEY-1KG', 'Whey Protein 1kg', 'IronForge', 'Isolado', 'Suplementos', 149.90, false, CURRENT_TIMESTAMP);

-- Produto 3: Ativo / Acessórios
INSERT INTO tb_produto (id, sku, nome, marca, descricao, categoria, preco, ativo, data_criacao)
VALUES ('323e4567-e89b-12d3-a456-426614174002', 'SQUEEZE-700ML', 'Garrafa 700ml', 'IronForge', 'Inox', 'Acessórios', 39.90, true, CURRENT_TIMESTAMP);


INSERT INTO tb_estoque (id, produto_id, quantidade_disponivel, quantidade_reservada, data_atualizacao)
VALUES ('987e6543-e21b-12d3-a456-426614174000', '123e4567-e89b-12d3-a456-426614174000', 50, 0, CURRENT_TIMESTAMP);
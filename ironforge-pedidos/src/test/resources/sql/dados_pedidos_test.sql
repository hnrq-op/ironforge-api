-- Pedido 1: Recém criado (ajustado para REALIZADO)
INSERT INTO tb_pedido (id, status, valor_total, data_criacao, cliente_id, chave_pagamento, observacoes, codigo_rastreio, url_nf)
VALUES ('b1111111-1111-1111-1111-111111111111', 'REALIZADO', 99.90, CURRENT_TIMESTAMP, 'a1111111-1111-1111-1111-111111111111', NULL, 'Entregar em horário comercial', NULL, NULL);

-- Pedido 2: Pago / Pronto para separação
INSERT INTO tb_pedido (id, status, valor_total, data_criacao, cliente_id, chave_pagamento, observacoes, codigo_rastreio, url_nf)
VALUES ('b2222222-2222-2222-2222-222222222222', 'PAGO', 399.70, CURRENT_TIMESTAMP, 'a1111111-1111-1111-1111-111111111111', 'PIX-KEY-123456789', 'Deixar na portaria', NULL, NULL);

-- Pedido 3: Em transporte (ajustado para ENVIADO)
INSERT INTO tb_pedido (id, status, valor_total, data_criacao, cliente_id, chave_pagamento, observacoes, codigo_rastreio, url_nf)
VALUES ('b3333333-3333-3333-3333-333333333333', 'ENVIADO', 39.90, CURRENT_TIMESTAMP, 'a2222222-2222-2222-2222-222222222222', 'CC-TRANS-987654321', NULL, 'BR123456789BR', 'https://ironforge.s3.amazonaws.com/nfe/pedido-3.pdf');


-- Item 1 (Pedido 1): 1x Creatina 500g
INSERT INTO tb_item_pedido (id, quantidade, preco_unitario, subtotal, produto_id, pedido_id)
VALUES ('c1111111-1111-1111-1111-111111111111', 1, 99.90, 99.90, '123e4567-e89b-12d3-a456-426614174000', 'b1111111-1111-1111-1111-111111111111');

-- Item 1 (Pedido 2): 1x Creatina 500g
INSERT INTO tb_item_pedido (id, quantidade, preco_unitario, subtotal, produto_id, pedido_id)
VALUES ('c2222222-2222-2222-2222-222222222222', 1, 99.90, 99.90, '123e4567-e89b-12d3-a456-426614174000', 'b2222222-2222-2222-2222-222222222222');

-- Item 2 (Pedido 2): 2x Whey Protein 1kg
INSERT INTO tb_item_pedido (id, quantidade, preco_unitario, subtotal, produto_id, pedido_id)
VALUES ('c3333333-3333-3333-3333-333333333333', 2, 149.90, 299.80, '223e4567-e89b-12d3-a456-426614174001', 'b2222222-2222-2222-2222-222222222222');

-- Item 1 (Pedido 3): 1x Garrafa 700ml
INSERT INTO tb_item_pedido (id, quantidade, preco_unitario, subtotal, produto_id, pedido_id)
VALUES ('c4444444-4444-4444-4444-444444444444', 1, 39.90, 39.90, '323e4567-e89b-12d3-a456-426614174002', 'b3333333-3333-3333-3333-333333333333');
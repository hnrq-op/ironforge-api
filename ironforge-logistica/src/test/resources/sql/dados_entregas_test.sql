DELETE FROM tb_entrega;

INSERT INTO tb_entrega (id, pedido_id, codigo_rastreio, chave_logistica, status, transportadora, data_envio, data_previsao, data_atualizacao)
VALUES
    (
        'e1111111-1111-1111-1111-111111111111',
        'a1111111-1111-1111-1111-111111111111',
        'BR123456789BR',
        'chave-logistica-valida-1',
        'ENVIADO',
        'Jadlog',
        '2026-10-09 10:00:00-03',
        '2026-10-15',
        '2026-10-09 10:00:00-03'
    ),
    (
        'e2222222-2222-2222-2222-222222222222',
        'a2222222-2222-2222-2222-222222222222',
        NULL,
        'chave-logistica-valida-2',
        'AGUARDANDO_TRANSPORTADORA',
        NULL,
        NULL,
        NULL,
        '2026-10-09 10:00:00-03'
    );
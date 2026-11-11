-- Baixa automática de estoque ao vender
CREATE OR REPLACE FUNCTION fn_baixa_estoque()
RETURNS TRIGGER AS $$
DECLARE
    v_idestoque INTEGER;
    v_qtd_atual INTEGER;
BEGIN
    SELECT p.idestoque INTO v_idestoque
    FROM produto p
    WHERE p.idproduto = NEW.idproduto;

    IF v_idestoque IS NULL THEN
        RAISE EXCEPTION 'Produto % não possui estoque vinculado.', NEW.idproduto;
    END IF;

    UPDATE estoque
    SET quantidadeatual = quantidadeatual - NEW.qtd
    WHERE idestoque = v_idestoque;

    SELECT quantidadeatual INTO v_qtd_atual
    FROM estoque WHERE idestoque = v_idestoque;

    IF v_qtd_atual <= (SELECT quantidademinima FROM estoque WHERE idestoque = v_idestoque) THEN
        RAISE NOTICE 'ALERTA: Estoque % abaixo do mínimo! Quantidade atual: %', v_idestoque, v_qtd_atual;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Bloquear venda sem estoque suficiente
CREATE OR REPLACE FUNCTION fn_verificar_estoque()
RETURNS TRIGGER AS $$
DECLARE
    v_qtd_disponivel INTEGER;
    v_nome_produto VARCHAR;
BEGIN
    SELECT e.quantidadeatual, p.nomeproduto
    INTO v_qtd_disponivel, v_nome_produto
    FROM produto p
    JOIN estoque e ON p.idestoque = e.idestoque
    WHERE p.idproduto = NEW.idproduto;

    IF v_qtd_disponivel IS NULL THEN
        RAISE EXCEPTION 'Produto % não possui estoque cadastrado.', NEW.idproduto;
    END IF;

    IF NEW.qtd > v_qtd_disponivel THEN
        RAISE EXCEPTION 'Estoque insuficiente para "%"! Solicitado: %, Disponível: %',
            v_nome_produto, NEW.qtd, v_qtd_disponivel;
    END IF;

    IF NEW.qtd <= 0 THEN
        RAISE EXCEPTION 'Quantidade deve ser maior que zero.';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Entrada automática de estoque ao comprar
CREATE OR REPLACE FUNCTION fn_entrada_estoque()
RETURNS TRIGGER AS $$
DECLARE
    v_idestoque INTEGER;
BEGIN
    SELECT p.idestoque INTO v_idestoque
    FROM produto p
    WHERE p.idproduto = NEW.idproduto;

    IF v_idestoque IS NULL THEN
        RAISE NOTICE 'Produto % não possui estoque vinculado. Entrada não contabilizada.', NEW.idproduto;
        RETURN NEW;
    END IF;

    UPDATE estoque
    SET quantidadeatual = quantidadeatual + NEW.qtd
    WHERE idestoque = v_idestoque;

    RAISE NOTICE 'Estoque atualizado: +% unidades no estoque %', NEW.qtd, v_idestoque;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Recalcular subtotal do estoque
CREATE OR REPLACE FUNCTION fn_recalcular_subtotal()
RETURNS TRIGGER AS $$
DECLARE
    v_subtotal NUMERIC(10,2);
BEGIN
    IF OLD.quantidadeatual IS DISTINCT FROM NEW.quantidadeatual THEN
        SELECT COALESCE(SUM(p.preco * NEW.quantidadeatual), 0)
        INTO v_subtotal
        FROM produto p
        WHERE p.idestoque = NEW.idestoque;

        UPDATE estoque
        SET subtotal = v_subtotal
        WHERE idestoque = NEW.idestoque
          AND subtotal IS DISTINCT FROM v_subtotal;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Validar formato do CNPJ
CREATE OR REPLACE FUNCTION fn_validar_cnpj()
RETURNS TRIGGER AS $$
DECLARE
    v_cnpj_limpo VARCHAR;
BEGIN
    IF NEW.cnpj IS NULL THEN
        RETURN NEW;
    END IF;

    v_cnpj_limpo := REGEXP_REPLACE(NEW.cnpj, '[^0-9]', '', 'g');

    IF LENGTH(v_cnpj_limpo) != 14 THEN
        RAISE EXCEPTION 'CNPJ inválido: "%" deve conter exatamente 14 dígitos numéricos. Encontrados: %',
            NEW.cnpj, LENGTH(v_cnpj_limpo);
    END IF;

    IF v_cnpj_limpo ~ '^(\d)\1{13}$' THEN
        RAISE EXCEPTION 'CNPJ inválido: "%" - todos os dígitos são iguais.', NEW.cnpj;
    END IF;

    NEW.cnpj := SUBSTRING(v_cnpj_limpo, 1, 2) || '.' ||
                SUBSTRING(v_cnpj_limpo, 3, 3) || '.' ||
                SUBSTRING(v_cnpj_limpo, 6, 3) || '/' ||
                SUBSTRING(v_cnpj_limpo, 9, 4) || '-' ||
                SUBSTRING(v_cnpj_limpo, 13, 2);

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Impedir exclusão de cliente com dependências
CREATE OR REPLACE FUNCTION fn_proteger_cliente()
RETURNS TRIGGER AS $$
DECLARE
    v_animais INTEGER;
    v_servicos INTEGER;
    v_vendas INTEGER;
    v_msg TEXT := '';
BEGIN
    SELECT COUNT(*) INTO v_animais FROM animal WHERE idcliente = OLD.idcliente;
    SELECT COUNT(*) INTO v_servicos FROM servico WHERE idcliente = OLD.idcliente;
    SELECT COUNT(*) INTO v_vendas FROM venda WHERE idcliente = OLD.idcliente;

    IF v_animais > 0 THEN
        v_msg := v_msg || v_animais || ' animal(is), ';
    END IF;
    IF v_servicos > 0 THEN
        v_msg := v_msg || v_servicos || ' serviço(s), ';
    END IF;
    IF v_vendas > 0 THEN
        v_msg := v_msg || v_vendas || ' venda(s), ';
    END IF;

    IF v_msg != '' THEN
        v_msg := LEFT(v_msg, LENGTH(v_msg) - 2);
        RAISE EXCEPTION 'Não é possível excluir o cliente "%" (ID: %). Existem dependências: %',
            OLD.nome, OLD.idcliente, v_msg;
    END IF;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- TRIGGERS

DROP TRIGGER IF EXISTS trg_baixa_estoque ON venda_produto_contem;
CREATE TRIGGER trg_baixa_estoque
    AFTER INSERT ON venda_produto_contem
    FOR EACH ROW
    EXECUTE FUNCTION fn_baixa_estoque();

DROP TRIGGER IF EXISTS trg_verificar_estoque ON venda_produto_contem;
CREATE TRIGGER trg_verificar_estoque
    BEFORE INSERT ON venda_produto_contem
    FOR EACH ROW
    EXECUTE FUNCTION fn_verificar_estoque();

DROP TRIGGER IF EXISTS trg_entrada_estoque ON fornecedor_produto_compra;
CREATE TRIGGER trg_entrada_estoque
    AFTER INSERT ON fornecedor_produto_compra
    FOR EACH ROW
    EXECUTE FUNCTION fn_entrada_estoque();

DROP TRIGGER IF EXISTS trg_recalcular_subtotal ON estoque;
CREATE TRIGGER trg_recalcular_subtotal
    AFTER UPDATE OF quantidadeatual ON estoque
    FOR EACH ROW
    EXECUTE FUNCTION fn_recalcular_subtotal();

DROP TRIGGER IF EXISTS trg_validar_cnpj ON fornecedor;
CREATE TRIGGER trg_validar_cnpj
    BEFORE INSERT OR UPDATE OF cnpj ON fornecedor
    FOR EACH ROW
    EXECUTE FUNCTION fn_validar_cnpj();

DROP TRIGGER IF EXISTS trg_proteger_cliente ON cliente;
CREATE TRIGGER trg_proteger_cliente
    BEFORE DELETE ON cliente
    FOR EACH ROW
    EXECUTE FUNCTION fn_proteger_cliente();

-- FUNÇÕES DE CONSULTA E RELATÓRIOS GERENCIAIS

CREATE OR REPLACE FUNCTION fn_relatorio_vendas_cliente(p_idcliente INTEGER)
RETURNS TABLE (
    nome_cliente VARCHAR,
    data_venda DATE,
    produto VARCHAR,
    quantidade INTEGER,
    preco_unitario NUMERIC,
    total_item NUMERIC
) AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM cliente WHERE idcliente = p_idcliente) THEN
        RAISE EXCEPTION 'Cliente com ID % não encontrado.', p_idcliente;
    END IF;

    RETURN QUERY
    SELECT
        c.nome,
        v.datavenda,
        p.nomeproduto,
        vpc.qtd,
        vpc.valorunit,
        (vpc.valorunit * vpc.qtd)::NUMERIC AS total
    FROM cliente c
    JOIN venda v ON v.idcliente = c.idcliente
    JOIN venda_produto_contem vpc ON vpc.idvenda = v.idvenda
    JOIN produto p ON p.idproduto = vpc.idproduto
    WHERE c.idcliente = p_idcliente
    ORDER BY v.datavenda DESC, p.nomeproduto;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION fn_produtos_estoque_baixo()
RETURNS TABLE (
    id_produto INTEGER,
    nome_produto VARCHAR,
    quantidade_atual INTEGER,
    quantidade_minima INTEGER,
    diferenca INTEGER,
    urgencia VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        p.idproduto,
        p.nomeproduto,
        e.quantidadeatual,
        e.quantidademinima,
        (e.quantidadeatual - e.quantidademinima),
        CASE
            WHEN e.quantidadeatual = 0 THEN 'ESGOTADO'::VARCHAR
            WHEN e.quantidadeatual <= e.quantidademinima / 2 THEN 'CRÍTICO'::VARCHAR
            WHEN e.quantidadeatual <= e.quantidademinima THEN 'BAIXO'::VARCHAR
            ELSE 'NORMAL'::VARCHAR
        END
    FROM produto p
    JOIN estoque e ON p.idestoque = e.idestoque
    WHERE e.quantidadeatual <= e.quantidademinima
    ORDER BY (e.quantidadeatual - e.quantidademinima) ASC;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION fn_total_gasto_cliente(p_idcliente INTEGER)
RETURNS TABLE (
    nome_cliente VARCHAR,
    total_produtos NUMERIC,
    total_servicos NUMERIC,
    total_geral NUMERIC,
    qtd_compras BIGINT,
    qtd_servicos BIGINT
) AS $$
DECLARE
    v_nome VARCHAR;
    v_total_prod NUMERIC;
    v_total_serv NUMERIC;
    v_qtd_compras BIGINT;
    v_qtd_servicos BIGINT;
BEGIN
    SELECT c.nome INTO v_nome FROM cliente c WHERE c.idcliente = p_idcliente;
    IF v_nome IS NULL THEN
        RAISE EXCEPTION 'Cliente com ID % não encontrado.', p_idcliente;
    END IF;

    SELECT COALESCE(SUM(vpc.valorunit * vpc.qtd), 0), COUNT(DISTINCT v.idvenda)
    INTO v_total_prod, v_qtd_compras
    FROM venda v
    JOIN venda_produto_contem vpc ON vpc.idvenda = v.idvenda
    WHERE v.idcliente = p_idcliente;

    SELECT COALESCE(SUM(s.valor), 0), COUNT(*)
    INTO v_total_serv, v_qtd_servicos
    FROM servico s
    WHERE s.idcliente = p_idcliente;

    RETURN QUERY
    SELECT v_nome, v_total_prod, v_total_serv,
           (v_total_prod + v_total_serv),
           v_qtd_compras, v_qtd_servicos;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION fn_ranking_produtos(p_limite INTEGER DEFAULT 10)
RETURNS TABLE (
    posicao BIGINT,
    nome_produto VARCHAR,
    quantidade_total BIGINT,
    faturamento_total NUMERIC,
    preco_medio NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        ROW_NUMBER() OVER (ORDER BY SUM(vpc.qtd) DESC),
        p.nomeproduto,
        SUM(vpc.qtd)::BIGINT,
        SUM(vpc.valorunit * vpc.qtd)::NUMERIC,
        ROUND(AVG(vpc.valorunit), 2)::NUMERIC
    FROM venda_produto_contem vpc
    JOIN produto p ON p.idproduto = vpc.idproduto
    GROUP BY p.idproduto, p.nomeproduto
    ORDER BY SUM(vpc.qtd) DESC
    LIMIT p_limite;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION fn_faturamento_periodo(p_data_inicio DATE, p_data_fim DATE)
RETURNS TABLE (
    tipo_receita VARCHAR,
    quantidade BIGINT,
    faturamento NUMERIC
) AS $$
DECLARE
    v_total_vendas NUMERIC;
    v_total_servicos NUMERIC;
    v_qtd_vendas BIGINT;
    v_qtd_servicos BIGINT;
BEGIN
    IF p_data_inicio > p_data_fim THEN
        RAISE EXCEPTION 'Data inicial (%) não pode ser maior que data final (%).', p_data_inicio, p_data_fim;
    END IF;

    SELECT COALESCE(SUM(vpc.valorunit * vpc.qtd), 0), COUNT(DISTINCT v.idvenda)
    INTO v_total_vendas, v_qtd_vendas
    FROM venda v
    JOIN venda_produto_contem vpc ON vpc.idvenda = v.idvenda
    WHERE v.datavenda BETWEEN p_data_inicio AND p_data_fim;

    SELECT COALESCE(SUM(s.valor), 0), COUNT(*)
    INTO v_total_servicos, v_qtd_servicos
    FROM servico s
    WHERE s.datatransacao BETWEEN p_data_inicio AND p_data_fim;

    tipo_receita := 'Vendas de Produtos'; quantidade := v_qtd_vendas; faturamento := v_total_vendas;
    RETURN NEXT;

    tipo_receita := 'Serviços'; quantidade := v_qtd_servicos; faturamento := v_total_servicos;
    RETURN NEXT;

    tipo_receita := '*** TOTAL ***'; quantidade := v_qtd_vendas + v_qtd_servicos; faturamento := v_total_vendas + v_total_servicos;
    RETURN NEXT;
END;
$$ LANGUAGE plpgsql;

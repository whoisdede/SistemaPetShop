-- =============================================================
-- SISTEMA DE GESTÃO DE PETSHOP - CARGA INICIAL DE DADOS
-- =============================================================

INSERT INTO funcionario (nome, cargo, salario) VALUES
('Carlos Silva', 'Vendedor', 2500.00),
('Ana Oliveira', 'Tosadora', 2800.00),
('Pedro Santos', 'Veterinário', 5000.00),
('Maria Costa', 'Gerente', 4500.00);

INSERT INTO usuario (login, senha, nivelacesso, idfunc) VALUES
('carlos', 'senha123', 'operador', 1),
('ana', 'senha123', 'operador', 2),
('maria', 'senha123', 'admin', 4);

INSERT INTO cliente (nome, email, datanascimento, endereco) VALUES
('João Pereira', 'joao@email.com', '1990-05-15', 'Rua das Palmeiras, 123'),
('Fernanda Lima', 'fernanda@email.com', '1985-08-22', 'Av. Brasil, 456'),
('Ricardo Souza', 'ricardo@email.com', '1992-03-10', 'Rua XV de Novembro, 789'),
('Patrícia Mendes', 'patricia@email.com', '1988-11-30', 'Rua Blumenau, 321'),
('Lucas Almeida', 'lucas@email.com', '1995-07-18', 'Rua Dona Francisca, 654');

INSERT INTO animal (nome, raca, especie, datanasc, dataadmissao, motivo, idcliente, idfunc) VALUES
('Rex', 'Labrador', 'Cão', '2020-01-10', '2026-03-15', 'Banho e tosa', 1, 2),
('Mimi', 'Persa', 'Gato', '2019-06-20', '2026-04-10', 'Consulta veterinária', 1, 3),
('Thor', 'Bulldog', 'Cão', '2021-03-05', '2026-05-20', 'Banho', 2, 2),
('Luna', 'SRD', 'Gato', '2022-08-12', '2026-06-01', 'Vacinação anual', 3, 3),
('Bob', 'Golden Retriever', 'Cão', '2020-11-25', '2026-07-15', 'Tosa higiênica', 4, 2),
('Pipoca', 'Poodle', 'Cão', '2021-09-03', '2026-08-20', 'Banho e tosa', 5, 2);

INSERT INTO estoque (quantidademinima, quantidadeatual, subtotal) VALUES
(10, 30, 0),
(5, 15, 0),
(5, 4, 0),
(8, 25, 0);

INSERT INTO produto (nomeproduto, preco, custo, idestoque) VALUES
('Ração Premium Cães Adultos 15kg', 189.90, 120.00, 1),
('Ração Filhotes 10kg', 149.90, 95.00, 1),
('Bola de Borracha Resistente', 29.90, 12.00, 2),
('Osso de Nylon Dental', 39.90, 18.00, 2),
('Vermífugo Canino (cx 4 comp.)', 45.00, 25.00, 3),
('Antipulgas Mastigável', 89.90, 50.00, 3),
('Coleira Peitoral Ajustável', 59.90, 30.00, 4),
('Cama Pet Almofadada Grande', 199.90, 110.00, 4);

-- Atualiza subtotal inicial dos estoques
UPDATE estoque e
SET subtotal = COALESCE((SELECT SUM(p.preco * e.quantidadeatual) FROM produto p WHERE p.idestoque = e.idestoque), 0);

INSERT INTO fornecedor (nome, razaosocial, cnpj, endereco) VALUES
('PetFood Distribuidora', 'PetFood Alimentos Ltda', '12.345.678/0001-90', 'Rua Industrial, 100'),
('BrinquePet Atacado', 'BrinquePet Acessórios ME', '23.456.789/0001-01', 'Av. Comercial, 200'),
('VetPharma Sul', 'VetPharma Medicamentos S.A.', '34.567.890/0001-12', 'Rua da Saúde, 300');

INSERT INTO fornecedor_produto_compra (idfornecedor, idproduto, data, qtd) VALUES
(1, 1, '2026-09-01', 10),
(1, 2, '2026-09-05', 10),
(2, 3, '2026-09-10', 15);

INSERT INTO servico (valor, tiposervico, datatransacao, idcliente, idusuario) VALUES
(80.00, 'Banho e Tosa', '2026-10-15', 1, 2),
(50.00, 'Banho', '2026-10-16', 2, 2),
(140.00, 'Consulta Veterinária', '2026-10-17', 3, 3),
(75.00, 'Tosa Higiênica', '2026-10-18', 4, 2);

INSERT INTO usuario_estoque_administra (idusuario, idestoque) VALUES
(3, 1), (3, 2), (3, 3), (3, 4);

INSERT INTO venda (datavenda, idcliente) VALUES
('2026-10-20', 1),
('2026-10-21', 2),
('2026-10-22', 3);

INSERT INTO venda_produto_contem (idvenda, idproduto, valorunit, qtd) VALUES
(1, 1, 189.90, 1),
(1, 3, 29.90, 2),
(2, 7, 59.90, 1),
(3, 5, 45.00, 1);

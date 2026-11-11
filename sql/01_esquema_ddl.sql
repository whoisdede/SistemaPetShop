
CREATE TABLE IF NOT EXISTS Fornecedor (
    idFornecedor SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    razaoSocial VARCHAR(100),
    cnpj VARCHAR(20) UNIQUE,
    endereco VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Estoque (
    idEstoque SERIAL PRIMARY KEY,
    quantidadeMinima INT,
    quantidadeAtual INT,
    subtotal NUMERIC(10, 2)
);

CREATE TABLE IF NOT EXISTS Cliente (
    idCliente SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    dataNascimento DATE,
    endereco VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Funcionario (
    idFunc SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cargo VARCHAR(50),
    salario NUMERIC(10, 2)
);

CREATE TABLE IF NOT EXISTS Produto (
    idProduto SERIAL PRIMARY KEY,
    nomeProduto VARCHAR(100) NOT NULL,
    preco NUMERIC(10, 2),
    custo NUMERIC(10, 2),
    idEstoque INT,
    FOREIGN KEY (idEstoque) REFERENCES Estoque(idEstoque)
);

CREATE TABLE IF NOT EXISTS Venda (
    idVenda SERIAL PRIMARY KEY,
    dataVenda DATE,
    idCliente INT NOT NULL,
    FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente)
);

CREATE TABLE IF NOT EXISTS Usuario (
    idUsuario SERIAL PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    nivelAcesso VARCHAR(50),
    idFunc INT UNIQUE NOT NULL,
    FOREIGN KEY (idFunc) REFERENCES Funcionario(idFunc)
);

CREATE TABLE IF NOT EXISTS Animal (
    idAnimal SERIAL PRIMARY KEY,
    nome VARCHAR(100),
    raca VARCHAR(50),
    especie VARCHAR(50),
    dataNasc DATE,
    dataAdmissao DATE,
    motivo VARCHAR(255),
    idCliente INT NOT NULL,
    idFunc INT NOT NULL,
    FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente),
    FOREIGN KEY (idFunc) REFERENCES Funcionario(idFunc)
);

CREATE TABLE IF NOT EXISTS Servico (
    idServico SERIAL PRIMARY KEY,
    valor NUMERIC(10, 2),
    tipoServico VARCHAR(100),
    dataTransacao DATE,
    idCliente INT NOT NULL,
    idUsuario INT NOT NULL,
    FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);

CREATE TABLE IF NOT EXISTS Fornecedor_Produto_Compra (
    idFornecedor INT,
    idProduto INT,
    data DATE,
    qtd INT,
    PRIMARY KEY (idFornecedor, idProduto, data),
    FOREIGN KEY (idFornecedor) REFERENCES Fornecedor(idFornecedor),
    FOREIGN KEY (idProduto) REFERENCES Produto(idProduto)
);

CREATE TABLE IF NOT EXISTS Venda_Produto_Contem (
    idVenda INT,
    idProduto INT,
    valorUnit NUMERIC(10, 2),
    qtd INT,
    PRIMARY KEY (idVenda, idProduto),
    FOREIGN KEY (idVenda) REFERENCES Venda(idVenda),
    FOREIGN KEY (idProduto) REFERENCES Produto(idProduto)
);

CREATE TABLE IF NOT EXISTS Usuario_Estoque_Administra (
    idUsuario INT,
    idEstoque INT,
    PRIMARY KEY (idUsuario, idEstoque),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario),
    FOREIGN KEY (idEstoque) REFERENCES Estoque(idEstoque)
);

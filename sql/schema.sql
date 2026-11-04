CREATE TABLE cliente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    cpf_cnpj VARCHAR(20) NOT NULL UNIQUE,
    telefone VARCHAR(20),
    email VARCHAR(120),
    endereco VARCHAR(200),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE veiculo (
    id SERIAL PRIMARY KEY,
    placa VARCHAR(10) NOT NULL UNIQUE,
    marca VARCHAR(60) NOT NULL,
    modelo VARCHAR(60) NOT NULL,
    ano INT,
    quilometragem NUMERIC(10,1),
    cliente_id INT NOT NULL REFERENCES cliente(id)
);

CREATE TABLE mecanico (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    especialidade VARCHAR(80),
    contato VARCHAR(80)
);

CREATE TABLE peca (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    descricao VARCHAR(120) NOT NULL,
    valor_unitario NUMERIC(12,2) NOT NULL CHECK (valor_unitario >= 0),
    quantidade INT NOT NULL CHECK (quantidade >= 0),
    estoque_minimo INT NOT NULL DEFAULT 0,
    fornecedor VARCHAR(120)
);

CREATE TABLE ordem_servico (
    id SERIAL PRIMARY KEY,
    data_abertura DATE NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT NOW(),
    status VARCHAR(20) NOT NULL,
    politica_desconto VARCHAR(40) NOT NULL DEFAULT 'SEM',
    valor_total NUMERIC(12,2) NOT NULL,
    cliente_id INT NOT NULL REFERENCES cliente(id),
    veiculo_id INT NOT NULL REFERENCES veiculo(id)
);

CREATE TABLE os_mecanico (
    os_id INT NOT NULL REFERENCES ordem_servico(id) ON DELETE CASCADE,
    mecanico_id INT NOT NULL REFERENCES mecanico(id),
    PRIMARY KEY (os_id, mecanico_id)
);

-- Item polimorfico (OCP): tipo define como v1/v2/quantidade sao lidos.
-- PECA: peca_id, quantidade | MAO_DE_OBRA: descricao, v1=horas, v2=valor/hora
-- SERVICO_FIXO: descricao, v1=valor | DIAGNOSTICO: v1=taxa fixa, v2=valor/sistema, quantidade=sistemas
CREATE TABLE os_item (
    id SERIAL PRIMARY KEY,
    os_id INT NOT NULL REFERENCES ordem_servico(id) ON DELETE CASCADE,
    tipo VARCHAR(20) NOT NULL,
    peca_id INT REFERENCES peca(id),
    descricao VARCHAR(200),
    quantidade INT,
    v1 NUMERIC(12,2),
    v2 NUMERIC(12,2)
);

-- Índices para as consultas mais frequentes (RNF07).
CREATE INDEX idx_veiculo_cliente ON veiculo(cliente_id);
CREATE INDEX idx_os_cliente ON ordem_servico(cliente_id);
CREATE INDEX idx_os_veiculo ON ordem_servico(veiculo_id);
CREATE INDEX idx_os_status ON ordem_servico(status);
CREATE INDEX idx_os_abertura ON ordem_servico(data_abertura);
CREATE INDEX idx_os_item_os ON os_item(os_id);
CREATE INDEX idx_os_mecanico_mec ON os_mecanico(mecanico_id);

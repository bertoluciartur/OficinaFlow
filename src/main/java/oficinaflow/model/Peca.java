package oficinaflow.model;

public class Peca {

    private final int id;
    private final String codigo;
    private final String descricao;
    private final double valorUnitario;
    private int quantidade;
    private final int estoqueMinimo;
    private final String fornecedor;

    public Peca(int id, String codigo, String descricao, double valorUnitario,
                int quantidade, int estoqueMinimo, String fornecedor) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.valorUnitario = valorUnitario;
        this.quantidade = quantidade;
        this.estoqueMinimo = estoqueMinimo;
        this.fornecedor = fornecedor;
    }

    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }
    public double getValorUnitario() { return valorUnitario; }
    public int getQuantidade() { return quantidade; }
    public int getEstoqueMinimo() { return estoqueMinimo; }
    public String getFornecedor() { return fornecedor; }

    public boolean possuiEstoque(int qtd) {
        return quantidade >= qtd;
    }

    public void baixar(int qtd) {
        if (!possuiEstoque(qtd)) {
            throw new IllegalStateException("Estoque insuficiente da peça " + codigo);
        }
        quantidade -= qtd;
    }

    public boolean estaAbaixoDoMinimo() {
        return quantidade <= estoqueMinimo;
    }

    @Override
    public String toString() { return id + " - " + codigo + " " + descricao; }
}

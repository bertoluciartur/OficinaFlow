package oficinaflow.model;

public class ItemPeca implements ItemCobravel {

    private final Peca peca;
    private final int quantidade;

    public ItemPeca(Peca peca, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        this.peca = peca;
        this.quantidade = quantidade;
    }

    public Peca getPeca() { return peca; }
    public int getQuantidade() { return quantidade; }

    @Override
    public String getDescricao() {
        return "Peça: " + peca.getDescricao() + " x" + quantidade;
    }

    @Override
    public double calcularValor() {
        return peca.getValorUnitario() * quantidade;
    }

    @Override
    public String getTipo() { return "PECA"; }

    @Override
    public Object[] dados() { return new Object[]{peca.getId(), null, quantidade, null, null}; }
}

package oficinaflow.model;

public class ItemServicoFixo implements ItemCobravel {

    private final String descricao;
    private final double valor;

    public ItemServicoFixo(String descricao, double valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("Valor do serviço não pode ser negativo.");
        }
        this.descricao = descricao;
        this.valor = valor;
    }

    @Override
    public String getDescricao() {
        return "Serviço: " + descricao;
    }

    @Override
    public double calcularValor() {
        return valor;
    }

    @Override
    public String getTipo() { return "SERVICO_FIXO"; }

    @Override
    public Object[] dados() { return new Object[]{null, descricao, null, valor, null}; }
}

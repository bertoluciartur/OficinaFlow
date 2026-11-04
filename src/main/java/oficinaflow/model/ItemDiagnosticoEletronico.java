package oficinaflow.model;

public class ItemDiagnosticoEletronico implements ItemCobravel {

    private final double taxaFixa;
    private final double valorPorSistemaAnalisado;
    private final int sistemasAnalisados;

    public ItemDiagnosticoEletronico(double taxaFixa, double valorPorSistema, int sistemas) {
        if (taxaFixa < 0 || valorPorSistema < 0 || sistemas <= 0) {
            throw new IllegalArgumentException("Valores do diagnóstico inválidos.");
        }
        this.taxaFixa = taxaFixa;
        this.valorPorSistemaAnalisado = valorPorSistema;
        this.sistemasAnalisados = sistemas;
    }

    @Override
    public String getDescricao() {
        return "Diagnóstico eletrônico (" + sistemasAnalisados + " sistemas)";
    }

    @Override
    public double calcularValor() {
        return taxaFixa + valorPorSistemaAnalisado * sistemasAnalisados;
    }

    @Override
    public String getTipo() { return "DIAGNOSTICO"; }

    @Override
    public Object[] dados() {
        return new Object[]{null, null, sistemasAnalisados, taxaFixa, valorPorSistemaAnalisado};
    }
}

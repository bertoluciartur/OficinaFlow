package oficinaflow.model;

public class ItemMaoDeObra implements ItemCobravel {

    private final String descricao;
    private final double horas;
    private final double valorHora;

    public ItemMaoDeObra(String descricao, double horas, double valorHora) {
        if (horas <= 0 || valorHora < 0) {
            throw new IllegalArgumentException("Horas devem ser maiores que zero e valor/hora não pode ser negativo.");
        }
        this.descricao = descricao;
        this.horas = horas;
        this.valorHora = valorHora;
    }

    @Override
    public String getDescricao() {
        return "Mão de obra: " + descricao + " (" + horas + "h)";
    }

    @Override
    public double calcularValor() {
        return horas * valorHora;
    }

    @Override
    public String getTipo() { return "MAO_DE_OBRA"; }

    @Override
    public Object[] dados() { return new Object[]{null, descricao, null, horas, valorHora}; }
}

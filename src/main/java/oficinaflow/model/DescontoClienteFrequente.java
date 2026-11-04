package oficinaflow.model;

public class DescontoClienteFrequente implements PoliticaDesconto {

    @Override
    public String getNome() { return "Cliente frequente (5%)"; }

    @Override
    public double calcular(double subtotal) { return subtotal * 0.05; }

    @Override
    public String getCodigo() { return "FREQUENTE"; }
}

package oficinaflow.model;

public class DescontoClienteVip implements PoliticaDesconto {

    @Override
    public String getNome() { return "Cliente VIP (10%)"; }

    @Override
    public double calcular(double subtotal) { return subtotal * 0.10; }

    @Override
    public String getCodigo() { return "VIP"; }
}

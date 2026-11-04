package oficinaflow.model;

public class SemDesconto implements PoliticaDesconto {

    @Override
    public String getNome() { return "Sem desconto"; }

    @Override
    public double calcular(double subtotal) { return 0; }

    @Override
    public String getCodigo() { return "SEM"; }
}

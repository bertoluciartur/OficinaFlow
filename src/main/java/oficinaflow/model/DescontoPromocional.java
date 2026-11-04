package oficinaflow.model;

public class DescontoPromocional implements PoliticaDesconto {

    private final double percentual;
    private final double tetoDoDesconto;

    public DescontoPromocional(double percentual, double tetoDoDesconto) {
        this.percentual = percentual;
        this.tetoDoDesconto = tetoDoDesconto;
    }

    @Override
    public String getNome() {
        return "Promoção (" + (int) (percentual * 100) + "%, teto R$ " + tetoDoDesconto + ")";
    }

    @Override
    public double calcular(double subtotal) {
        return Math.min(subtotal * percentual, tetoDoDesconto);
    }

    @Override
    public String getCodigo() { return "PROMO:" + percentual + ":" + tetoDoDesconto; }
}

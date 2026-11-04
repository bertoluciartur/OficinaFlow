package oficinaflow.model;

/** Reconstrói a política a partir do código salvo no banco. Único ponto a editar ao criar uma nova política. */
public final class PoliticasDesconto {

    private PoliticasDesconto() { }

    public static PoliticaDesconto de(String codigo) {
        if (codigo.startsWith("PROMO:")) {
            String[] p = codigo.split(":");
            return new DescontoPromocional(Double.parseDouble(p[1]), Double.parseDouble(p[2]));
        }
        switch (codigo) {
            case "FREQUENTE": return new DescontoClienteFrequente();
            case "VIP": return new DescontoClienteVip();
            default: return new SemDesconto();
        }
    }
}

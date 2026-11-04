package oficinaflow.model;

/** Contrato OCP: novas políticas entram criando uma classe e registrando-a em PoliticasDesconto. */
public interface PoliticaDesconto {
    String getNome();
    double calcular(double subtotal);

    /** Código persistido em ordem_servico.politica_desconto. */
    String getCodigo();
}

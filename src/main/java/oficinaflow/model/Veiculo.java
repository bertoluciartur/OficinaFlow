package oficinaflow.model;

public class Veiculo {

    private final int id;
    private final String placa;
    private final String marca;
    private final String modelo;
    private final int ano;
    private final double quilometragem;
    private final Cliente proprietario;

    public Veiculo(int id, String placa, String marca, String modelo, int ano,
                   double quilometragem, Cliente proprietario) {
        this.id = id;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.quilometragem = quilometragem;
        this.proprietario = proprietario;
    }

    public int getId() { return id; }
    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public int getAno() { return ano; }
    public double getQuilometragem() { return quilometragem; }
    public Cliente getProprietario() { return proprietario; }

    @Override
    public String toString() { return id + " - " + placa + " (" + modelo + ")"; }
}

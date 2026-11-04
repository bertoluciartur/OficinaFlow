package oficinaflow.model;

public class Mecanico {

    private final int id;
    private final String nome;
    private final String especialidade;
    private final String contato;

    public Mecanico(int id, String nome, String especialidade, String contato) {
        this.id = id;
        this.nome = nome;
        this.especialidade = especialidade;
        this.contato = contato;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEspecialidade() { return especialidade; }
    public String getContato() { return contato; }

    @Override
    public boolean equals(Object o) { return o instanceof Mecanico m && m.id == id; }

    @Override
    public int hashCode() { return Integer.hashCode(id); }

    @Override
    public String toString() { return id + " - " + nome; }
}

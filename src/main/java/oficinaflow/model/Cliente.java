package oficinaflow.model;

public class Cliente {

    private final int id;
    private final String nome;
    private final String cpfCnpj;
    private final String telefone;
    private final String email;
    private final String endereco;
    private final boolean ativo;

    public Cliente(int id, String nome, String cpfCnpj, String telefone, String email, String endereco,
                   boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.ativo = ativo;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCpfCnpj() { return cpfCnpj; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public String getEndereco() { return endereco; }
    public boolean isAtivo() { return ativo; }

    @Override
    public String toString() { return id + " - " + nome; }
}

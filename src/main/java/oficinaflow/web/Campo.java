package oficinaflow.web;

import java.util.List;

public class Campo {

    private final String nome;
    private final String rotulo;
    private final boolean obrigatorio;
    private final List<String> opcoes;

    public Campo(String nome, String rotulo, boolean obrigatorio) {
        this(nome, rotulo, obrigatorio, null);
    }

    /** Com {@code opcoes} o campo vira uma lista de seleção. */
    public Campo(String nome, String rotulo, boolean obrigatorio, List<String> opcoes) {
        this.nome = nome;
        this.rotulo = rotulo;
        this.obrigatorio = obrigatorio;
        this.opcoes = opcoes;
    }

    public String getNome() { return nome; }
    public String getRotulo() { return rotulo; }
    public boolean isObrigatorio() { return obrigatorio; }
    public List<String> getOpcoes() { return opcoes; }
}

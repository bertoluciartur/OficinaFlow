package oficinaflow.model;

public enum StatusOrdemServico {
    ABERTA("Aberta"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusOrdemServico(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean podeTransicionarPara(StatusOrdemServico novo) {
        switch (this) {
            case ABERTA:
                return novo == EM_ANDAMENTO || novo == CANCELADA;
            case EM_ANDAMENTO:
                return novo == CONCLUIDA || novo == CANCELADA;
            default:
                return false;
        }
    }

    @Override
    public String toString() { return descricao; }
}

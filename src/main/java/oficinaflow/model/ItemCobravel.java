package oficinaflow.model;

/** Contrato OCP: novos tipos de item entram criando uma classe e registrando-a em ItemFactory. */
public interface ItemCobravel {
    String getDescricao();
    double calcularValor();

    String getTipo();

    /** Dados para persistência: {pecaId, descricao, quantidade, v1, v2}; ver sql/schema.sql. */
    Object[] dados();
}

package oficinaflow.model;

/** Reconstrói itens a partir do banco. Único ponto a editar ao criar um novo tipo de item. */
public final class ItemFactory {

    private ItemFactory() { }

    public static ItemCobravel criar(String tipo, Peca peca, String descricao, Integer qtd, Double v1, Double v2) {
        switch (tipo) {
            case "PECA": return new ItemPeca(peca, qtd);
            case "MAO_DE_OBRA": return new ItemMaoDeObra(descricao, v1, v2);
            case "SERVICO_FIXO": return new ItemServicoFixo(descricao, v1);
            case "DIAGNOSTICO": return new ItemDiagnosticoEletronico(v1, v2, qtd);
            default: throw new IllegalArgumentException("Tipo de item desconhecido: " + tipo);
        }
    }
}

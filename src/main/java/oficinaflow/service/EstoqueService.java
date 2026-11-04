package oficinaflow.service;

import java.util.ArrayList;
import java.util.List;
import oficinaflow.model.ItemCobravel;
import oficinaflow.model.ItemPeca;
import oficinaflow.model.Peca;

@org.springframework.stereotype.Service
public class EstoqueService {

    /** Baixa as peças dos itens em memória e devolve as que ficaram no estoque mínimo (RF09, RF10). */
    public List<Peca> baixarEstoque(List<ItemCobravel> itens) {
        for (ItemCobravel item : itens) {
            if (item instanceof ItemPeca ip && !ip.getPeca().possuiEstoque(ip.getQuantidade())) {
                throw new IllegalStateException("Estoque insuficiente da peça " + ip.getPeca().getCodigo());
            }
        }
        List<Peca> alertas = new ArrayList<>();
        for (ItemCobravel item : itens) {
            if (item instanceof ItemPeca ip) {
                ip.getPeca().baixar(ip.getQuantidade());
                if (ip.getPeca().estaAbaixoDoMinimo() && !alertas.contains(ip.getPeca())) {
                    alertas.add(ip.getPeca());
                }
            }
        }
        return alertas;
    }
}

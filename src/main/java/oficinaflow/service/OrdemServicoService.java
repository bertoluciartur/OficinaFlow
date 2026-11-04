package oficinaflow.service;

import java.sql.SQLException;
import java.util.List;
import oficinaflow.dao.OrdemServicoDAO;
import oficinaflow.model.ItemCobravel;
import oficinaflow.model.OrdemServico;
import oficinaflow.model.Peca;
import oficinaflow.model.StatusOrdemServico;
import org.springframework.stereotype.Service;

/** Orquestra as regras de negócio; cada passo mora em sua própria classe (SRP). */
@Service
public class OrdemServicoService {

    private final ValidadorOrdemServico validador;
    private final EstoqueService estoque;
    private final OrdemServicoDAO dao;

    public OrdemServicoService(ValidadorOrdemServico validador, EstoqueService estoque, OrdemServicoDAO dao) {
        this.validador = validador;
        this.estoque = estoque;
        this.dao = dao;
    }

    /** Valida, salva e baixa o estoque; devolve as peças que atingiram o mínimo. */
    public List<Peca> abrir(OrdemServico os) throws SQLException {
        validador.validar(os);
        List<Peca> alertas = estoque.baixarEstoque(os.getItens());
        dao.salvar(os);
        return alertas;
    }

    /** Itens extras só entram em OS aberta ou em andamento (RF12); devolve as peças no estoque mínimo. */
    public List<Peca> adicionarItens(OrdemServico os, List<ItemCobravel> novos) throws SQLException {
        if (os.getStatus() != StatusOrdemServico.ABERTA && os.getStatus() != StatusOrdemServico.EM_ANDAMENTO) {
            throw new IllegalStateException("Só é possível adicionar itens a OS aberta ou em andamento.");
        }
        if (novos.isEmpty()) {
            throw new IllegalArgumentException("Adicione ao menos um item (peça ou serviço).");
        }
        List<Peca> alertas = estoque.baixarEstoque(novos);
        novos.forEach(os::adicionarItem);
        dao.adicionarItens(os, novos);
        return alertas;
    }

    /** Cancelar devolve ao estoque as peças da OS. */
    public void atualizarStatus(OrdemServico os, StatusOrdemServico novo) throws SQLException {
        os.atualizarStatus(novo);
        dao.atualizarStatus(os, novo == StatusOrdemServico.CANCELADA);
    }
}

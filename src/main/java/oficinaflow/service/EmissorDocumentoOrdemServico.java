package oficinaflow.service;

import oficinaflow.model.OrdemServico;
import oficinaflow.model.StatusOrdemServico;

/** Regras de emissão (RF16); a apresentação do documento fica no template doc.html. */
@org.springframework.stereotype.Service
public class EmissorDocumentoOrdemServico {

    /** Devolve o título do documento; a nota de fechamento só existe para OS concluída. */
    public String titulo(OrdemServico os, boolean nota) {
        if (nota && os.getStatus() != StatusOrdemServico.CONCLUIDA) {
            throw new IllegalStateException("A nota só pode ser emitida para OS concluída.");
        }
        return nota ? "Nota de Fechamento" : "Orçamento";
    }
}

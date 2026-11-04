package oficinaflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import oficinaflow.model.DescontoPromocional;
import oficinaflow.model.ItemDiagnosticoEletronico;
import oficinaflow.model.ItemMaoDeObra;
import oficinaflow.model.ItemPeca;
import oficinaflow.model.ItemServicoFixo;
import oficinaflow.model.OrdemServico;
import oficinaflow.model.Peca;
import oficinaflow.model.StatusOrdemServico;
import org.junit.jupiter.api.Test;

class OrdemServicoTest {

    @Test
    void totalComItensNovosEDescontoPromocionalComTeto() {
        OrdemServico os = new OrdemServico(null, null, null);
        os.adicionarItem(new ItemPeca(new Peca(1, "PF-200", "Pastilha", 120.0, 10, 2, "X"), 2)); // 240
        os.adicionarItem(new ItemMaoDeObra("Troca", 1.5, 90.0)); // 135
        os.adicionarItem(new ItemServicoFixo("Alinhamento", 80.0)); // 80
        os.adicionarItem(new ItemDiagnosticoEletronico(50.0, 15.0, 4)); // 110
        os.definirPoliticaDesconto(new DescontoPromocional(0.20, 60.0)); // 20% de 565 = 113 -> teto 60
        assertEquals(565.0, os.calcularSubtotal(), 0.001);
        assertEquals(60.0, os.calcularDesconto(), 0.001);
        assertEquals(505.0, os.calcularValorTotal(), 0.001);
    }

    @Test
    void statusSoAvancaPorTransicaoValida() {
        OrdemServico os = new OrdemServico(null, null, null);
        os.atualizarStatus(StatusOrdemServico.EM_ANDAMENTO);
        os.atualizarStatus(StatusOrdemServico.CONCLUIDA);
        assertThrows(IllegalStateException.class, () -> os.atualizarStatus(StatusOrdemServico.ABERTA));
    }

    @Test
    void baixaDeEstoqueInsuficienteFalha() {
        Peca p = new Peca(1, "FO-100", "Filtro", 35.0, 2, 1, "X");
        assertThrows(IllegalStateException.class, () -> p.baixar(3));
    }
}

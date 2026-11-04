package oficinaflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import oficinaflow.model.Cliente;
import oficinaflow.model.ItemCobravel;
import oficinaflow.model.ItemPeca;
import oficinaflow.model.Mecanico;
import oficinaflow.model.OrdemServico;
import oficinaflow.model.Peca;
import oficinaflow.model.Veiculo;
import oficinaflow.service.EstoqueService;
import oficinaflow.service.ValidadorOrdemServico;
import org.junit.jupiter.api.Test;

class RegrasTest {

    @Test
    void clienteInativoNaoAbreOrdem() {
        Cliente inativo = new Cliente(1, "Ana", "123.456.789-00", "", "", "", false);
        Veiculo v = new Veiculo(1, "ABC-1234", "VW", "Gol", 2020, 0, inativo);
        OrdemServico os = new OrdemServico(inativo, v, new Mecanico(1, "Carlos", "", ""));
        assertThrows(IllegalArgumentException.class, () -> new ValidadorOrdemServico().validar(os));
    }

    @Test
    void baixaAvisaQuandoAtingeEstoqueMinimo() {
        Peca filtro = new Peca(1, "FO-100", "Filtro", 35.0, 5, 3, "X");
        List<ItemCobravel> itens = List.of(new ItemPeca(filtro, 2));
        List<Peca> alertas = new EstoqueService().baixarEstoque(itens);
        assertEquals(3, filtro.getQuantidade());
        assertEquals(List.of(filtro), alertas);
    }

    @Test
    void baixaMaiorQueOEstoqueFalhaSemAlterarNada() {
        Peca filtro = new Peca(1, "FO-100", "Filtro", 35.0, 2, 1, "X");
        List<ItemCobravel> itens = List.of(new ItemPeca(filtro, 3));
        assertThrows(IllegalStateException.class, () -> new EstoqueService().baixarEstoque(itens));
        assertEquals(2, filtro.getQuantidade());
    }
}

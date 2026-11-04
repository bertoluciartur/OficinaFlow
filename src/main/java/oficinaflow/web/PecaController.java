package oficinaflow.web;

import java.util.ArrayList;
import java.util.List;
import oficinaflow.dao.PecaDAO;
import oficinaflow.model.Peca;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pecas")
public class PecaController extends CrudController {

    private final PecaDAO dao;

    public PecaController(PecaDAO dao) {
        this.dao = dao;
    }

    @Override
    protected String base() { return "pecas"; }

    @Override
    protected String titulo() { return "Peças e Estoque"; }

    @Override
    protected List<Campo> campos() {
        return List.of(new Campo("codigo", "Código", true), new Campo("descricao", "Descrição", true),
            new Campo("valor", "Valor unitário", true), new Campo("quantidade", "Quantidade", true),
            new Campo("minimo", "Estoque mínimo", true), new Campo("fornecedor", "Fornecedor", false));
    }

    @Override
    protected List<Object[]> carregar() throws Exception {
        List<Object[]> linhas = new ArrayList<>();
        for (Peca p : dao.listar()) {
            // vírgula decimal: o formulário de edição já aceita esse formato (gravar() troca ',' por '.')
            linhas.add(new Object[]{p.getId(), p.getCodigo(), p.getDescricao(),
                String.format(java.util.Locale.forLanguageTag("pt-BR"), "%.2f", p.getValorUnitario()),
                p.getQuantidade(), p.getEstoqueMinimo(), p.getFornecedor()});
        }
        return linhas;
    }

    @Override
    protected void gravar(int id, String[] v) throws Exception {
        double valor = Double.parseDouble(v[2].replace(',', '.'));
        int qtd = Integer.parseInt(v[3]);
        int minimo = Integer.parseInt(v[4]);
        if (valor < 0 || qtd < 0 || minimo < 0) {
            throw new IllegalArgumentException("Valor, quantidade e estoque mínimo não podem ser negativos.");
        }
        dao.salvar(new Peca(id, v[0], v[1], valor, qtd, minimo, v[5]));
    }

    @Override
    protected void remover(int id) throws Exception {
        dao.excluir(id);
    }

    /** RF10 e RF19: aviso permanente no topo da página. */
    @Override
    protected String alerta() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (Peca p : dao.listar()) {
            if (p.estaAbaixoDoMinimo()) {
                sb.append(sb.length() == 0 ? "Estoque mínimo atingido: " : "; ")
                    .append(p.getCodigo()).append(" (").append(p.getQuantidade())
                    .append("/").append(p.getEstoqueMinimo()).append(")");
            }
        }
        return sb.length() == 0 ? null : sb.toString();
    }
}

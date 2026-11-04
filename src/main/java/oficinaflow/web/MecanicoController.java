package oficinaflow.web;

import java.util.ArrayList;
import java.util.List;
import oficinaflow.dao.MecanicoDAO;
import oficinaflow.model.Mecanico;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mecanicos")
public class MecanicoController extends CrudController {

    private final MecanicoDAO dao;

    public MecanicoController(MecanicoDAO dao) {
        this.dao = dao;
    }

    @Override
    protected String base() { return "mecanicos"; }

    @Override
    protected String titulo() { return "Mecânicos"; }

    @Override
    protected List<Campo> campos() {
        return List.of(new Campo("nome", "Nome", true), new Campo("especialidade", "Especialidade", false),
            new Campo("contato", "Contato", false));
    }

    @Override
    protected List<Object[]> carregar() throws Exception {
        List<Object[]> linhas = new ArrayList<>();
        for (Mecanico m : dao.listar()) {
            linhas.add(new Object[]{m.getId(), m.getNome(), m.getEspecialidade(), m.getContato()});
        }
        return linhas;
    }

    @Override
    protected void gravar(int id, String[] v) throws Exception {
        dao.salvar(new Mecanico(id, v[0], v[1], v[2]));
    }

    @Override
    protected void remover(int id) throws Exception {
        dao.excluir(id);
    }
}

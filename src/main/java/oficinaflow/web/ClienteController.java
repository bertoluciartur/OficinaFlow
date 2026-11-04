package oficinaflow.web;

import java.util.ArrayList;
import java.util.List;
import oficinaflow.dao.ClienteDAO;
import oficinaflow.model.Cliente;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/clientes")
public class ClienteController extends CrudController {

    private final ClienteDAO dao;

    public ClienteController(ClienteDAO dao) {
        this.dao = dao;
    }

    @Override
    protected String base() { return "clientes"; }

    @Override
    protected String titulo() { return "Clientes"; }

    @Override
    protected List<Campo> campos() {
        return List.of(new Campo("nome", "Nome", true), new Campo("cpf", "CPF/CNPJ", true),
            new Campo("telefone", "Telefone", false), new Campo("email", "E-mail", false),
            new Campo("endereco", "Endereço", false),
            new Campo("situacao", "Situação", false, List.of("Ativo", "Inativo")));
    }

    @Override
    protected List<Object[]> carregar() throws Exception {
        List<Object[]> linhas = new ArrayList<>();
        for (Cliente c : dao.listar()) {
            linhas.add(new Object[]{c.getId(), c.getNome(), c.getCpfCnpj(), c.getTelefone(), c.getEmail(),
                c.getEndereco(), c.isAtivo() ? "Ativo" : "Inativo"});
        }
        return linhas;
    }

    @Override
    protected void gravar(int id, String[] v) throws Exception {
        int digitos = v[1].replaceAll("\\D", "").length();
        if (digitos != 11 && digitos != 14) {
            throw new IllegalArgumentException("CPF/CNPJ deve ter 11 ou 14 dígitos.");
        }
        if (!v[3].isEmpty() && !v[3].matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        dao.salvar(new Cliente(id, v[0], v[1], v[2], v[3], v[4], !v[5].equals("Inativo")));
    }

    @Override
    protected void remover(int id) throws Exception {
        dao.excluir(id);
    }
}

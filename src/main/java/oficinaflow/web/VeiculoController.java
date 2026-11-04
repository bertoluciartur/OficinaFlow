package oficinaflow.web;

import java.util.ArrayList;
import java.util.List;
import oficinaflow.dao.ClienteDAO;
import oficinaflow.dao.VeiculoDAO;
import oficinaflow.model.Cliente;
import oficinaflow.model.Veiculo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/veiculos")
public class VeiculoController extends CrudController {

    private final VeiculoDAO dao;
    private final ClienteDAO clientes;

    public VeiculoController(VeiculoDAO dao, ClienteDAO clientes) {
        this.dao = dao;
        this.clientes = clientes;
    }

    @Override
    protected String base() { return "veiculos"; }

    @Override
    protected String titulo() { return "Veículos"; }

    @Override
    protected List<Campo> campos() throws Exception {
        List<String> donos = new ArrayList<>();
        for (Cliente c : clientes.listar()) {
            donos.add(c.toString());
        }
        return List.of(new Campo("placa", "Placa", true), new Campo("marca", "Marca", true),
            new Campo("modelo", "Modelo", true), new Campo("cliente", "Cliente", true, donos),
            new Campo("ano", "Ano", false), new Campo("km", "Quilometragem", false));
    }

    @Override
    protected List<Object[]> carregar() throws Exception {
        List<Object[]> linhas = new ArrayList<>();
        for (Veiculo v : dao.listar()) {
            linhas.add(new Object[]{v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(), v.getProprietario(),
                v.getAno(), v.getQuilometragem()});
        }
        return linhas;
    }

    @Override
    protected void gravar(int id, String[] v) throws Exception {
        String placa = v[0].toUpperCase();
        if (!placa.matches("[A-Z]{3}-?[0-9][A-Z0-9][0-9]{2}")) {
            throw new IllegalArgumentException("Placa inválida (ex.: ABC-1234 ou ABC1D23).");
        }
        int ano = v[4].isEmpty() ? 0 : Integer.parseInt(v[4]);
        double km = v[5].isEmpty() ? 0 : Double.parseDouble(v[5].replace(',', '.'));
        if (ano < 0 || km < 0) {
            throw new IllegalArgumentException("Ano e quilometragem não podem ser negativos.");
        }
        dao.salvar(new Veiculo(id, placa, v[1], v[2], ano, km, clientes.buscar(idDe(v[3]))));
    }

    @Override
    protected void remover(int id) throws Exception {
        dao.excluir(id);
    }
}

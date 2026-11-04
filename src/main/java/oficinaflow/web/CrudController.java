package oficinaflow.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Cadastro genérico (lista + formulário na mesma página). Cada entidade só descreve seus campos. */
public abstract class CrudController {

    protected abstract String base();

    protected abstract String titulo();

    protected abstract List<Campo> campos() throws Exception;

    /** Linhas da tabela: ID primeiro, depois um valor por campo, na ordem de campos(). */
    protected abstract List<Object[]> carregar() throws Exception;

    /** id == 0 indica novo registro. */
    protected abstract void gravar(int id, String[] valores) throws Exception;

    protected abstract void remover(int id) throws Exception;

    protected String alerta() throws Exception { return null; }

    protected static int idDe(String itemSelecao) {
        return Integer.parseInt(itemSelecao.split(" - ")[0]);
    }

    @GetMapping
    public String listar(Model m) {
        return pagina(m, 0);
    }

    @GetMapping("/{id}")
    public String editar(@PathVariable int id, Model m) {
        return pagina(m, id);
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam Map<String, String> p, RedirectAttributes ra) {
        try {
            List<Campo> campos = campos();
            String[] v = new String[campos.size()];
            for (int i = 0; i < v.length; i++) {
                v[i] = p.getOrDefault(campos.get(i).getNome(), "").trim();
                if (campos.get(i).isObrigatorio() && v[i].isEmpty()) {
                    throw new IllegalArgumentException("Preencha o campo: " + campos.get(i).getRotulo());
                }
            }
            gravar(Integer.parseInt(p.getOrDefault("id", "0")), v);
            ra.addFlashAttribute("ok", "Registro salvo com sucesso.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", Mensagens.de(e));
        }
        return "redirect:/" + base();
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable int id, RedirectAttributes ra) {
        try {
            remover(id);
            ra.addFlashAttribute("ok", "Registro excluído com sucesso.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", Mensagens.de(e));
        }
        return "redirect:/" + base();
    }

    private String pagina(Model m, int id) {
        m.addAttribute("titulo", titulo());
        m.addAttribute("base", base());
        m.addAttribute("id", id);
        m.addAttribute("campos", List.of());
        m.addAttribute("linhas", List.of());
        m.addAttribute("valores", List.of());
        try {
            List<Campo> campos = campos();
            List<Object[]> linhas = carregar();
            List<String> valores = new ArrayList<>();
            for (int i = 0; i < campos.size(); i++) {
                valores.add("");
            }
            for (Object[] l : linhas) {
                if (((Number) l[0]).intValue() == id) {
                    for (int i = 0; i < valores.size(); i++) {
                        valores.set(i, l[i + 1] == null ? "" : l[i + 1].toString());
                    }
                }
            }
            m.addAttribute("campos", campos);
            m.addAttribute("linhas", linhas);
            m.addAttribute("valores", valores);
            m.addAttribute("alerta", alerta());
        } catch (Exception e) {
            m.addAttribute("erro", Mensagens.de(e));
        }
        return "crud";
    }
}

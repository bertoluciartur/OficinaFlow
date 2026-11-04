package oficinaflow.web;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import oficinaflow.dao.ClienteDAO;
import oficinaflow.dao.MecanicoDAO;
import oficinaflow.dao.OrdemServicoDAO;
import oficinaflow.dao.PecaDAO;
import oficinaflow.dao.VeiculoDAO;
import oficinaflow.model.Cliente;
import oficinaflow.model.DescontoClienteFrequente;
import oficinaflow.model.DescontoClienteVip;
import oficinaflow.model.DescontoPromocional;
import oficinaflow.model.ItemCobravel;
import oficinaflow.model.ItemFactory;
import oficinaflow.model.Mecanico;
import oficinaflow.model.OrdemServico;
import oficinaflow.model.Peca;
import oficinaflow.model.PoliticaDesconto;
import oficinaflow.model.PoliticasDesconto;
import oficinaflow.model.SemDesconto;
import oficinaflow.model.StatusOrdemServico;
import oficinaflow.model.Veiculo;
import oficinaflow.service.EmissorDocumentoOrdemServico;
import oficinaflow.service.OrdemServicoService;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Abertura, consulta, status e documentos da OS (RF05, 07, 11-17). */
@Controller
@RequestMapping("/os")
public class OrdemServicoController {

    private static final List<PoliticaDesconto> POLITICAS = List.of(new SemDesconto(),
        new DescontoClienteFrequente(), new DescontoClienteVip(), new DescontoPromocional(0.20, 60.0));

    private final OrdemServicoDAO dao;
    private final OrdemServicoService service;
    private final EmissorDocumentoOrdemServico emissor;
    private final ClienteDAO clientes;
    private final VeiculoDAO veiculos;
    private final MecanicoDAO mecanicos;
    private final PecaDAO pecas;

    public OrdemServicoController(OrdemServicoDAO dao, OrdemServicoService service,
                                  EmissorDocumentoOrdemServico emissor, ClienteDAO clientes,
                                  VeiculoDAO veiculos, MecanicoDAO mecanicos, PecaDAO pecas) {
        this.dao = dao;
        this.service = service;
        this.emissor = emissor;
        this.clientes = clientes;
        this.veiculos = veiculos;
        this.mecanicos = mecanicos;
        this.pecas = pecas;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Integer cliente,
                         @RequestParam(required = false) Integer veiculo,
                         @RequestParam(required = false) Integer mecanico,
                         @RequestParam(required = false) String status,
                         @RequestParam(required = false) String de,
                         @RequestParam(required = false) String ate, Model m) {
        m.addAttribute("ordens", List.of());
        m.addAttribute("statuses", StatusOrdemServico.values());
        try {
            m.addAttribute("clientes", clientes.listar());
            m.addAttribute("veiculos", veiculos.listar());
            m.addAttribute("mecanicos", mecanicos.listar());
            StatusOrdemServico st = vazio(status) ? null : StatusOrdemServico.valueOf(status);
            LocalDate d = vazio(de) ? null : LocalDate.parse(de);
            LocalDate a = vazio(ate) ? null : LocalDate.parse(ate);
            m.addAttribute("ordens", dao.buscar(cliente, veiculo, mecanico, st, d, a));
        } catch (Exception e) {
            m.addAttribute("erro", Mensagens.de(e));
        }
        return "os-lista";
    }

    @GetMapping("/nova")
    public String nova(Model m) {
        try {
            m.addAttribute("clientes", clientes.listarAtivos());
            m.addAttribute("veiculos", veiculos.listar());
            m.addAttribute("mecanicos", mecanicos.listar());
            m.addAttribute("pecas", pecas.listar());
            m.addAttribute("politicas", POLITICAS);
        } catch (Exception e) {
            m.addAttribute("erro", Mensagens.de(e));
        }
        return "os-nova";
    }

    /** Usado pela tela de nova OS para mostrar o desconto em tempo real, com a política real. */
    @GetMapping("/desconto")
    @ResponseBody
    public String desconto(@RequestParam String codigo, @RequestParam double subtotal) {
        return String.valueOf(PoliticasDesconto.de(codigo).calcular(subtotal));
    }

    /** Cada linha de item chega como uma posição em cada lista (tipo, peca, desc, qtd, v1, v2). */
    @PostMapping
    public String abrir(@RequestParam int cliente, @RequestParam int veiculo,
                        @RequestParam(required = false) List<Integer> mecanico, @RequestParam String desconto,
                        @RequestParam MultiValueMap<String, String> p, RedirectAttributes ra) {
        try {
            Cliente c = clientes.buscar(cliente);
            Veiculo v = veiculos.buscar(veiculo);
            List<Mecanico> ms = mecanicos.listar().stream()
                .filter(x -> mecanico != null && mecanico.contains(x.getId())).toList();
            OrdemServico os = new OrdemServico(c, v, ms.isEmpty() ? null : ms.get(0));
            ms.forEach(os::associarMecanico);
            os.definirPoliticaDesconto(PoliticasDesconto.de(desconto));
            lerItens(p).forEach(os::adicionarItem);
            if (os.getItens().isEmpty()) {
                throw new IllegalArgumentException("Adicione ao menos um item (peça ou serviço).");
            }
            List<Peca> alertas = service.abrir(os);
            StringBuilder msg = new StringBuilder("OS #" + os.getId() + " aberta com sucesso.");
            for (Peca baixa : alertas) {
                msg.append(" ALERTA: estoque mínimo atingido -> ").append(baixa.getDescricao())
                    .append(" (restam ").append(baixa.getQuantidade()).append(").");
            }
            ra.addFlashAttribute("ok", msg.toString());
            return "redirect:/os";
        } catch (Exception e) {
            ra.addFlashAttribute("erro", Mensagens.de(e));
            return "redirect:/os/nova";
        }
    }

    /** Tela para acrescentar peças e serviços a uma OS já aberta (RF12). */
    @GetMapping("/{id}/itens")
    public String itens(@PathVariable int id, Model m) {
        try {
            m.addAttribute("os", buscar(id));
            m.addAttribute("pecas", pecas.listar());
        } catch (Exception e) {
            m.addAttribute("erro", Mensagens.de(e));
        }
        return "os-itens";
    }

    @PostMapping("/{id}/itens")
    public String adicionarItens(@PathVariable int id, @RequestParam MultiValueMap<String, String> p,
                                 RedirectAttributes ra) {
        try {
            List<Peca> alertas = service.adicionarItens(buscar(id), lerItens(p));
            StringBuilder msg = new StringBuilder("Itens adicionados à OS #" + id + ".");
            for (Peca baixa : alertas) {
                msg.append(" ALERTA: estoque mínimo atingido -> ").append(baixa.getDescricao())
                    .append(" (restam ").append(baixa.getQuantidade()).append(").");
            }
            ra.addFlashAttribute("ok", msg.toString());
            return "redirect:/os";
        } catch (Exception e) {
            ra.addFlashAttribute("erro", Mensagens.de(e));
            return "redirect:/os/" + id + "/itens";
        }
    }

    @PostMapping("/{id}/status")
    public String status(@PathVariable int id, @RequestParam String status, RedirectAttributes ra) {
        try {
            service.atualizarStatus(buscar(id), StatusOrdemServico.valueOf(status));
            ra.addFlashAttribute("ok", "Status atualizado com sucesso.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", Mensagens.de(e));
        }
        return "redirect:/os";
    }

    /** tipo: "orcamento" ou "nota" (RF16). */
    @GetMapping("/{id}/{tipo}")
    public String documento(@PathVariable int id, @PathVariable String tipo, Model m) {
        try {
            OrdemServico os = buscar(id);
            boolean nota = tipo.equals("nota");
            m.addAttribute("titulo", emissor.titulo(os, nota));
            m.addAttribute("nota", nota);
            m.addAttribute("os", os);
        } catch (Exception e) {
            m.addAttribute("erro", Mensagens.de(e));
        }
        return "doc";
    }

    private OrdemServico buscar(int id) throws Exception {
        OrdemServico os = dao.buscarPorId(id);
        if (os == null) {
            throw new IllegalArgumentException("Ordem de serviço não encontrada.");
        }
        return os;
    }

    /** Monta os itens a partir das linhas do formulário (tipo, peca, desc, qtd, v1, v2); ignora linhas sem tipo. */
    private List<ItemCobravel> lerItens(MultiValueMap<String, String> p) throws Exception {
        List<ItemCobravel> itens = new ArrayList<>();
        List<String> tipos = p.getOrDefault("tipo", List.of());
        for (int i = 0; i < tipos.size(); i++) {
            if (vazio(tipos.get(i))) {
                continue;
            }
            String pc = campo(p, "peca", i), q = campo(p, "qtd", i);
            String a = campo(p, "v1", i), b = campo(p, "v2", i);
            itens.add(ItemFactory.criar(tipos.get(i),
                vazio(pc) ? null : pecas.buscar(Integer.parseInt(pc)), campo(p, "desc", i),
                vazio(q) ? null : Integer.valueOf(q),
                vazio(a) ? null : Double.valueOf(a.replace(',', '.')),
                vazio(b) ? null : Double.valueOf(b.replace(',', '.'))));
        }
        return itens;
    }

    /** Valor da linha i do campo k; o mapa bruto evita que o Spring separe vírgulas ou descarte vazios. */
    private static String campo(MultiValueMap<String, String> p, String k, int i) {
        List<String> l = p.get(k);
        return l != null && i < l.size() ? l.get(i) : null;
    }

    private static boolean vazio(String s) {
        return s == null || s.isBlank();
    }
}

package oficinaflow.web;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import oficinaflow.dao.OrdemServicoDAO;
import oficinaflow.dao.PecaDAO;
import oficinaflow.model.OrdemServico;
import oficinaflow.model.Peca;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Relatórios gerenciais: OS concluídas por período (RF17), estoque baixo (RF19) e visão geral. */
@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    private final OrdemServicoDAO ordens;
    private final PecaDAO pecas;

    public RelatorioController(OrdemServicoDAO ordens, PecaDAO pecas) {
        this.ordens = ordens;
        this.pecas = pecas;
    }

    /** Sem datas, o período é do início do mês até hoje. */
    @GetMapping
    public String relatorio(@RequestParam(required = false) String de,
                            @RequestParam(required = false) String ate, Model m) {
        LocalDate hoje = LocalDate.now();
        m.addAttribute("de", hoje.withDayOfMonth(1));
        m.addAttribute("ate", hoje);
        m.addAttribute("concluidas", List.of());
        m.addAttribute("abaixoMinimo", List.of());
        m.addAttribute("porStatus", Map.of());
        m.addAttribute("faturamento", 0.0);
        m.addAttribute("ticket", 0.0);
        try {
            LocalDate d = de == null || de.isBlank() ? hoje.withDayOfMonth(1) : LocalDate.parse(de);
            LocalDate a = ate == null || ate.isBlank() ? hoje : LocalDate.parse(ate);
            List<OrdemServico> concluidas = ordens.concluidasNoPeriodo(d, a);
            double faturamento = concluidas.stream().mapToDouble(OrdemServico::calcularValorTotal).sum();
            m.addAttribute("de", d);
            m.addAttribute("ate", a);
            m.addAttribute("concluidas", concluidas);
            m.addAttribute("faturamento", faturamento);
            m.addAttribute("ticket", concluidas.isEmpty() ? 0.0 : faturamento / concluidas.size());
            m.addAttribute("porStatus", ordens.contagemPorStatus());
            m.addAttribute("abaixoMinimo", pecas.listar().stream().filter(Peca::estaAbaixoDoMinimo).toList());
        } catch (Exception e) {
            m.addAttribute("erro", Mensagens.de(e));
        }
        return "relatorios";
    }
}

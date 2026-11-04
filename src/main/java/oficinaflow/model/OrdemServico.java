package oficinaflow.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrdemServico {

    private int id;
    private final LocalDate dataAbertura;
    private StatusOrdemServico status;
    private final Cliente cliente;
    private final Veiculo veiculo;
    private final List<Mecanico> mecanicos = new ArrayList<>();
    private final List<ItemCobravel> itens = new ArrayList<>();
    private PoliticaDesconto politicaDesconto = new SemDesconto();

    public OrdemServico(Cliente cliente, Veiculo veiculo, Mecanico responsavel) {
        this(0, LocalDate.now(), StatusOrdemServico.ABERTA, cliente, veiculo);
        if (responsavel != null) {
            mecanicos.add(responsavel);
        }
    }

    /** Usado pelo DAO para reconstruir uma OS já salva. */
    public OrdemServico(int id, LocalDate dataAbertura, StatusOrdemServico status,
                        Cliente cliente, Veiculo veiculo) {
        this.id = id;
        this.dataAbertura = dataAbertura;
        this.status = status;
        this.cliente = cliente;
        this.veiculo = veiculo;
    }

    public void associarMecanico(Mecanico mecanico) {
        if (!mecanicos.contains(mecanico)) {
            mecanicos.add(mecanico);
        }
    }

    public void adicionarItem(ItemCobravel item) {
        itens.add(item);
    }

    public void definirPoliticaDesconto(PoliticaDesconto politica) {
        this.politicaDesconto = politica;
    }

    public double calcularSubtotal() {
        double subtotal = 0;
        for (ItemCobravel item : itens) {
            subtotal += item.calcularValor();
        }
        return subtotal;
    }

    public double calcularDesconto() {
        return politicaDesconto.calcular(calcularSubtotal());
    }

    public double calcularValorTotal() {
        return calcularSubtotal() - calcularDesconto();
    }

    public void atualizarStatus(StatusOrdemServico novoStatus) {
        if (!status.podeTransicionarPara(novoStatus)) {
            throw new IllegalStateException(
                "Transição inválida: " + status.getDescricao() + " -> " + novoStatus.getDescricao());
        }
        this.status = novoStatus;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getDataAbertura() { return dataAbertura; }
    public StatusOrdemServico getStatus() { return status; }
    public Cliente getCliente() { return cliente; }
    public Veiculo getVeiculo() { return veiculo; }
    public PoliticaDesconto getPoliticaDesconto() { return politicaDesconto; }
    public List<Mecanico> getMecanicos() { return Collections.unmodifiableList(mecanicos); }
    public List<ItemCobravel> getItens() { return Collections.unmodifiableList(itens); }
}

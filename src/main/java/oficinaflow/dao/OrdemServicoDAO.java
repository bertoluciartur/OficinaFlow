package oficinaflow.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import oficinaflow.model.ItemCobravel;
import oficinaflow.model.ItemFactory;
import oficinaflow.model.ItemPeca;
import oficinaflow.model.OrdemServico;
import oficinaflow.model.PoliticasDesconto;
import oficinaflow.model.StatusOrdemServico;

@org.springframework.stereotype.Repository
public class OrdemServicoDAO {

    private final javax.sql.DataSource ds;
    private final ClienteDAO clientes;
    private final VeiculoDAO veiculos;
    private final PecaDAO pecas;

    public OrdemServicoDAO(javax.sql.DataSource ds, ClienteDAO clientes, VeiculoDAO veiculos, PecaDAO pecas) {
        this.ds = ds;
        this.clientes = clientes;
        this.veiculos = veiculos;
        this.pecas = pecas;
    }

    public OrdemServico buscarPorId(int id) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM ordem_servico WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(c, rs) : null;
            }
        }
    }

    /** Grava OS, mecânicos, itens e baixa de estoque na mesma transação. */
    public void salvar(OrdemServico os) throws SQLException {
        try (Connection c = ds.getConnection()) {
            c.setAutoCommit(false);
            try {
                inserirOrdem(c, os);
                inserirMecanicos(c, os);
                inserirItens(c, os.getId(), os.getItens());
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** Grava os itens novos (com baixa de estoque) e atualiza o valor total, numa só transação (RF12). */
    public void adicionarItens(OrdemServico os, List<ItemCobravel> novos) throws SQLException {
        try (Connection c = ds.getConnection()) {
            c.setAutoCommit(false);
            try {
                inserirItens(c, os.getId(), novos);
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE ordem_servico SET valor_total = ?, data_atualizacao = NOW() WHERE id = ?")) {
                    ps.setDouble(1, os.calcularValorTotal());
                    ps.setInt(2, os.getId());
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** Com devolverEstoque, as peças da OS voltam ao estoque na mesma transação (cancelamento). */
    public void atualizarStatus(OrdemServico os, boolean devolverEstoque) throws SQLException {
        try (Connection c = ds.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE ordem_servico SET status = ?, data_atualizacao = NOW() WHERE id = ?")) {
                    ps.setString(1, os.getStatus().name());
                    ps.setInt(2, os.getId());
                    ps.executeUpdate();
                }
                if (devolverEstoque) {
                    try (PreparedStatement ps = c.prepareStatement(
                            "UPDATE peca SET quantidade = quantidade + ? WHERE id = ?")) {
                        for (ItemCobravel item : os.getItens()) {
                            if (item instanceof ItemPeca ip) {
                                ps.setInt(1, ip.getQuantidade());
                                ps.setInt(2, ip.getPeca().getId());
                                ps.executeUpdate();
                            }
                        }
                    }
                }
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** OS concluídas cuja conclusão (última atualização) caiu no período (RF17). */
    public List<OrdemServico> concluidasNoPeriodo(LocalDate de, LocalDate ate) throws SQLException {
        return consultar("SELECT * FROM ordem_servico o WHERE o.status = 'CONCLUIDA' "
            + "AND o.data_atualizacao::date BETWEEN ? AND ? ORDER BY o.data_atualizacao DESC",
            List.of(Date.valueOf(de), Date.valueOf(ate)));
    }

    public java.util.Map<StatusOrdemServico, Long> contagemPorStatus() throws SQLException {
        java.util.Map<StatusOrdemServico, Long> mapa = new java.util.EnumMap<>(StatusOrdemServico.class);
        for (StatusOrdemServico s : StatusOrdemServico.values()) {
            mapa.put(s, 0L);
        }
        try (Connection c = ds.getConnection();
             ResultSet rs = c.createStatement().executeQuery(
                 "SELECT status, COUNT(*) AS n FROM ordem_servico GROUP BY status")) {
            while (rs.next()) {
                mapa.put(StatusOrdemServico.valueOf(rs.getString("status")), rs.getLong("n"));
            }
        }
        return mapa;
    }

    /** Filtros nulos são ignorados (RF05, RF15). */
    public List<OrdemServico> buscar(Integer clienteId, Integer veiculoId, Integer mecanicoId,
                                     StatusOrdemServico status, LocalDate de, LocalDate ate) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM ordem_servico o WHERE 1=1");
        List<Object> args = new ArrayList<>();
        if (clienteId != null) { sql.append(" AND o.cliente_id = ?"); args.add(clienteId); }
        if (veiculoId != null) { sql.append(" AND o.veiculo_id = ?"); args.add(veiculoId); }
        if (mecanicoId != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM os_mecanico m WHERE m.os_id = o.id AND m.mecanico_id = ?)");
            args.add(mecanicoId);
        }
        if (status != null) { sql.append(" AND o.status = ?"); args.add(status.name()); }
        if (de != null) { sql.append(" AND o.data_abertura >= ?"); args.add(Date.valueOf(de)); }
        if (ate != null) { sql.append(" AND o.data_abertura <= ?"); args.add(Date.valueOf(ate)); }
        sql.append(" ORDER BY o.id DESC");
        return consultar(sql.toString(), args);
    }

    private List<OrdemServico> consultar(String sql, List<Object> args) throws SQLException {
        List<OrdemServico> lista = new ArrayList<>();
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < args.size(); i++) {
                ps.setObject(i + 1, args.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montar(c, rs));
                }
            }
        }
        return lista;
    }

    private OrdemServico montar(Connection c, ResultSet rs) throws SQLException {
        OrdemServico os = new OrdemServico(rs.getInt("id"), rs.getDate("data_abertura").toLocalDate(),
            StatusOrdemServico.valueOf(rs.getString("status")),
            clientes.buscar(rs.getInt("cliente_id")), veiculos.buscar(rs.getInt("veiculo_id")));
        os.definirPoliticaDesconto(PoliticasDesconto.de(rs.getString("politica_desconto")));
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT m.* FROM mecanico m JOIN os_mecanico om ON om.mecanico_id = m.id WHERE om.os_id = ?")) {
            ps.setInt(1, os.getId());
            try (ResultSet r = ps.executeQuery()) {
                while (r.next()) {
                    os.associarMecanico(MecanicoDAO.ler(r));
                }
            }
        }
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM os_item WHERE os_id = ? ORDER BY id")) {
            ps.setInt(1, os.getId());
            try (ResultSet r = ps.executeQuery()) {
                while (r.next()) {
                    int pecaId = r.getInt("peca_id");
                    Integer qtd = (Integer) r.getObject("quantidade");
                    Double v1 = decimal(r, "v1");
                    Double v2 = decimal(r, "v2");
                    os.adicionarItem(ItemFactory.criar(r.getString("tipo"),
                        pecaId == 0 ? null : pecas.buscar(pecaId), r.getString("descricao"), qtd, v1, v2));
                }
            }
        }
        return os;
    }

    /** O driver não converte numeric direto para Double; null do banco continua null. */
    private static Double decimal(ResultSet r, String coluna) throws SQLException {
        java.math.BigDecimal b = r.getBigDecimal(coluna);
        return b == null ? null : b.doubleValue();
    }

    private void inserirOrdem(Connection c, OrdemServico os) throws SQLException {
        String sql = "INSERT INTO ordem_servico "
            + "(data_abertura, status, politica_desconto, valor_total, cliente_id, veiculo_id) "
            + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(os.getDataAbertura()));
            ps.setString(2, os.getStatus().name());
            ps.setString(3, os.getPoliticaDesconto().getCodigo());
            ps.setDouble(4, os.calcularValorTotal());
            ps.setInt(5, os.getCliente().getId());
            ps.setInt(6, os.getVeiculo().getId());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                os.setId(rs.getInt(1));
            }
        }
    }

    private void inserirMecanicos(Connection c, OrdemServico os) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO os_mecanico (os_id, mecanico_id) VALUES (?, ?)")) {
            for (var m : os.getMecanicos()) {
                ps.setInt(1, os.getId());
                ps.setInt(2, m.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void inserirItens(Connection c, int osId, List<ItemCobravel> itens) throws SQLException {
        String sql = "INSERT INTO os_item (os_id, tipo, peca_id, descricao, quantidade, v1, v2) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String baixa = "UPDATE peca SET quantidade = quantidade - ? WHERE id = ? AND quantidade >= ?";
        try (PreparedStatement ps = c.prepareStatement(sql); PreparedStatement bx = c.prepareStatement(baixa)) {
            for (ItemCobravel item : itens) {
                Object[] d = item.dados();
                ps.setInt(1, osId);
                ps.setString(2, item.getTipo());
                for (int i = 0; i < d.length; i++) {
                    ps.setObject(i + 3, d[i]);
                }
                ps.executeUpdate();
                if (item instanceof ItemPeca ip) {
                    bx.setInt(1, ip.getQuantidade());
                    bx.setInt(2, ip.getPeca().getId());
                    bx.setInt(3, ip.getQuantidade());
                    if (bx.executeUpdate() == 0) {
                        throw new IllegalStateException(
                            "Estoque insuficiente da peça " + ip.getPeca().getCodigo());
                    }
                }
            }
        }
    }
}

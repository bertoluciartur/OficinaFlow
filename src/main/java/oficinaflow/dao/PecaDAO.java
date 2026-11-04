package oficinaflow.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import oficinaflow.model.Peca;

@org.springframework.stereotype.Repository
public class PecaDAO {

    private final javax.sql.DataSource ds;

    public PecaDAO(javax.sql.DataSource ds) {
        this.ds = ds;
    }

    static Peca ler(ResultSet rs) throws SQLException {
        return new Peca(rs.getInt("id"), rs.getString("codigo"), rs.getString("descricao"),
            rs.getDouble("valor_unitario"), rs.getInt("quantidade"), rs.getInt("estoque_minimo"),
            rs.getString("fornecedor"));
    }

    public List<Peca> listar() throws SQLException {
        List<Peca> lista = new ArrayList<>();
        try (Connection c = ds.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT * FROM peca ORDER BY descricao")) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        }
        return lista;
    }

    public Peca buscar(int id) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM peca WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? ler(rs) : null;
            }
        }
    }

    public void salvar(Peca p) throws SQLException {
        boolean novo = p.getId() == 0;
        String sql = novo
            ? "INSERT INTO peca (codigo, descricao, valor_unitario, quantidade, estoque_minimo, fornecedor) "
              + "VALUES (?, ?, ?, ?, ?, ?)"
            : "UPDATE peca SET codigo=?, descricao=?, valor_unitario=?, quantidade=?, estoque_minimo=?, "
              + "fornecedor=? WHERE id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getDescricao());
            ps.setDouble(3, p.getValorUnitario());
            ps.setInt(4, p.getQuantidade());
            ps.setInt(5, p.getEstoqueMinimo());
            ps.setString(6, p.getFornecedor());
            if (!novo) {
                ps.setInt(7, p.getId());
            }
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM peca WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}

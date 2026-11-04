package oficinaflow.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import oficinaflow.model.Veiculo;

@org.springframework.stereotype.Repository
public class VeiculoDAO {

    private final javax.sql.DataSource ds;

    public VeiculoDAO(javax.sql.DataSource ds) {
        this.ds = ds;
    }

    private static final String SELECT =
        "SELECT v.id, v.placa, v.marca, v.modelo, v.ano, v.quilometragem, "
        + "c.id AS c_id, c.nome, c.cpf_cnpj, c.telefone, c.email, c.endereco, c.ativo "
        + "FROM veiculo v JOIN cliente c ON c.id = v.cliente_id ";

    private static Veiculo ler(ResultSet rs) throws SQLException {
        oficinaflow.model.Cliente cl = new oficinaflow.model.Cliente(rs.getInt("c_id"), rs.getString("nome"),
            rs.getString("cpf_cnpj"), rs.getString("telefone"), rs.getString("email"), rs.getString("endereco"),
            rs.getBoolean("ativo"));
        return new Veiculo(rs.getInt("id"), rs.getString("placa"), rs.getString("marca"), rs.getString("modelo"),
            rs.getInt("ano"), rs.getDouble("quilometragem"), cl);
    }

    public List<Veiculo> listar() throws SQLException {
        List<Veiculo> lista = new ArrayList<>();
        try (Connection c = ds.getConnection();
             ResultSet rs = c.createStatement().executeQuery(SELECT + "ORDER BY v.placa")) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        }
        return lista;
    }

    public Veiculo buscar(int id) throws SQLException {
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(SELECT + "WHERE v.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? ler(rs) : null;
            }
        }
    }

    public void salvar(Veiculo v) throws SQLException {
        boolean novo = v.getId() == 0;
        String sql = novo
            ? "INSERT INTO veiculo (placa, marca, modelo, ano, quilometragem, cliente_id) VALUES (?, ?, ?, ?, ?, ?)"
            : "UPDATE veiculo SET placa=?, marca=?, modelo=?, ano=?, quilometragem=?, cliente_id=? WHERE id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setInt(4, v.getAno());
            ps.setDouble(5, v.getQuilometragem());
            ps.setInt(6, v.getProprietario().getId());
            if (!novo) {
                ps.setInt(7, v.getId());
            }
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM veiculo WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}

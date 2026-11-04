package oficinaflow.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import oficinaflow.model.Mecanico;

@org.springframework.stereotype.Repository
public class MecanicoDAO {

    private final javax.sql.DataSource ds;

    public MecanicoDAO(javax.sql.DataSource ds) {
        this.ds = ds;
    }

    static Mecanico ler(ResultSet rs) throws SQLException {
        return new Mecanico(rs.getInt("id"), rs.getString("nome"),
            rs.getString("especialidade"), rs.getString("contato"));
    }

    public List<Mecanico> listar() throws SQLException {
        List<Mecanico> lista = new ArrayList<>();
        try (Connection c = ds.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT * FROM mecanico ORDER BY nome")) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        }
        return lista;
    }

    public void salvar(Mecanico m) throws SQLException {
        boolean novo = m.getId() == 0;
        String sql = novo
            ? "INSERT INTO mecanico (nome, especialidade, contato) VALUES (?, ?, ?)"
            : "UPDATE mecanico SET nome=?, especialidade=?, contato=? WHERE id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getNome());
            ps.setString(2, m.getEspecialidade());
            ps.setString(3, m.getContato());
            if (!novo) {
                ps.setInt(4, m.getId());
            }
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM mecanico WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}

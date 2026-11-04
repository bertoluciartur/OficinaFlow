package oficinaflow.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import oficinaflow.model.Cliente;

@org.springframework.stereotype.Repository
public class ClienteDAO {

    private final javax.sql.DataSource ds;

    public ClienteDAO(javax.sql.DataSource ds) {
        this.ds = ds;
    }

    static Cliente ler(ResultSet rs) throws SQLException {
        return new Cliente(rs.getInt("id"), rs.getString("nome"), rs.getString("cpf_cnpj"),
            rs.getString("telefone"), rs.getString("email"), rs.getString("endereco"), rs.getBoolean("ativo"));
    }

    public List<Cliente> listar() throws SQLException {
        return consultar("SELECT * FROM cliente ORDER BY nome");
    }

    /** Clientes inativos não recebem novas ordens de serviço (RF02). */
    public List<Cliente> listarAtivos() throws SQLException {
        return consultar("SELECT * FROM cliente WHERE ativo ORDER BY nome");
    }

    private List<Cliente> consultar(String sql) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        try (Connection c = ds.getConnection(); ResultSet rs = c.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        }
        return lista;
    }

    public Cliente buscar(int id) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM cliente WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? ler(rs) : null;
            }
        }
    }

    public void salvar(Cliente cl) throws SQLException {
        boolean novo = cl.getId() == 0;
        String sql = novo
            ? "INSERT INTO cliente (nome, cpf_cnpj, telefone, email, endereco, ativo) VALUES (?, ?, ?, ?, ?, ?)"
            : "UPDATE cliente SET nome=?, cpf_cnpj=?, telefone=?, email=?, endereco=?, ativo=? WHERE id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cl.getNome());
            ps.setString(2, cl.getCpfCnpj());
            ps.setString(3, cl.getTelefone());
            ps.setString(4, cl.getEmail());
            ps.setString(5, cl.getEndereco());
            ps.setBoolean(6, cl.isAtivo());
            if (!novo) {
                ps.setInt(7, cl.getId());
            }
            ps.executeUpdate();
        }
    }

    /** Falha por FK se houver veículos ou OS vinculados (RF02, RNF05). */
    public void excluir(int id) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM cliente WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}

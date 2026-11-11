package persistencia;

import dados.Cliente;
import excecoes.PersistenciaException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public void inserir(Cliente c) throws PersistenciaException {
        String sql = "INSERT INTO cliente (nome, email, datanascimento, endereco) VALUES (?, ?, ?, ?) RETURNING idcliente";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            if (c.getEmail() != null && !c.getEmail().isBlank()) {
                ps.setString(2, c.getEmail().trim());
            } else {
                ps.setNull(2, Types.VARCHAR);
            }
            if (c.getDataNascimento() != null) {
                ps.setDate(3, Date.valueOf(c.getDataNascimento()));
            } else {
                ps.setNull(3, Types.DATE);
            }
            if (c.getEndereco() != null && !c.getEndereco().isBlank()) {
                ps.setString(4, c.getEndereco().trim());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c.setIdCliente(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao inserir cliente", e), e);
        }
    }

    public void atualizar(Cliente c) throws PersistenciaException {
        String sql = "UPDATE cliente SET nome = ?, email = ?, datanascimento = ?, endereco = ? WHERE idcliente = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            if (c.getEmail() != null && !c.getEmail().isBlank()) {
                ps.setString(2, c.getEmail().trim());
            } else {
                ps.setNull(2, Types.VARCHAR);
            }
            if (c.getDataNascimento() != null) {
                ps.setDate(3, Date.valueOf(c.getDataNascimento()));
            } else {
                ps.setNull(3, Types.DATE);
            }
            if (c.getEndereco() != null && !c.getEndereco().isBlank()) {
                ps.setString(4, c.getEndereco().trim());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }
            ps.setInt(5, c.getIdCliente());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao atualizar cliente", e), e);
        }
    }

    public void excluir(int idCliente) throws PersistenciaException {
        String sql = "DELETE FROM cliente WHERE idcliente = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao excluir cliente", e), e);
        }
    }

    public List<Cliente> listarTodos() throws PersistenciaException {
        String sql = "SELECT idcliente, nome, email, datanascimento, endereco FROM cliente ORDER BY idcliente";
        Connection con = Conexao.getConexao();
        List<Cliente> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao consultar clientes", e), e);
        }
    }

    public List<Cliente> buscarPorTermo(String termo) throws PersistenciaException {
        String sql = "SELECT idcliente, nome, email, datanascimento, endereco FROM cliente " +
                     "WHERE LOWER(nome) LIKE LOWER(?) OR LOWER(COALESCE(email, '')) LIKE LOWER(?) " +
                     "ORDER BY idcliente";
        Connection con = Conexao.getConexao();
        List<Cliente> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String filtro = "%" + termo.trim() + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCliente(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao filtrar clientes", e), e);
        }
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Date dt = rs.getDate("datanascimento");
        return new Cliente(
                rs.getInt("idcliente"),
                rs.getString("nome"),
                rs.getString("email"),
                dt != null ? dt.toLocalDate() : null,
                rs.getString("endereco")
        );
    }
}

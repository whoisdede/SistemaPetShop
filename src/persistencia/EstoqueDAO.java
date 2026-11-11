package persistencia;

import dados.Estoque;
import excecoes.PersistenciaException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstoqueDAO {

    public void inserir(Estoque e) throws PersistenciaException {
        String sql = "INSERT INTO estoque (quantidademinima, quantidadeatual, subtotal) VALUES (?, ?, ?) RETURNING idestoque";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, e.getQuantidadeMinima());
            ps.setInt(2, e.getQuantidadeAtual());
            ps.setBigDecimal(3, e.getSubtotal() != null ? e.getSubtotal() : BigDecimal.ZERO);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    e.setIdEstoque(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao inserir estoque", ex), ex);
        }
    }

    public void atualizar(Estoque e) throws PersistenciaException {
        String sql = "UPDATE estoque SET quantidademinima = ?, quantidadeatual = ? WHERE idestoque = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, e.getQuantidadeMinima());
            ps.setInt(2, e.getQuantidadeAtual());
            ps.setInt(3, e.getIdEstoque());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao atualizar estoque", ex), ex);
        }
    }

    public List<Estoque> listarTodos() throws PersistenciaException {
        String sql = "SELECT idestoque, quantidademinima, quantidadeatual, subtotal FROM estoque ORDER BY idestoque";
        Connection con = Conexao.getConexao();
        List<Estoque> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Estoque(
                        rs.getInt("idestoque"),
                        rs.getInt("quantidademinima"),
                        rs.getInt("quantidadeatual"),
                        rs.getBigDecimal("subtotal")
                ));
            }
            return lista;
        } catch (SQLException ex) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao consultar estoques", ex), ex);
        }
    }

    public void vincularAdministrador(int idUsuario, int idEstoque) throws PersistenciaException {
        String sql = "INSERT INTO usuario_estoque_administra (idusuario, idestoque) VALUES (?, ?) ON CONFLICT DO NOTHING";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idEstoque);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao vincular administrador ao estoque", ex), ex);
        }
    }

    public List<String[]> listarAdministradores() throws PersistenciaException {
        String sql = "SELECT uea.idestoque, u.login, f.nome AS funcionario, u.nivelacesso " +
                     "FROM usuario_estoque_administra uea " +
                     "JOIN usuario u ON u.idusuario = uea.idusuario " +
                     "JOIN funcionario f ON f.idfunc = u.idfunc " +
                     "ORDER BY uea.idestoque, u.login";
        Connection con = Conexao.getConexao();
        List<String[]> linhas = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                linhas.add(new String[]{
                        "Estoque #" + rs.getInt("idestoque"),
                        rs.getString("login"),
                        rs.getString("funcionario"),
                        rs.getString("nivelacesso")
                });
            }
            return linhas;
        } catch (SQLException ex) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao consultar administradores de estoque", ex), ex);
        }
    }
}

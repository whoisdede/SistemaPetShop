package persistencia;

import dados.Estoque;
import dados.Produto;
import excecoes.PersistenciaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public void inserir(Produto p) throws PersistenciaException {
        String sql = "INSERT INTO produto (nomeproduto, preco, custo, idestoque) VALUES (?, ?, ?, ?) RETURNING idproduto";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNomeProduto().trim());
            ps.setBigDecimal(2, p.getPreco());
            ps.setBigDecimal(3, p.getCusto());
            if (p.getEstoque() != null) {
                ps.setInt(4, p.getEstoque().getIdEstoque());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p.setIdProduto(rs.getInt(1));
                }
            }
            if (p.getEstoque() != null) {
                atualizarSubtotalEstoque(con, p.getEstoque().getIdEstoque());
            }
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao inserir produto", e), e);
        }
    }

    public void atualizar(Produto p) throws PersistenciaException {
        String sql = "UPDATE produto SET nomeproduto = ?, preco = ?, custo = ?, idestoque = ? WHERE idproduto = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNomeProduto().trim());
            ps.setBigDecimal(2, p.getPreco());
            ps.setBigDecimal(3, p.getCusto());
            if (p.getEstoque() != null) {
                ps.setInt(4, p.getEstoque().getIdEstoque());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setInt(5, p.getIdProduto());
            ps.executeUpdate();
            if (p.getEstoque() != null) {
                atualizarSubtotalEstoque(con, p.getEstoque().getIdEstoque());
            }
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao atualizar produto", e), e);
        }
    }

    public void excluir(int idProduto) throws PersistenciaException {
        String sql = "DELETE FROM produto WHERE idproduto = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProduto);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao excluir produto", e), e);
        }
    }

    public List<Produto> listarTodos() throws PersistenciaException {
        String sql = "SELECT p.idproduto, p.nomeproduto, p.preco, p.custo, " +
                     "e.idestoque, e.quantidademinima, e.quantidadeatual, e.subtotal " +
                     "FROM produto p " +
                     "LEFT JOIN estoque e ON e.idestoque = p.idestoque " +
                     "ORDER BY p.idproduto";
        Connection con = Conexao.getConexao();
        List<Produto> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearProduto(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao consultar produtos", e), e);
        }
    }

    private void atualizarSubtotalEstoque(Connection con, int idEstoque) throws SQLException {
        String sql = "UPDATE estoque e SET subtotal = COALESCE((" +
                     "SELECT SUM(p.preco * e.quantidadeatual) FROM produto p WHERE p.idestoque = e.idestoque" +
                     "), 0) WHERE e.idestoque = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstoque);
            ps.executeUpdate();
        }
    }

    private Produto mapearProduto(ResultSet rs) throws SQLException {
        Estoque est = null;
        int idEst = rs.getInt("idestoque");
        if (!rs.wasNull()) {
            est = new Estoque(
                    idEst,
                    rs.getInt("quantidademinima"),
                    rs.getInt("quantidadeatual"),
                    rs.getBigDecimal("subtotal")
            );
        }
        return new Produto(
                rs.getInt("idproduto"),
                rs.getString("nomeproduto"),
                rs.getBigDecimal("preco"),
                rs.getBigDecimal("custo"),
                est
        );
    }
}

package persistencia;

import dados.CompraFornecedor;
import dados.Fornecedor;
import dados.Produto;
import excecoes.PersistenciaException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompraFornecedorDAO {

    public void registrarCompra(CompraFornecedor cf) throws PersistenciaException {
        String sql = "INSERT INTO fornecedor_produto_compra (idfornecedor, idproduto, data, qtd) VALUES (?, ?, ?, ?)";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cf.getFornecedor().getIdFornecedor());
            ps.setInt(2, cf.getProduto().getIdProduto());
            ps.setDate(3, Date.valueOf(cf.getData()));
            ps.setInt(4, cf.getQtd());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao registrar compra de fornecedor", e), e);
        }
    }

    public List<CompraFornecedor> listarTodas() throws PersistenciaException {
        String sql = "SELECT fpc.data, fpc.qtd, " +
                     "f.idfornecedor, f.nome AS nome_forn, f.razaosocial, f.cnpj, f.endereco, " +
                     "p.idproduto, p.nomeproduto, p.preco, p.custo " +
                     "FROM fornecedor_produto_compra fpc " +
                     "JOIN fornecedor f ON f.idfornecedor = fpc.idfornecedor " +
                     "JOIN produto p ON p.idproduto = fpc.idproduto " +
                     "ORDER BY fpc.data DESC, f.nome";
        Connection con = Conexao.getConexao();
        List<CompraFornecedor> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Fornecedor forn = new Fornecedor(
                        rs.getInt("idfornecedor"),
                        rs.getString("nome_forn"),
                        rs.getString("razaosocial"),
                        rs.getString("cnpj"),
                        rs.getString("endereco")
                );
                Produto prod = new Produto(
                        rs.getInt("idproduto"),
                        rs.getString("nomeproduto"),
                        rs.getBigDecimal("preco"),
                        rs.getBigDecimal("custo"),
                        null
                );
                Date dt = rs.getDate("data");
                lista.add(new CompraFornecedor(forn, prod, dt != null ? dt.toLocalDate() : null, rs.getInt("qtd")));
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao consultar compras de fornecedores", e), e);
        }
    }
}

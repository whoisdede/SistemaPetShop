package persistencia;

import dados.Cliente;
import dados.ItemVenda;
import dados.Produto;
import dados.Venda;
import excecoes.PersistenciaException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VendaDAO {

    /**
     * Registra a venda e seus itens dentro de uma transação atômica.
     * Se o trigger trg_verificar_estoque rejeitar algum item por falta de estoque,
     * toda a venda sofre rollback automaticamente.
     */
    public void registrarVenda(Venda venda) throws PersistenciaException {
        String sqlVenda = "INSERT INTO venda (datavenda, idcliente) VALUES (?, ?) RETURNING idvenda";
        String sqlItem = "INSERT INTO venda_produto_contem (idvenda, idproduto, valorunit, qtd) VALUES (?, ?, ?, ?)";

        Connection con = Conexao.getConexao();
        boolean autoCommitOriginal = true;
        try {
            autoCommitOriginal = con.getAutoCommit();
            con.setAutoCommit(false);

            try (PreparedStatement psVenda = con.prepareStatement(sqlVenda)) {
                psVenda.setDate(1, Date.valueOf(venda.getDataVenda()));
                psVenda.setInt(2, venda.getCliente().getIdCliente());
                try (ResultSet rs = psVenda.executeQuery()) {
                    if (rs.next()) {
                        venda.setIdVenda(rs.getInt(1));
                    }
                }
            }

            try (PreparedStatement psItem = con.prepareStatement(sqlItem)) {
                for (ItemVenda item : venda.getItens()) {
                    psItem.setInt(1, venda.getIdVenda());
                    psItem.setInt(2, item.getProduto().getIdProduto());
                    psItem.setBigDecimal(3, item.getValorUnit());
                    psItem.setInt(4, item.getQtd());
                    psItem.executeUpdate();
                }
            }

            con.commit();
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ignored) {
            }
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao registrar venda", e), e);
        } finally {
            try {
                con.setAutoCommit(autoCommitOriginal);
            } catch (SQLException ignored) {
            }
        }
    }

    public List<Venda> listarTodasComItens() throws PersistenciaException {
        String sql = "SELECT v.idvenda, v.datavenda, " +
                     "c.idcliente, c.nome AS nome_cliente, c.email, c.datanascimento, c.endereco, " +
                     "p.idproduto, p.nomeproduto, p.preco, p.custo, " +
                     "vpc.valorunit, vpc.qtd " +
                     "FROM venda v " +
                     "JOIN cliente c ON c.idcliente = v.idcliente " +
                     "LEFT JOIN venda_produto_contem vpc ON vpc.idvenda = v.idvenda " +
                     "LEFT JOIN produto p ON p.idproduto = vpc.idproduto " +
                     "ORDER BY v.idvenda DESC, p.nomeproduto";
        Connection con = Conexao.getConexao();
        Map<Integer, Venda> mapa = new LinkedHashMap<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int idVenda = rs.getInt("idvenda");
                Venda v = mapa.get(idVenda);
                if (v == null) {
                    Date dtNasc = rs.getDate("datanascimento");
                    Cliente cli = new Cliente(
                            rs.getInt("idcliente"),
                            rs.getString("nome_cliente"),
                            rs.getString("email"),
                            dtNasc != null ? dtNasc.toLocalDate() : null,
                            rs.getString("endereco")
                    );
                    Date dtVenda = rs.getDate("datavenda");
                    v = new Venda(idVenda, dtVenda != null ? dtVenda.toLocalDate() : null, cli);
                    mapa.put(idVenda, v);
                }

                int idProd = rs.getInt("idproduto");
                if (!rs.wasNull()) {
                    Produto prod = new Produto(
                            idProd,
                            rs.getString("nomeproduto"),
                            rs.getBigDecimal("preco"),
                            rs.getBigDecimal("custo"),
                            null
                    );
                    v.adicionarItem(new ItemVenda(prod, rs.getBigDecimal("valorunit"), rs.getInt("qtd")));
                }
            }
            return new ArrayList<>(mapa.values());
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao consultar vendas", e), e);
        }
    }
}

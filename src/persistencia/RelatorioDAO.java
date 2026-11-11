package persistencia;

import excecoes.PersistenciaException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RelatorioDAO {

    public List<String[]> consultarEstoqueBaixo() throws PersistenciaException {
        String sql = "SELECT id_produto, nome_produto, quantidade_atual, quantidade_minima, diferenca, urgencia " +
                     "FROM fn_produtos_estoque_baixo()";
        Connection con = Conexao.getConexao();
        List<String[]> linhas = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                linhas.add(new String[]{
                        String.valueOf(rs.getInt("id_produto")),
                        rs.getString("nome_produto"),
                        String.valueOf(rs.getInt("quantidade_atual")),
                        String.valueOf(rs.getInt("quantidade_minima")),
                        String.valueOf(rs.getInt("diferenca")),
                        rs.getString("urgencia")
                });
            }
            return linhas;
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro no relatório de estoque baixo", e), e);
        }
    }

    public List<String[]> consultarRankingProdutos(int limite) throws PersistenciaException {
        String sql = "SELECT posicao, nome_produto, quantidade_total, faturamento_total, preco_medio " +
                     "FROM fn_ranking_produtos(?)";
        Connection con = Conexao.getConexao();
        List<String[]> linhas = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    linhas.add(new String[]{
                            rs.getLong("posicao") + "º",
                            rs.getString("nome_produto"),
                            String.valueOf(rs.getLong("quantidade_total")),
                            "R$ " + rs.getBigDecimal("faturamento_total"),
                            "R$ " + rs.getBigDecimal("preco_medio")
                    });
                }
            }
            return linhas;
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro no ranking de produtos", e), e);
        }
    }

    public List<String[]> consultarFaturamentoPeriodo(LocalDate inicio, LocalDate fim) throws PersistenciaException {
        String sql = "SELECT tipo_receita, quantidade, faturamento FROM fn_faturamento_periodo(?, ?)";
        Connection con = Conexao.getConexao();
        List<String[]> linhas = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(inicio));
            ps.setDate(2, Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    linhas.add(new String[]{
                            rs.getString("tipo_receita"),
                            String.valueOf(rs.getLong("quantidade")),
                            "R$ " + rs.getBigDecimal("faturamento")
                    });
                }
            }
            return linhas;
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro no relatório de faturamento", e), e);
        }
    }

    public List<String[]> consultarResumoGastoCliente(int idCliente) throws PersistenciaException {
        String sql = "SELECT nome_cliente, total_produtos, total_servicos, total_geral, qtd_compras, qtd_servicos " +
                     "FROM fn_total_gasto_cliente(?)";
        Connection con = Conexao.getConexao();
        List<String[]> linhas = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    linhas.add(new String[]{
                            rs.getString("nome_cliente"),
                            "R$ " + rs.getBigDecimal("total_produtos"),
                            "R$ " + rs.getBigDecimal("total_servicos"),
                            "R$ " + rs.getBigDecimal("total_geral"),
                            String.valueOf(rs.getLong("qtd_compras")),
                            String.valueOf(rs.getLong("qtd_servicos"))
                    });
                }
            }
            return linhas;
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao consultar total gasto do cliente", e), e);
        }
    }

    public List<String[]> consultarHistoricoComprasCliente(int idCliente) throws PersistenciaException {
        String sql = "SELECT nome_cliente, data_venda, produto, quantidade, preco_unitario, total_item " +
                     "FROM fn_relatorio_vendas_cliente(?)";
        Connection con = Conexao.getConexao();
        List<String[]> linhas = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    linhas.add(new String[]{
                            rs.getString("nome_cliente"),
                            String.valueOf(rs.getDate("data_venda")),
                            rs.getString("produto"),
                            String.valueOf(rs.getInt("quantidade")),
                            "R$ " + rs.getBigDecimal("preco_unitario"),
                            "R$ " + rs.getBigDecimal("total_item")
                    });
                }
            }
            return linhas;
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao consultar histórico do cliente", e), e);
        }
    }
}

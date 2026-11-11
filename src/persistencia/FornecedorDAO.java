package persistencia;

import dados.Fornecedor;
import excecoes.PersistenciaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class FornecedorDAO {

    public void inserir(Fornecedor f) throws PersistenciaException {
        String sql = "INSERT INTO fornecedor (nome, razaosocial, cnpj, endereco) VALUES (?, ?, ?, ?) RETURNING idfornecedor, cnpj";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getNome().trim());
            if (f.getRazaoSocial() != null && !f.getRazaoSocial().isBlank()) ps.setString(2, f.getRazaoSocial().trim());
            else ps.setNull(2, Types.VARCHAR);

            if (f.getCnpj() != null && !f.getCnpj().isBlank()) ps.setString(3, f.getCnpj().trim());
            else ps.setNull(3, Types.VARCHAR);

            if (f.getEndereco() != null && !f.getEndereco().isBlank()) ps.setString(4, f.getEndereco().trim());
            else ps.setNull(4, Types.VARCHAR);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    f.setIdFornecedor(rs.getInt("idfornecedor"));
                    f.setCnpj(rs.getString("cnpj"));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao inserir fornecedor", e), e);
        }
    }

    public void excluir(int idFornecedor) throws PersistenciaException {
        String sql = "DELETE FROM fornecedor WHERE idfornecedor = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idFornecedor);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao excluir fornecedor", e), e);
        }
    }

    public List<Fornecedor> listarTodos() throws PersistenciaException {
        String sql = "SELECT idfornecedor, nome, razaosocial, cnpj, endereco FROM fornecedor ORDER BY idfornecedor";
        Connection con = Conexao.getConexao();
        List<Fornecedor> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Fornecedor(
                        rs.getInt("idfornecedor"),
                        rs.getString("nome"),
                        rs.getString("razaosocial"),
                        rs.getString("cnpj"),
                        rs.getString("endereco")
                ));
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao consultar fornecedores", e), e);
        }
    }
}

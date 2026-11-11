package persistencia;

import dados.Funcionario;
import excecoes.PersistenciaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {

    public void inserir(Funcionario f) throws PersistenciaException {
        String sql = "INSERT INTO funcionario (nome, cargo, salario) VALUES (?, ?, ?) RETURNING idfunc";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            if (f.getCargo() != null && !f.getCargo().isBlank()) {
                ps.setString(2, f.getCargo().trim());
            } else {
                ps.setNull(2, Types.VARCHAR);
            }
            if (f.getSalario() != null) {
                ps.setBigDecimal(3, f.getSalario());
            } else {
                ps.setNull(3, Types.NUMERIC);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    f.setIdFunc(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao inserir funcionário", e), e);
        }
    }

    public List<Funcionario> listarTodos() throws PersistenciaException {
        String sql = "SELECT idfunc, nome, cargo, salario FROM funcionario ORDER BY idfunc";
        Connection con = Conexao.getConexao();
        List<Funcionario> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Funcionario(
                        rs.getInt("idfunc"),
                        rs.getString("nome"),
                        rs.getString("cargo"),
                        rs.getBigDecimal("salario")
                ));
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao consultar funcionários", e), e);
        }
    }
}

package persistencia;

import dados.Funcionario;
import dados.Usuario;
import excecoes.PersistenciaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void inserir(Usuario u) throws PersistenciaException {
        String sql = "INSERT INTO usuario (login, senha, nivelacesso, idfunc) VALUES (?, ?, ?, ?) RETURNING idusuario";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getLogin().trim());
            ps.setString(2, u.getSenha());
            ps.setString(3, u.getNivelAcesso());
            ps.setInt(4, u.getFuncionario().getIdFunc());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u.setIdUsuario(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao inserir usuário", e), e);
        }
    }

    public List<Usuario> listarTodos() throws PersistenciaException {
        String sql = "SELECT u.idusuario, u.login, u.senha, u.nivelacesso, " +
                     "f.idfunc, f.nome AS nome_func, f.cargo, f.salario " +
                     "FROM usuario u " +
                     "JOIN funcionario f ON f.idfunc = u.idfunc " +
                     "ORDER BY u.idusuario";
        Connection con = Conexao.getConexao();
        List<Usuario> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Funcionario f = new Funcionario(
                        rs.getInt("idfunc"),
                        rs.getString("nome_func"),
                        rs.getString("cargo"),
                        rs.getBigDecimal("salario")
                );
                lista.add(new Usuario(
                        rs.getInt("idusuario"),
                        rs.getString("login"),
                        rs.getString("senha"),
                        rs.getString("nivelacesso"),
                        f
                ));
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao consultar usuários", e), e);
        }
    }
}

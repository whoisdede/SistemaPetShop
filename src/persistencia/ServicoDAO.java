package persistencia;

import dados.Cliente;
import dados.Funcionario;
import dados.Servico;
import dados.Usuario;
import excecoes.PersistenciaException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {

    public void inserir(Servico s) throws PersistenciaException {
        String sql = "INSERT INTO servico (valor, tiposervico, datatransacao, idcliente, idusuario) " +
                     "VALUES (?, ?, ?, ?, ?) RETURNING idservico";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, s.getValor());
            ps.setString(2, s.getTipoServico().trim());
            ps.setDate(3, Date.valueOf(s.getDataTransacao()));
            ps.setInt(4, s.getCliente().getIdCliente());
            ps.setInt(5, s.getUsuario().getIdUsuario());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    s.setIdServico(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao registrar serviço", e), e);
        }
    }

    public List<Servico> listarTodos() throws PersistenciaException {
        String sql = "SELECT s.idservico, s.valor, s.tiposervico, s.datatransacao, " +
                     "c.idcliente, c.nome AS nome_cliente, c.email, c.datanascimento, c.endereco, " +
                     "u.idusuario, u.login, u.senha, u.nivelacesso, " +
                     "f.idfunc, f.nome AS nome_func, f.cargo, f.salario " +
                     "FROM servico s " +
                     "JOIN cliente c ON c.idcliente = s.idcliente " +
                     "JOIN usuario u ON u.idusuario = s.idusuario " +
                     "JOIN funcionario f ON f.idfunc = u.idfunc " +
                     "ORDER BY s.datatransacao DESC, s.idservico DESC";
        Connection con = Conexao.getConexao();
        List<Servico> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Date dtNasc = rs.getDate("datanascimento");
                Cliente c = new Cliente(
                        rs.getInt("idcliente"),
                        rs.getString("nome_cliente"),
                        rs.getString("email"),
                        dtNasc != null ? dtNasc.toLocalDate() : null,
                        rs.getString("endereco")
                );
                Funcionario f = new Funcionario(
                        rs.getInt("idfunc"),
                        rs.getString("nome_func"),
                        rs.getString("cargo"),
                        rs.getBigDecimal("salario")
                );
                Usuario u = new Usuario(
                        rs.getInt("idusuario"),
                        rs.getString("login"),
                        rs.getString("senha"),
                        rs.getString("nivelacesso"),
                        f
                );
                Date dtTrans = rs.getDate("datatransacao");
                lista.add(new Servico(
                        rs.getInt("idservico"),
                        rs.getBigDecimal("valor"),
                        rs.getString("tiposervico"),
                        dtTrans != null ? dtTrans.toLocalDate() : null,
                        c,
                        u
                ));
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao consultar serviços", e), e);
        }
    }
}

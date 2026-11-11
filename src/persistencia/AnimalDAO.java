package persistencia;

import dados.Animal;
import dados.Cliente;
import dados.Funcionario;
import excecoes.PersistenciaException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AnimalDAO {

    public void inserir(Animal a) throws PersistenciaException {
        String sql = "INSERT INTO animal (nome, raca, especie, datanasc, dataadmissao, motivo, idcliente, idfunc) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING idanimal";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            preencherStatement(ps, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    a.setIdAnimal(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao inserir animal", e), e);
        }
    }

    public void atualizar(Animal a) throws PersistenciaException {
        String sql = "UPDATE animal SET nome = ?, raca = ?, especie = ?, datanasc = ?, dataadmissao = ?, " +
                     "motivo = ?, idcliente = ?, idfunc = ? WHERE idanimal = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            preencherStatement(ps, a);
            ps.setInt(9, a.getIdAnimal());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao atualizar animal", e), e);
        }
    }

    public void excluir(int idAnimal) throws PersistenciaException {
        String sql = "DELETE FROM animal WHERE idanimal = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAnimal);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao excluir animal", e), e);
        }
    }

    public List<Animal> listarTodos() throws PersistenciaException {
        String sql = "SELECT a.idanimal, a.nome, a.raca, a.especie, a.datanasc, a.dataadmissao, a.motivo, " +
                     "c.idcliente, c.nome AS nome_cliente, c.email, c.datanascimento AS nasc_cliente, c.endereco, " +
                     "f.idfunc, f.nome AS nome_func, f.cargo, f.salario " +
                     "FROM animal a " +
                     "JOIN cliente c ON c.idcliente = a.idcliente " +
                     "JOIN funcionario f ON f.idfunc = a.idfunc " +
                     "ORDER BY a.idanimal";
        Connection con = Conexao.getConexao();
        List<Animal> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearAnimal(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao consultar animais", e), e);
        }
    }

    public List<Animal> buscarPorFiltro(String termo) throws PersistenciaException {
        String sql = "SELECT a.idanimal, a.nome, a.raca, a.especie, a.datanasc, a.dataadmissao, a.motivo, " +
                     "c.idcliente, c.nome AS nome_cliente, c.email, c.datanascimento AS nasc_cliente, c.endereco, " +
                     "f.idfunc, f.nome AS nome_func, f.cargo, f.salario " +
                     "FROM animal a " +
                     "JOIN cliente c ON c.idcliente = a.idcliente " +
                     "JOIN funcionario f ON f.idfunc = a.idfunc " +
                     "WHERE LOWER(COALESCE(a.nome, '')) LIKE LOWER(?) " +
                     "   OR LOWER(COALESCE(a.especie, '')) LIKE LOWER(?) " +
                     "   OR LOWER(c.nome) LIKE LOWER(?) " +
                     "ORDER BY a.idanimal";
        Connection con = Conexao.getConexao();
        List<Animal> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String f = "%" + termo.trim() + "%";
            ps.setString(1, f);
            ps.setString(2, f);
            ps.setString(3, f);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAnimal(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao filtrar animais", e), e);
        }
    }

    private void preencherStatement(PreparedStatement ps, Animal a) throws SQLException {
        ps.setString(1, a.getNome());
        if (a.getRaca() != null && !a.getRaca().isBlank()) ps.setString(2, a.getRaca().trim());
        else ps.setNull(2, Types.VARCHAR);

        if (a.getEspecie() != null && !a.getEspecie().isBlank()) ps.setString(3, a.getEspecie().trim());
        else ps.setNull(3, Types.VARCHAR);

        if (a.getDataNasc() != null) ps.setDate(4, Date.valueOf(a.getDataNasc()));
        else ps.setNull(4, Types.DATE);

        if (a.getDataAdmissao() != null) ps.setDate(5, Date.valueOf(a.getDataAdmissao()));
        else ps.setNull(5, Types.DATE);

        if (a.getMotivo() != null && !a.getMotivo().isBlank()) ps.setString(6, a.getMotivo().trim());
        else ps.setNull(6, Types.VARCHAR);

        ps.setInt(7, a.getCliente().getIdCliente());
        ps.setInt(8, a.getFuncionario().getIdFunc());
    }

    private Animal mapearAnimal(ResultSet rs) throws SQLException {
        Date dtNascCli = rs.getDate("nasc_cliente");
        Cliente c = new Cliente(
                rs.getInt("idcliente"),
                rs.getString("nome_cliente"),
                rs.getString("email"),
                dtNascCli != null ? dtNascCli.toLocalDate() : null,
                rs.getString("endereco")
        );
        Funcionario f = new Funcionario(
                rs.getInt("idfunc"),
                rs.getString("nome_func"),
                rs.getString("cargo"),
                rs.getBigDecimal("salario")
        );
        Date dtNasc = rs.getDate("datanasc");
        Date dtAdm = rs.getDate("dataadmissao");
        return new Animal(
                rs.getInt("idanimal"),
                rs.getString("nome"),
                rs.getString("raca"),
                rs.getString("especie"),
                dtNasc != null ? dtNasc.toLocalDate() : null,
                dtAdm != null ? dtAdm.toLocalDate() : null,
                rs.getString("motivo"),
                c,
                f
        );
    }
}

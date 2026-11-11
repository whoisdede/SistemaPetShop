package persistencia;

import excecoes.PersistenciaException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexao {
    private static Connection conexao = null;
    private static String host = "localhost";
    private static String porta = "5432";
    private static String banco = "petshop";
    private static String usuario = "postgres";
    private static String senha = "";

    private Conexao() {
    }

    public static void configurar(String novoHost, String novaPorta, String novoBanco, String novoUsuario, String novaSenha) {
        fecharConexao();
        host = novoHost != null && !novoHost.isBlank() ? novoHost.trim() : "localhost";
        porta = novaPorta != null && !novaPorta.isBlank() ? novaPorta.trim() : "5432";
        banco = novoBanco != null && !novoBanco.isBlank() ? novoBanco.trim() : "petshop";
        usuario = novoUsuario != null && !novoUsuario.isBlank() ? novoUsuario.trim() : "postgres";
        senha = novaSenha != null ? novaSenha : "";
    }

    public static Connection getConexao() throws PersistenciaException {
        try {
            if (conexao == null || conexao.isClosed()) {
                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://" + host + ":" + porta + "/" + banco;
                conexao = DriverManager.getConnection(url, usuario, senha);
            }
            return conexao;
        } catch (ClassNotFoundException e) {
            throw new PersistenciaException("Driver JDBC do PostgreSQL não encontrado em libs/.", e);
        } catch (SQLException e) {
            throw new PersistenciaException(formatarErroSQL("Falha na conexão com o PostgreSQL", e), e);
        }
    }

    public static boolean isBancoInexistente(PersistenciaException ex) {
        Throwable causa = ex.getCause();
        if (causa instanceof SQLException sqlEx) {
            return "3D000".equals(sqlEx.getSQLState());
        }
        return false;
    }

    public static void criarBancoDeDados() throws PersistenciaException {
        if (!banco.matches("[a-zA-Z0-9_]+")) {
            throw new PersistenciaException("Nome de banco inválido: use apenas letras, números e underscore.");
        }
        try {
            Class.forName("org.postgresql.Driver");
            String urlAdmin = "jdbc:postgresql://" + host + ":" + porta + "/postgres";
            try (Connection conAdmin = DriverManager.getConnection(urlAdmin, usuario, senha);
                 Statement st = conAdmin.createStatement()) {
                st.executeUpdate("CREATE DATABASE " + banco);
            }
        } catch (ClassNotFoundException e) {
            throw new PersistenciaException("Driver JDBC do PostgreSQL não encontrado.", e);
        } catch (SQLException e) {
            throw new PersistenciaException(formatarErroSQL("Não foi possível criar o banco '" + banco + "'", e), e);
        }
    }

    public static void fecharConexao() {
        if (conexao != null) {
            try {
                if (!conexao.isClosed()) {
                    conexao.close();
                }
            } catch (SQLException ignored) {
            } finally {
                conexao = null;
            }
        }
    }

    public static String getResumoConexao() {
        return usuario + "@" + host + ":" + porta + "/" + banco;
    }

    public static String formatarErroSQL(String contexto, SQLException e) {
        String msg = e.getMessage();
        if (msg != null) {
            int idxWhere = msg.indexOf("\n  Where:");
            if (idxWhere < 0) idxWhere = msg.indexOf("\n  Onde:");
            if (idxWhere > 0) {
                msg = msg.substring(0, idxWhere).trim();
            }
            msg = msg.replaceFirst("^(ERROR|ERRO):\\s*", "");
            return msg;
        }
        return contexto + ": erro desconhecido no banco de dados.";
    }
}

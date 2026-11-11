package persistencia;

import excecoes.PersistenciaException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InicializadorBanco {

    public boolean verificarEsquemaInstalado() throws PersistenciaException {
        String sql = "SELECT COUNT(*) FROM information_schema.tables " +
                     "WHERE table_schema = 'public' AND table_name IN ('cliente', 'produto', 'venda', 'animal')";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) >= 4;
            }
            return false;
        } catch (SQLException e) {
            throw new PersistenciaException(Conexao.formatarErroSQL("Erro ao verificar tabelas do esquema", e), e);
        }
    }

    public boolean verificarBancoPossuiDados() throws PersistenciaException {
        if (!verificarEsquemaInstalado()) {
            return false;
        }
        Connection con = Conexao.getConexao();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM cliente")) {
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public void inicializarEsquema(boolean popularDadosIniciais) throws PersistenciaException {
        executarArquivoSQL("01_esquema_ddl.sql");
        executarArquivoSQL("02_functions_triggers.sql");
        if (popularDadosIniciais && !verificarBancoPossuiDados()) {
            executarArquivoSQL("03_dados_iniciais.sql");
        }
    }

    private void executarArquivoSQL(String nomeArquivo) throws PersistenciaException {
        Path caminho = localizarArquivoSQL(nomeArquivo);
        String conteudo;
        try {
            conteudo = Files.readString(caminho, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("Não foi possível ler o arquivo SQL: " + caminho.toAbsolutePath(), e);
        }

        List<String> comandos = separarComandosSQL(conteudo);
        Connection con = Conexao.getConexao();
        try (Statement st = con.createStatement()) {
            for (String cmd : comandos) {
                if (!cmd.isBlank()) {
                    st.execute(cmd);
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException(
                    Conexao.formatarErroSQL("Erro ao executar script " + nomeArquivo, e), e);
        }
    }

    private Path localizarArquivoSQL(String nomeArquivo) throws PersistenciaException {
        Path[] candidatos = new Path[]{
                Path.of("sql", nomeArquivo),
                Path.of("SistemaPetShop", "sql", nomeArquivo),
                Path.of("..", "sql", nomeArquivo)
        };
        for (Path p : candidatos) {
            if (Files.exists(p)) {
                return p;
            }
        }
        throw new PersistenciaException("Arquivo SQL não encontrado: sql/" + nomeArquivo);
    }

    /**
     * Divide um script SQL em instruções individuais respeitando comentários de linha (--),
     * strings simples ('...') e blocos dollar-quoted do PL/pgSQL ($$ ... $$).
     */
    private List<String> separarComandosSQL(String sql) {
        List<String> comandos = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        boolean emAspasSimples = false;
        boolean emDollarQuote = false;
        boolean emComentarioLinha = false;

        int n = sql.length();
        for (int i = 0; i < n; i++) {
            char c = sql.charAt(i);
            char prox = (i + 1 < n) ? sql.charAt(i + 1) : '\0';

            if (emComentarioLinha) {
                if (c == '\n') {
                    emComentarioLinha = false;
                    atual.append(c);
                }
                continue;
            }

            if (!emAspasSimples && !emDollarQuote && c == '-' && prox == '-') {
                emComentarioLinha = true;
                i++;
                continue;
            }

            if (!emAspasSimples && c == '$' && prox == '$') {
                emDollarQuote = !emDollarQuote;
                atual.append("$$");
                i++;
                continue;
            }

            if (!emDollarQuote && c == '\'') {
                if (emAspasSimples && prox == '\'') {
                    atual.append("''");
                    i++;
                    continue;
                }
                emAspasSimples = !emAspasSimples;
                atual.append(c);
                continue;
            }

            if (!emAspasSimples && !emDollarQuote && c == ';') {
                String cmd = atual.toString().trim();
                if (!cmd.isEmpty()) {
                    comandos.add(cmd);
                }
                atual.setLength(0);
            } else {
                atual.append(c);
            }
        }

        String restante = atual.toString().trim();
        if (!restante.isEmpty()) {
            comandos.add(restante);
        }
        return comandos;
    }
}

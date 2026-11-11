package apresentacao;

import excecoes.RegraNegocioException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class TemaUI {

    public static final Color COR_PRIMARIA = new Color(21, 101, 192);
    public static final Color COR_SECUNDARIA = new Color(46, 125, 50);
    public static final Color COR_PERIGO = new Color(198, 40, 40);
    public static final Color COR_NEUTRA = new Color(96, 125, 139);
    public static final Color COR_FUNDO = new Color(245, 247, 250);
    public static final Color COR_PAINEL = Color.WHITE;

    public static final Font FONTE_PADRAO = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONTE_NEGRITO = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);

    private static final DateTimeFormatter FMT_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat FMT_MOEDA;

    static {
        DecimalFormatSymbols sym = new DecimalFormatSymbols(Locale.forLanguageTag("pt-BR"));
        sym.setDecimalSeparator(',');
        sym.setGroupingSeparator('.');
        FMT_MOEDA = new DecimalFormat("#,##0.00", sym);
    }

    private TemaUI() {
    }

    public static void aplicarTemaGlobal() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }
        UIManager.put("Label.font", FONTE_PADRAO);
        UIManager.put("Button.font", FONTE_NEGRITO);
        UIManager.put("TextField.font", FONTE_PADRAO);
        UIManager.put("PasswordField.font", FONTE_PADRAO);
        UIManager.put("ComboBox.font", FONTE_PADRAO);
        UIManager.put("Table.font", FONTE_PADRAO);
        UIManager.put("TableHeader.font", FONTE_NEGRITO);
        UIManager.put("TabbedPane.font", FONTE_NEGRITO);
    }

    public static JButton criarBotao(String texto, Color corFundo) {
        JButton btn = new JButton(texto);
        btn.setBackground(corFundo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(FONTE_NEGRITO);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void estilizarTabela(JTable tabela) {
        tabela.setRowHeight(26);
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionBackground(new Color(227, 242, 253));
        tabela.setSelectionForeground(Color.BLACK);
        tabela.setGridColor(new Color(230, 230, 230));
        JTableHeader header = tabela.getTableHeader();
        if (header != null) {
            header.setReorderingAllowed(false);
            header.setFont(FONTE_NEGRITO);
        }
    }

    public static Border criarBordaTitulo(String titulo) {
        TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(210, 218, 226)),
                " " + titulo + " "
        );
        tb.setTitleFont(FONTE_NEGRITO);
        tb.setTitleColor(new Color(38, 50, 56));
        return BorderFactory.createCompoundBorder(tb, BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }

    public static void configurarPainel(JComponent comp) {
        comp.setBackground(COR_FUNDO);
        comp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public static LocalDate parseDataOpcional(String texto, String nomeCampo) throws RegraNegocioException {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        String t = texto.trim();
        try {
            if (t.contains("/")) {
                return LocalDate.parse(t, FMT_BR);
            }
            return LocalDate.parse(t);
        } catch (DateTimeParseException e) {
            throw new RegraNegocioException("Data inválida em '" + nomeCampo + "'. Use dd/MM/aaaa ou aaaa-MM-dd.");
        }
    }

    public static String formatarData(LocalDate data) {
        return data != null ? data.format(FMT_BR) : "";
    }

    public static BigDecimal parseDecimal(String texto, String nomeCampo) throws RegraNegocioException {
        if (texto == null || texto.isBlank()) {
            throw new RegraNegocioException("O campo '" + nomeCampo + "' é obrigatório.");
        }
        String limpo = texto.trim().replace("R$", "").trim();
        if (limpo.contains(",") && limpo.contains(".")) {
            limpo = limpo.replace(".", "").replace(",", ".");
        } else if (limpo.contains(",")) {
            limpo = limpo.replace(",", ".");
        }
        try {
            return new BigDecimal(limpo);
        } catch (NumberFormatException e) {
            throw new RegraNegocioException("Valor numérico inválido em '" + nomeCampo + "'.");
        }
    }

    public static int parseInteiro(String texto, String nomeCampo) throws RegraNegocioException {
        if (texto == null || texto.isBlank()) {
            throw new RegraNegocioException("O campo '" + nomeCampo + "' é obrigatório.");
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new RegraNegocioException("Informe um número inteiro válido em '" + nomeCampo + "'.");
        }
    }

    public static String formatarMoeda(BigDecimal valor) {
        if (valor == null) return "R$ 0,00";
        return "R$ " + FMT_MOEDA.format(valor);
    }

    public static void exibirErro(Component parent, Exception ex) {
        JOptionPane.showMessageDialog(parent, ex.getMessage(), "Atenção", JOptionPane.ERROR_MESSAGE);
    }

    public static void exibirSucesso(Component parent, String mensagem) {
        JOptionPane.showMessageDialog(parent, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
}

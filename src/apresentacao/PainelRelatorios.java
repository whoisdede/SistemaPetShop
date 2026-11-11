package apresentacao;

import dados.Cliente;
import negocio.SistemaPetShop;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;

public final class PainelRelatorios extends JPanel {

    private final SistemaPetShop sistema;
    private final JComboBox<Cliente> cbCliente = new JComboBox<>();
    private final JTextField txtDataIni = new JTextField("01/01/2026", 10);
    private final JTextField txtDataFim = new JTextField(10);
    private final JTextField txtLimite = new JTextField("10", 4);
    private final JLabel lblTituloResultado = new JLabel("Selecione uma consulta acima para visualizar os dados");
    private final DefaultTableModel modeloResultado = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public PainelRelatorios(SistemaPetShop sistema) {
        this.sistema = sistema;
        TemaUI.configurarPainel(this);
        setLayout(new BorderLayout(10, 10));
        txtDataFim.setText(TemaUI.formatarData(LocalDate.now().plusMonths(2)));

        JPanel topo = new JPanel(new GridLayout(2, 1, 6, 6));
        topo.setBackground(TemaUI.COR_PAINEL);
        topo.setBorder(TemaUI.criarBordaTitulo("Consultas Gerenciais e Funções PL/pgSQL do Banco"));

        JPanel linha1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        linha1.setOpaque(false);
        JButton btnEstoqueBaixo = TemaUI.criarBotao("Alerta de Estoque Baixo", TemaUI.COR_PERIGO);
        JButton btnRanking = TemaUI.criarBotao("Ranking Mais Vendidos", TemaUI.COR_PRIMARIA);
        JButton btnFaturamento = TemaUI.criarBotao("Faturamento no Período", TemaUI.COR_SECUNDARIA);
        JButton btnAdminsEstoque = TemaUI.criarBotao("Administradores de Estoque", TemaUI.COR_NEUTRA);

        btnEstoqueBaixo.addActionListener(e -> executarEstoqueBaixo());
        btnRanking.addActionListener(e -> executarRanking());
        btnFaturamento.addActionListener(e -> executarFaturamento());
        btnAdminsEstoque.addActionListener(e -> executarAdminsEstoque());

        linha1.add(btnEstoqueBaixo);
        linha1.add(new JLabel("Top N:"));
        linha1.add(txtLimite);
        linha1.add(btnRanking);
        linha1.add(new JLabel("De:"));
        linha1.add(txtDataIni);
        linha1.add(new JLabel("Até:"));
        linha1.add(txtDataFim);
        linha1.add(btnFaturamento);
        linha1.add(btnAdminsEstoque);

        JPanel linha2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        linha2.setOpaque(false);
        JButton btnResumoCliente = TemaUI.criarBotao("Total Gasto pelo Cliente", TemaUI.COR_PRIMARIA);
        JButton btnHistCliente = TemaUI.criarBotao("Itens Comprados pelo Cliente", TemaUI.COR_SECUNDARIA);
        btnResumoCliente.addActionListener(e -> executarResumoCliente());
        btnHistCliente.addActionListener(e -> executarHistoricoCliente());

        linha2.add(new JLabel("Cliente:"));
        linha2.add(cbCliente);
        linha2.add(btnResumoCliente);
        linha2.add(btnHistCliente);

        topo.add(linha1);
        topo.add(linha2);
        add(topo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(6, 6));
        centro.setBackground(TemaUI.COR_PAINEL);
        centro.setBorder(TemaUI.criarBordaTitulo("Resultado da Consulta"));
        lblTituloResultado.setFont(TemaUI.FONTE_NEGRITO);

        JTable tabela = new JTable(modeloResultado);
        TemaUI.estilizarTabela(tabela);

        centro.add(lblTituloResultado, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
    }

    public void recarregarDados() {
        try {
            cbCliente.removeAllItems();
            for (Cliente c : sistema.listarClientes()) {
                cbCliente.addItem(c);
            }
            if (modeloResultado.getColumnCount() == 0) {
                executarEstoqueBaixo();
            }
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void popularResultado(String titulo, String[] colunas, List<String[]> linhas) {
        lblTituloResultado.setText(titulo + " (" + linhas.size() + " registro(s))");
        modeloResultado.setRowCount(0);
        modeloResultado.setColumnIdentifiers(colunas);
        for (String[] l : linhas) {
            modeloResultado.addRow(l);
        }
    }

    private void executarEstoqueBaixo() {
        try {
            popularResultado(
                    "Função fn_produtos_estoque_baixo() — Produtos com saldo igual ou inferior ao mínimo",
                    new String[]{"ID Produto", "Produto", "Qtd. Atual", "Qtd. Mínima", "Diferença", "Urgência"},
                    sistema.relatorioEstoqueBaixo()
            );
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void executarRanking() {
        try {
            int limite = TemaUI.parseInteiro(txtLimite.getText(), "Top N");
            popularResultado(
                    "Função fn_ranking_produtos(" + limite + ") — Produtos mais vendidos",
                    new String[]{"Posição", "Produto", "Qtd. Total Vendida", "Faturamento Total", "Preço Médio"},
                    sistema.relatorioRankingProdutos(limite)
            );
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void executarFaturamento() {
        try {
            LocalDate ini = TemaUI.parseDataOpcional(txtDataIni.getText(), "Data Inicial");
            LocalDate fim = TemaUI.parseDataOpcional(txtDataFim.getText(), "Data Final");
            popularResultado(
                    "Função fn_faturamento_periodo() — Receita consolidada de Produtos e Serviços",
                    new String[]{"Categoria de Receita", "Quantidade de Operações", "Faturamento"},
                    sistema.relatorioFaturamentoPeriodo(ini, fim)
            );
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void executarAdminsEstoque() {
        try {
            popularResultado(
                    "Consulta Usuario_Estoque_Administra — Responsáveis pelos estoques",
                    new String[]{"Estoque", "Login do Usuário", "Funcionário", "Nível de Acesso"},
                    sistema.listarAdministradoresEstoque()
            );
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void executarResumoCliente() {
        try {
            Cliente c = (Cliente) cbCliente.getSelectedItem();
            popularResultado(
                    "Função fn_total_gasto_cliente() — Resumo financeiro do cliente",
                    new String[]{"Cliente", "Total em Produtos", "Total em Serviços", "Total Geral", "Nº Compras", "Nº Serviços"},
                    sistema.relatorioResumoGastoCliente(c)
            );
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void executarHistoricoCliente() {
        try {
            Cliente c = (Cliente) cbCliente.getSelectedItem();
            popularResultado(
                    "Função fn_relatorio_vendas_cliente() — Detalhamento de produtos comprados",
                    new String[]{"Cliente", "Data da Venda", "Produto", "Quantidade", "Preço Unitário", "Total do Item"},
                    sistema.relatorioHistoricoComprasCliente(c)
            );
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }
}

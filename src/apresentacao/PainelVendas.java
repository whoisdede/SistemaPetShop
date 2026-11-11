package apresentacao;

import dados.Cliente;
import dados.ItemVenda;
import dados.Produto;
import dados.Venda;
import negocio.SistemaPetShop;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class PainelVendas extends JPanel {

    private final SistemaPetShop sistema;

    // Nova Venda (PDV)
    private final JComboBox<Cliente> cbCliente = new JComboBox<>();
    private final JTextField txtDataVenda = new JTextField(10);
    private final JComboBox<Produto> cbProduto = new JComboBox<>();
    private final JTextField txtValorUnit = new JTextField(9);
    private final JTextField txtQtd = new JTextField("1", 5);
    private final JLabel lblTotalCarrinho = new JLabel("Total da Venda: R$ 0,00");

    private final DefaultTableModel modeloCarrinho;
    private final JTable tabelaCarrinho;
    private final List<ItemVenda> itensCarrinho = new ArrayList<>();

    // Histórico de Vendas + Itens
    private final DefaultTableModel modeloVendas;
    private final JTable tabelaVendas;
    private final DefaultTableModel modeloItensHistorico;
    private List<Venda> historicoVendas = new ArrayList<>();

    public PainelVendas(SistemaPetShop sistema) {
        this.sistema = sistema;
        TemaUI.configurarPainel(this);
        setLayout(new BorderLayout(10, 10));
        txtDataVenda.setText(TemaUI.formatarData(LocalDate.now()));
        lblTotalCarrinho.setFont(TemaUI.FONTE_TITULO);
        lblTotalCarrinho.setForeground(TemaUI.COR_SECUNDARIA);

        cbProduto.addActionListener(e -> {
            Produto p = (Produto) cbProduto.getSelectedItem();
            if (p != null && p.getPreco() != null) {
                txtValorUnit.setText(p.getPreco().toPlainString());
            }
        });

        // =====================================================
        // ESQUERDA: PDV - NOVA VENDA
        // =====================================================
        JPanel pPdv = new JPanel(new BorderLayout(8, 8));
        pPdv.setBackground(TemaUI.COR_PAINEL);
        pPdv.setBorder(TemaUI.criarBordaTitulo("Ponto de Venda (Verificação e Baixa Automática de Estoque)"));

        JPanel formVenda = new JPanel(new GridBagLayout());
        formVenda.setOpaque(false);
        GridBagConstraints gv = new GridBagConstraints();
        gv.insets = new Insets(4, 5, 4, 5);
        gv.fill = GridBagConstraints.HORIZONTAL;

        gv.gridx = 0; gv.gridy = 0; formVenda.add(new JLabel("Cliente *:"), gv);
        gv.gridx = 1; gv.gridwidth = 2; gv.weightx = 1.0; formVenda.add(cbCliente, gv);
        gv.gridx = 3; gv.gridwidth = 1; gv.weightx = 0; formVenda.add(new JLabel("Data:"), gv);
        gv.gridx = 4; formVenda.add(txtDataVenda, gv);

        gv.gridx = 0; gv.gridy = 1; formVenda.add(new JLabel("Produto *:"), gv);
        gv.gridx = 1; gv.gridwidth = 4; gv.weightx = 1.0; formVenda.add(cbProduto, gv);

        gv.gridx = 0; gv.gridy = 2; gv.gridwidth = 1; gv.weightx = 0;
        formVenda.add(new JLabel("Valor Unit. (R$):"), gv);
        gv.gridx = 1; formVenda.add(txtValorUnit, gv);
        gv.gridx = 2; formVenda.add(new JLabel("Qtd *:"), gv);
        gv.gridx = 3; formVenda.add(txtQtd, gv);

        JButton btnAddItem = TemaUI.criarBotao("+ Adicionar Item", TemaUI.COR_PRIMARIA);
        btnAddItem.addActionListener(e -> adicionarAoCarrinho());
        gv.gridx = 4; formVenda.add(btnAddItem, gv);

        modeloCarrinho = new DefaultTableModel(
                new Object[]{"Produto", "Qtd", "Valor Unit.", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaCarrinho = new JTable(modeloCarrinho);
        TemaUI.estilizarTabela(tabelaCarrinho);

        JPanel rodapePdv = new JPanel(new BorderLayout(8, 4));
        rodapePdv.setOpaque(false);
        rodapePdv.add(lblTotalCarrinho, BorderLayout.WEST);

        JPanel botoesCarrinho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        botoesCarrinho.setOpaque(false);
        JButton btnRemoverItem = TemaUI.criarBotao("Remover Item", TemaUI.COR_NEUTRA);
        JButton btnLimparCarrinho = TemaUI.criarBotao("Limpar", TemaUI.COR_PERIGO);
        JButton btnFinalizar = TemaUI.criarBotao("Finalizar Venda", TemaUI.COR_SECUNDARIA);

        btnRemoverItem.addActionListener(e -> removerItemCarrinho());
        btnLimparCarrinho.addActionListener(e -> limparCarrinho());
        btnFinalizar.addActionListener(e -> finalizarVenda());

        botoesCarrinho.add(btnRemoverItem);
        botoesCarrinho.add(btnLimparCarrinho);
        botoesCarrinho.add(btnFinalizar);
        rodapePdv.add(botoesCarrinho, BorderLayout.EAST);

        pPdv.add(formVenda, BorderLayout.NORTH);
        pPdv.add(new JScrollPane(tabelaCarrinho), BorderLayout.CENTER);
        pPdv.add(rodapePdv, BorderLayout.SOUTH);

        // =====================================================
        // DIREITA: HISTÓRICO DE VENDAS E ITENS
        // =====================================================
        JPanel pHist = new JPanel(new BorderLayout(8, 8));
        pHist.setBackground(TemaUI.COR_PAINEL);
        pHist.setBorder(TemaUI.criarBordaTitulo("Histórico de Vendas Registradas (Clique para ver itens)"));

        modeloVendas = new DefaultTableModel(
                new Object[]{"Nº Venda", "Data", "Cliente", "Qtd. Itens", "Valor Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaVendas = new JTable(modeloVendas);
        tabelaVendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TemaUI.estilizarTabela(tabelaVendas);
        tabelaVendas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) exibirItensDaVendaSelecionada();
        });

        modeloItensHistorico = new DefaultTableModel(
                new Object[]{"ID Prod.", "Produto", "Quantidade", "Valor Unitário", "Subtotal Item"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabelaItensHist = new JTable(modeloItensHistorico);
        TemaUI.estilizarTabela(tabelaItensHist);

        JPanel painelItens = new JPanel(new BorderLayout());
        painelItens.setBackground(TemaUI.COR_PAINEL);
        painelItens.setBorder(TemaUI.criarBordaTitulo("Itens da Venda Selecionada (Venda_Produto_Contem)"));
        painelItens.add(new JScrollPane(tabelaItensHist), BorderLayout.CENTER);

        JSplitPane splitHist = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tabelaVendas), painelItens);
        splitHist.setResizeWeight(0.55);
        splitHist.setBorder(null);
        pHist.add(splitHist, BorderLayout.CENTER);

        JSplitPane splitPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pPdv, pHist);
        splitPrincipal.setResizeWeight(0.48);
        splitPrincipal.setBorder(null);
        add(splitPrincipal, BorderLayout.CENTER);
    }

    public void recarregarDados() {
        try {
            cbCliente.removeAllItems();
            for (Cliente c : sistema.listarClientes()) {
                cbCliente.addItem(c);
            }

            cbProduto.removeAllItems();
            for (Produto p : sistema.listarProdutos()) {
                cbProduto.addItem(p);
            }

            historicoVendas = sistema.listarVendas();
            modeloVendas.setRowCount(0);
            modeloItensHistorico.setRowCount(0);
            for (Venda v : historicoVendas) {
                modeloVendas.addRow(new Object[]{
                        "#" + v.getIdVenda(),
                        TemaUI.formatarData(v.getDataVenda()),
                        v.getCliente() != null ? v.getCliente().getNome() : "",
                        v.getItens().size(),
                        TemaUI.formatarMoeda(v.getValorTotal())
                });
            }
            if (!historicoVendas.isEmpty()) {
                tabelaVendas.setRowSelectionInterval(0, 0);
            }
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void adicionarAoCarrinho() {
        try {
            Produto p = (Produto) cbProduto.getSelectedItem();
            if (p == null) return;
            BigDecimal valor = TemaUI.parseDecimal(txtValorUnit.getText(), "Valor Unitário");
            int qtd = TemaUI.parseInteiro(txtQtd.getText(), "Quantidade");
            if (qtd <= 0) {
                throw new excecoes.RegraNegocioException("A quantidade deve ser maior que zero.");
            }

            itensCarrinho.add(new ItemVenda(p, valor, qtd));
            atualizarTabelaCarrinho();
            txtQtd.setText("1");
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void removerItemCarrinho() {
        int row = tabelaCarrinho.getSelectedRow();
        if (row >= 0 && row < itensCarrinho.size()) {
            itensCarrinho.remove(row);
            atualizarTabelaCarrinho();
        }
    }

    private void limparCarrinho() {
        itensCarrinho.clear();
        atualizarTabelaCarrinho();
    }

    private void atualizarTabelaCarrinho() {
        modeloCarrinho.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        for (ItemVenda item : itensCarrinho) {
            modeloCarrinho.addRow(new Object[]{
                    item.getProduto().getNomeProduto(),
                    item.getQtd(),
                    TemaUI.formatarMoeda(item.getValorUnit()),
                    TemaUI.formatarMoeda(item.getSubtotal())
            });
            total = total.add(item.getSubtotal());
        }
        lblTotalCarrinho.setText("Total da Venda: " + TemaUI.formatarMoeda(total));
    }

    private void finalizarVenda() {
        try {
            Venda venda = new Venda();
            venda.setCliente((Cliente) cbCliente.getSelectedItem());
            LocalDate dt = TemaUI.parseDataOpcional(txtDataVenda.getText(), "Data da Venda");
            venda.setDataVenda(dt != null ? dt : LocalDate.now());
            venda.setItens(new ArrayList<>(itensCarrinho));

            sistema.registrarVenda(venda);
            limparCarrinho();
            recarregarDados();
            TemaUI.exibirSucesso(this,
                    "Venda #" + venda.getIdVenda() + " registrada com sucesso!\n" +
                    "Estoque dos itens foi baixado automaticamente pelo banco de dados.");
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void exibirItensDaVendaSelecionada() {
        int row = tabelaVendas.getSelectedRow();
        modeloItensHistorico.setRowCount(0);
        if (row >= 0 && row < historicoVendas.size()) {
            Venda v = historicoVendas.get(row);
            for (ItemVenda item : v.getItens()) {
                modeloItensHistorico.addRow(new Object[]{
                        item.getProduto().getIdProduto(),
                        item.getProduto().getNomeProduto(),
                        item.getQtd(),
                        TemaUI.formatarMoeda(item.getValorUnit()),
                        TemaUI.formatarMoeda(item.getSubtotal())
                });
            }
        }
    }
}

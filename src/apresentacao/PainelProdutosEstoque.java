package apresentacao;

import dados.Estoque;
import dados.Produto;
import dados.Usuario;
import negocio.SistemaPetShop;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class PainelProdutosEstoque extends JPanel {

    private final SistemaPetShop sistema;

    // Campos Produto
    private final JTextField txtIdProd = new JTextField(5);
    private final JTextField txtNomeProd = new JTextField(20);
    private final JTextField txtPreco = new JTextField(9);
    private final JTextField txtCusto = new JTextField(9);
    private final JComboBox<Estoque> cbEstoqueProd = new JComboBox<>();
    private final DefaultTableModel modeloProdutos;
    private final JTable tabelaProdutos;
    private List<Produto> listaProdutos = new ArrayList<>();

    // Campos Estoque + Administradores
    private final JTextField txtIdEst = new JTextField(4);
    private final JTextField txtQtdMin = new JTextField(6);
    private final JTextField txtQtdAtual = new JTextField(6);
    private final JComboBox<Usuario> cbUsuarioAdmin = new JComboBox<>();
    private final JComboBox<Estoque> cbEstoqueAdmin = new JComboBox<>();
    private final DefaultTableModel modeloEstoques;
    private final JTable tabelaEstoques;
    private List<Estoque> listaEstoques = new ArrayList<>();

    public PainelProdutosEstoque(SistemaPetShop sistema) {
        this.sistema = sistema;
        TemaUI.configurarPainel(this);
        setLayout(new BorderLayout(10, 10));

        txtIdProd.setEditable(false);
        txtIdProd.setBackground(new Color(235, 238, 242));
        txtIdEst.setEditable(false);
        txtIdEst.setBackground(new Color(235, 238, 242));

        // =====================================================
        // LADO ESQUERDO: PRODUTOS
        // =====================================================
        JPanel painelProd = new JPanel(new BorderLayout(8, 8));
        painelProd.setBackground(TemaUI.COR_PAINEL);
        painelProd.setBorder(TemaUI.criarBordaTitulo("Produtos Comercializados"));

        JPanel formProd = new JPanel(new GridBagLayout());
        formProd.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formProd.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; formProd.add(txtIdProd, gbc);
        gbc.gridx = 2; formProd.add(new JLabel("Produto *:"), gbc);
        gbc.gridx = 3; gbc.gridwidth = 3; gbc.weightx = 1.0; formProd.add(txtNomeProd, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0;
        formProd.add(new JLabel("Preço Venda (R$) *:"), gbc);
        gbc.gridx = 1; formProd.add(txtPreco, gbc);
        gbc.gridx = 2; formProd.add(new JLabel("Custo (R$) *:"), gbc);
        gbc.gridx = 3; formProd.add(txtCusto, gbc);
        gbc.gridx = 4; formProd.add(new JLabel("Estoque *:"), gbc);
        gbc.gridx = 5; gbc.weightx = 1.0; formProd.add(cbEstoqueProd, gbc);

        JPanel botoesProd = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        botoesProd.setOpaque(false);
        JButton btnLimparProd = TemaUI.criarBotao("Limpar", TemaUI.COR_NEUTRA);
        JButton btnCadProd = TemaUI.criarBotao("Cadastrar Produto", TemaUI.COR_SECUNDARIA);
        JButton btnEditProd = TemaUI.criarBotao("Atualizar", TemaUI.COR_PRIMARIA);
        JButton btnDelProd = TemaUI.criarBotao("Excluir", TemaUI.COR_PERIGO);

        btnLimparProd.addActionListener(e -> limparProduto());
        btnCadProd.addActionListener(e -> cadastrarProduto());
        btnEditProd.addActionListener(e -> atualizarProduto());
        btnDelProd.addActionListener(e -> excluirProduto());

        botoesProd.add(btnLimparProd);
        botoesProd.add(btnCadProd);
        botoesProd.add(btnEditProd);
        botoesProd.add(btnDelProd);

        JPanel topoProd = new JPanel(new BorderLayout(4, 4));
        topoProd.setOpaque(false);
        topoProd.add(formProd, BorderLayout.CENTER);
        topoProd.add(botoesProd, BorderLayout.SOUTH);

        modeloProdutos = new DefaultTableModel(
                new Object[]{"ID", "Nome do Produto", "Preço Venda", "Custo", "Estoque", "Saldo Atual", "Situação"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProdutos = new JTable(modeloProdutos);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TemaUI.estilizarTabela(tabelaProdutos);
        tabelaProdutos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherProdutoSelecionado();
        });

        painelProd.add(topoProd, BorderLayout.NORTH);
        painelProd.add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);

        // =====================================================
        // LADO DIREITO: ESTOQUES E VÍNCULO DE ADMINISTRADOR
        // =====================================================
        JPanel painelEst = new JPanel(new BorderLayout(8, 8));
        painelEst.setBackground(TemaUI.COR_PAINEL);
        painelEst.setBorder(TemaUI.criarBordaTitulo("Lotes/Locais de Estoque (Subtotal via Trigger)"));

        JPanel formEst = new JPanel(new GridBagLayout());
        formEst.setOpaque(false);
        GridBagConstraints ge = new GridBagConstraints();
        ge.insets = new Insets(4, 5, 4, 5);
        ge.fill = GridBagConstraints.HORIZONTAL;

        ge.gridx = 0; ge.gridy = 0; formEst.add(new JLabel("ID:"), ge);
        ge.gridx = 1; formEst.add(txtIdEst, ge);
        ge.gridx = 2; formEst.add(new JLabel("Qtd. Mínima *:"), ge);
        ge.gridx = 3; formEst.add(txtQtdMin, ge);
        ge.gridx = 4; formEst.add(new JLabel("Qtd. Atual *:"), ge);
        ge.gridx = 5; formEst.add(txtQtdAtual, ge);

        JPanel botoesEst = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        botoesEst.setOpaque(false);
        JButton btnNovoEst = TemaUI.criarBotao("Criar Estoque", TemaUI.COR_SECUNDARIA);
        JButton btnAtualizarEst = TemaUI.criarBotao("Salvar Estoque", TemaUI.COR_PRIMARIA);
        btnNovoEst.addActionListener(e -> criarEstoque());
        btnAtualizarEst.addActionListener(e -> salvarEstoque());
        botoesEst.add(btnNovoEst);
        botoesEst.add(btnAtualizarEst);

        JPanel topoEst = new JPanel(new BorderLayout());
        topoEst.setOpaque(false);
        topoEst.add(formEst, BorderLayout.CENTER);
        topoEst.add(botoesEst, BorderLayout.SOUTH);

        modeloEstoques = new DefaultTableModel(
                new Object[]{"ID Estoque", "Qtd. Mínima", "Qtd. Atual", "Subtotal Calculado", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaEstoques = new JTable(modeloEstoques);
        tabelaEstoques.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TemaUI.estilizarTabela(tabelaEstoques);
        tabelaEstoques.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherEstoqueSelecionado();
        });

        JPanel rodapeAdmin = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        rodapeAdmin.setOpaque(false);
        rodapeAdmin.setBorder(TemaUI.criarBordaTitulo("Vincular Administrador (Usuario_Estoque_Administra)"));
        JButton btnVincular = TemaUI.criarBotao("Vincular", TemaUI.COR_PRIMARIA);
        btnVincular.addActionListener(e -> vincularAdmin());
        rodapeAdmin.add(new JLabel("Usuário:"));
        rodapeAdmin.add(cbUsuarioAdmin);
        rodapeAdmin.add(new JLabel("Estoque:"));
        rodapeAdmin.add(cbEstoqueAdmin);
        rodapeAdmin.add(btnVincular);

        painelEst.add(topoEst, BorderLayout.NORTH);
        painelEst.add(new JScrollPane(tabelaEstoques), BorderLayout.CENTER);
        painelEst.add(rodapeAdmin, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelProd, painelEst);
        split.setResizeWeight(0.58);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);
    }

    public void recarregarDados() {
        try {
            listaEstoques = sistema.listarEstoques();
            cbEstoqueProd.removeAllItems();
            cbEstoqueAdmin.removeAllItems();
            modeloEstoques.setRowCount(0);
            for (Estoque e : listaEstoques) {
                cbEstoqueProd.addItem(e);
                cbEstoqueAdmin.addItem(e);
                modeloEstoques.addRow(new Object[]{
                        "Estoque #" + e.getIdEstoque(),
                        e.getQuantidadeMinima(),
                        e.getQuantidadeAtual(),
                        TemaUI.formatarMoeda(e.getSubtotal()),
                        e.isAbaixoDoMinimo() ? "ABAIXO DO MÍNIMO" : "NORMAL"
                });
            }

            cbUsuarioAdmin.removeAllItems();
            for (Usuario u : sistema.listarUsuarios()) {
                cbUsuarioAdmin.addItem(u);
            }

            listaProdutos = sistema.listarProdutos();
            modeloProdutos.setRowCount(0);
            for (Produto p : listaProdutos) {
                Estoque est = p.getEstoque();
                String status = "Sem estoque";
                int saldo = 0;
                if (est != null) {
                    saldo = est.getQuantidadeAtual();
                    status = est.isAbaixoDoMinimo() ? "BAIXO / CRÍTICO" : "DISPONÍVEL";
                }
                modeloProdutos.addRow(new Object[]{
                        p.getIdProduto(),
                        p.getNomeProduto(),
                        TemaUI.formatarMoeda(p.getPreco()),
                        TemaUI.formatarMoeda(p.getCusto()),
                        est != null ? "#" + est.getIdEstoque() : "-",
                        saldo,
                        status
                });
            }
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void preencherProdutoSelecionado() {
        int row = tabelaProdutos.getSelectedRow();
        if (row >= 0 && row < listaProdutos.size()) {
            Produto p = listaProdutos.get(row);
            txtIdProd.setText(String.valueOf(p.getIdProduto()));
            txtNomeProd.setText(p.getNomeProduto());
            txtPreco.setText(p.getPreco() != null ? p.getPreco().toPlainString() : "");
            txtCusto.setText(p.getCusto() != null ? p.getCusto().toPlainString() : "");
            if (p.getEstoque() != null) {
                cbEstoqueProd.setSelectedItem(p.getEstoque());
            }
        }
    }

    private void preencherEstoqueSelecionado() {
        int row = tabelaEstoques.getSelectedRow();
        if (row >= 0 && row < listaEstoques.size()) {
            Estoque e = listaEstoques.get(row);
            txtIdEst.setText(String.valueOf(e.getIdEstoque()));
            txtQtdMin.setText(String.valueOf(e.getQuantidadeMinima()));
            txtQtdAtual.setText(String.valueOf(e.getQuantidadeAtual()));
        }
    }

    private void limparProduto() {
        txtIdProd.setText("");
        txtNomeProd.setText("");
        txtPreco.setText("");
        txtCusto.setText("");
        tabelaProdutos.clearSelection();
    }

    private void cadastrarProduto() {
        try {
            Produto p = new Produto(
                    0,
                    txtNomeProd.getText().trim(),
                    TemaUI.parseDecimal(txtPreco.getText(), "Preço de Venda"),
                    TemaUI.parseDecimal(txtCusto.getText(), "Custo"),
                    (Estoque) cbEstoqueProd.getSelectedItem()
            );
            sistema.cadastrarProduto(p);
            limparProduto();
            recarregarDados();
            TemaUI.exibirSucesso(this, "Produto cadastrado com sucesso!");
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void atualizarProduto() {
        try {
            int id = TemaUI.parseInteiro(txtIdProd.getText(), "ID do Produto");
            Produto p = new Produto(
                    id,
                    txtNomeProd.getText().trim(),
                    TemaUI.parseDecimal(txtPreco.getText(), "Preço de Venda"),
                    TemaUI.parseDecimal(txtCusto.getText(), "Custo"),
                    (Estoque) cbEstoqueProd.getSelectedItem()
            );
            sistema.atualizarProduto(p);
            limparProduto();
            recarregarDados();
            TemaUI.exibirSucesso(this, "Produto atualizado com sucesso!");
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void excluirProduto() {
        try {
            int id = TemaUI.parseInteiro(txtIdProd.getText(), "ID do Produto");
            int conf = JOptionPane.showConfirmDialog(this, "Deseja excluir o produto ID " + id + "?",
                    "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                sistema.excluirProduto(id);
                limparProduto();
                recarregarDados();
                TemaUI.exibirSucesso(this, "Produto excluído com sucesso!");
            }
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void criarEstoque() {
        try {
            Estoque e = new Estoque(
                    0,
                    TemaUI.parseInteiro(txtQtdMin.getText(), "Quantidade Mínima"),
                    TemaUI.parseInteiro(txtQtdAtual.getText(), "Quantidade Atual"),
                    BigDecimal.ZERO
            );
            sistema.cadastrarEstoque(e);
            txtIdEst.setText("");
            txtQtdMin.setText("");
            txtQtdAtual.setText("");
            recarregarDados();
            TemaUI.exibirSucesso(this, "Estoque #" + e.getIdEstoque() + " criado com sucesso!");
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void salvarEstoque() {
        try {
            int id = TemaUI.parseInteiro(txtIdEst.getText(), "ID do Estoque");
            Estoque e = new Estoque(
                    id,
                    TemaUI.parseInteiro(txtQtdMin.getText(), "Quantidade Mínima"),
                    TemaUI.parseInteiro(txtQtdAtual.getText(), "Quantidade Atual"),
                    BigDecimal.ZERO
            );
            sistema.atualizarEstoque(e);
            recarregarDados();
            TemaUI.exibirSucesso(this, "Estoque atualizado (subtotal recalculado pelo trigger)!");
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void vincularAdmin() {
        try {
            Usuario u = (Usuario) cbUsuarioAdmin.getSelectedItem();
            Estoque e = (Estoque) cbEstoqueAdmin.getSelectedItem();
            sistema.vincularAdministradorEstoque(u, e);
            TemaUI.exibirSucesso(this, "Usuário vinculado como administrador do estoque selecionado!");
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }
}

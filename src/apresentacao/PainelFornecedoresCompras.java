package apresentacao;

import dados.CompraFornecedor;
import dados.Fornecedor;
import dados.Produto;
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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class PainelFornecedoresCompras extends JPanel {

    private final SistemaPetShop sistema;

    // Fornecedores
    private final JTextField txtNomeForn = new JTextField(16);
    private final JTextField txtRazaoSocial = new JTextField(16);
    private final JTextField txtCnpj = new JTextField(15);
    private final JTextField txtEnderecoForn = new JTextField(20);
    private final DefaultTableModel modeloFornecedores;
    private final JTable tabelaFornecedores;
    private List<Fornecedor> listaFornecedores = new ArrayList<>();

    // Compras de reposição
    private final JComboBox<Fornecedor> cbFornecedor = new JComboBox<>();
    private final JComboBox<Produto> cbProduto = new JComboBox<>();
    private final JTextField txtDataCompra = new JTextField(10);
    private final JTextField txtQtdCompra = new JTextField("10", 6);
    private final DefaultTableModel modeloCompras;

    public PainelFornecedoresCompras(SistemaPetShop sistema) {
        this.sistema = sistema;
        TemaUI.configurarPainel(this);
        setLayout(new BorderLayout(10, 10));
        txtDataCompra.setText(TemaUI.formatarData(LocalDate.now()));

        // =====================================================
        // ESQUERDA: FORNECEDORES
        // =====================================================
        JPanel pForn = new JPanel(new BorderLayout(8, 8));
        pForn.setBackground(TemaUI.COR_PAINEL);
        pForn.setBorder(TemaUI.criarBordaTitulo("Fornecedores (Validação e Máscara de CNPJ via Trigger)"));

        JPanel formForn = new JPanel(new GridBagLayout());
        formForn.setOpaque(false);
        GridBagConstraints gf = new GridBagConstraints();
        gf.insets = new Insets(4, 5, 4, 5);
        gf.fill = GridBagConstraints.HORIZONTAL;

        gf.gridx = 0; gf.gridy = 0; formForn.add(new JLabel("Nome Fantasia *:"), gf);
        gf.gridx = 1; gf.weightx = 1.0; formForn.add(txtNomeForn, gf);
        gf.gridx = 0; gf.gridy = 1; gf.weightx = 0; formForn.add(new JLabel("Razão Social:"), gf);
        gf.gridx = 1; gf.weightx = 1.0; formForn.add(txtRazaoSocial, gf);
        gf.gridx = 0; gf.gridy = 2; gf.weightx = 0; formForn.add(new JLabel("CNPJ (14 dígitos):"), gf);
        gf.gridx = 1; gf.weightx = 1.0; formForn.add(txtCnpj, gf);
        gf.gridx = 0; gf.gridy = 3; gf.weightx = 0; formForn.add(new JLabel("Endereço:"), gf);
        gf.gridx = 1; gf.weightx = 1.0; formForn.add(txtEnderecoForn, gf);

        JPanel botoesForn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        botoesForn.setOpaque(false);
        JButton btnCadForn = TemaUI.criarBotao("Cadastrar Fornecedor", TemaUI.COR_SECUNDARIA);
        JButton btnDelForn = TemaUI.criarBotao("Excluir Selecionado", TemaUI.COR_PERIGO);
        btnCadForn.addActionListener(e -> cadastrarFornecedor());
        btnDelForn.addActionListener(e -> excluirFornecedor());
        botoesForn.add(btnCadForn);
        botoesForn.add(btnDelForn);

        JPanel topoForn = new JPanel(new BorderLayout());
        topoForn.setOpaque(false);
        topoForn.add(formForn, BorderLayout.CENTER);
        topoForn.add(botoesForn, BorderLayout.SOUTH);

        modeloFornecedores = new DefaultTableModel(
                new Object[]{"ID", "Nome Fantasia", "Razão Social", "CNPJ", "Endereço"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaFornecedores = new JTable(modeloFornecedores);
        tabelaFornecedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TemaUI.estilizarTabela(tabelaFornecedores);

        pForn.add(topoForn, BorderLayout.NORTH);
        pForn.add(new JScrollPane(tabelaFornecedores), BorderLayout.CENTER);

        // =====================================================
        // DIREITA: COMPRAS / ENTRADA DE ESTOQUE
        // =====================================================
        JPanel pComp = new JPanel(new BorderLayout(8, 8));
        pComp.setBackground(TemaUI.COR_PAINEL);
        pComp.setBorder(TemaUI.criarBordaTitulo("Compras de Fornecedores (Entrada Automática em Estoque via Trigger)"));

        JPanel formComp = new JPanel(new GridBagLayout());
        formComp.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 5, 4, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0; gc.gridy = 0; formComp.add(new JLabel("Fornecedor *:"), gc);
        gc.gridx = 1; gc.gridwidth = 3; gc.weightx = 1.0; formComp.add(cbFornecedor, gc);

        gc.gridx = 0; gc.gridy = 1; gc.gridwidth = 1; gc.weightx = 0;
        formComp.add(new JLabel("Produto *:"), gc);
        gc.gridx = 1; gc.gridwidth = 3; gc.weightx = 1.0; formComp.add(cbProduto, gc);

        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 1; gc.weightx = 0;
        formComp.add(new JLabel("Data da Compra:"), gc);
        gc.gridx = 1; formComp.add(txtDataCompra, gc);
        gc.gridx = 2; formComp.add(new JLabel("Quantidade *:"), gc);
        gc.gridx = 3; formComp.add(txtQtdCompra, gc);

        JPanel botoesComp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        botoesComp.setOpaque(false);
        JButton btnRegCompra = TemaUI.criarBotao("Registrar Entrada de Mercadoria", TemaUI.COR_PRIMARIA);
        btnRegCompra.addActionListener(e -> registrarCompra());
        botoesComp.add(btnRegCompra);

        JPanel topoComp = new JPanel(new BorderLayout());
        topoComp.setOpaque(false);
        topoComp.add(formComp, BorderLayout.CENTER);
        topoComp.add(botoesComp, BorderLayout.SOUTH);

        modeloCompras = new DefaultTableModel(
                new Object[]{"Data", "Fornecedor", "Produto", "Qtd. Recebida"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabelaCompras = new JTable(modeloCompras);
        TemaUI.estilizarTabela(tabelaCompras);

        pComp.add(topoComp, BorderLayout.NORTH);
        pComp.add(new JScrollPane(tabelaCompras), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pForn, pComp);
        split.setResizeWeight(0.48);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);
    }

    public void recarregarDados() {
        try {
            listaFornecedores = sistema.listarFornecedores();
            cbFornecedor.removeAllItems();
            modeloFornecedores.setRowCount(0);
            for (Fornecedor f : listaFornecedores) {
                cbFornecedor.addItem(f);
                modeloFornecedores.addRow(new Object[]{
                        f.getIdFornecedor(),
                        f.getNome(),
                        f.getRazaoSocial() != null ? f.getRazaoSocial() : "",
                        f.getCnpj() != null ? f.getCnpj() : "",
                        f.getEndereco() != null ? f.getEndereco() : ""
                });
            }

            cbProduto.removeAllItems();
            for (Produto p : sistema.listarProdutos()) {
                cbProduto.addItem(p);
            }

            modeloCompras.setRowCount(0);
            for (CompraFornecedor cf : sistema.listarComprasFornecedor()) {
                modeloCompras.addRow(new Object[]{
                        TemaUI.formatarData(cf.getData()),
                        cf.getFornecedor().getNome(),
                        cf.getProduto().getNomeProduto(),
                        "+" + cf.getQtd() + " un."
                });
            }
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void cadastrarFornecedor() {
        try {
            Fornecedor f = new Fornecedor(
                    0,
                    txtNomeForn.getText().trim(),
                    txtRazaoSocial.getText().trim(),
                    txtCnpj.getText().trim(),
                    txtEnderecoForn.getText().trim()
            );
            sistema.cadastrarFornecedor(f);
            txtNomeForn.setText("");
            txtRazaoSocial.setText("");
            txtCnpj.setText("");
            txtEnderecoForn.setText("");
            recarregarDados();
            TemaUI.exibirSucesso(this, "Fornecedor cadastrado! CNPJ validado/formatado: " +
                    (f.getCnpj() != null ? f.getCnpj() : "(não informado)"));
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void excluirFornecedor() {
        int row = tabelaFornecedores.getSelectedRow();
        if (row < 0 || row >= listaFornecedores.size()) {
            JOptionPane.showMessageDialog(this, "Selecione um fornecedor na tabela para excluir.");
            return;
        }
        Fornecedor f = listaFornecedores.get(row);
        int conf = JOptionPane.showConfirmDialog(this,
                "Deseja excluir o fornecedor '" + f.getNome() + "'?",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                sistema.excluirFornecedor(f.getIdFornecedor());
                recarregarDados();
                TemaUI.exibirSucesso(this, "Fornecedor excluído com sucesso!");
            } catch (Exception ex) {
                TemaUI.exibirErro(this, ex);
            }
        }
    }

    private void registrarCompra() {
        try {
            LocalDate data = TemaUI.parseDataOpcional(txtDataCompra.getText(), "Data da Compra");
            int qtd = TemaUI.parseInteiro(txtQtdCompra.getText(), "Quantidade");
            CompraFornecedor cf = new CompraFornecedor(
                    (Fornecedor) cbFornecedor.getSelectedItem(),
                    (Produto) cbProduto.getSelectedItem(),
                    data != null ? data : LocalDate.now(),
                    qtd
            );
            sistema.registrarCompraFornecedor(cf);
            recarregarDados();
            TemaUI.exibirSucesso(this,
                    "Compra registrada! O estoque do produto foi incrementado automaticamente em +" + qtd + " unidades.");
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }
}

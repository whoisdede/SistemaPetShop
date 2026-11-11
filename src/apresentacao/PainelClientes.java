package apresentacao;

import dados.Cliente;
import negocio.SistemaPetShop;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import java.util.ArrayList;
import java.util.List;

public final class PainelClientes extends JPanel {

    private final SistemaPetShop sistema;
    private final JTextField txtId = new JTextField(6);
    private final JTextField txtNome = new JTextField(22);
    private final JTextField txtEmail = new JTextField(20);
    private final JTextField txtDataNasc = new JTextField(12);
    private final JTextField txtEndereco = new JTextField(26);
    private final JTextField txtBusca = new JTextField(20);

    private final DefaultTableModel modeloTabela;
    private final JTable tabela;
    private List<Cliente> listaAtual = new ArrayList<>();

    public PainelClientes(SistemaPetShop sistema) {
        this.sistema = sistema;
        TemaUI.configurarPainel(this);
        setLayout(new BorderLayout(10, 10));

        txtId.setEditable(false);
        txtId.setBackground(new Color(235, 238, 242));

        JPanel cardForm = new JPanel(new BorderLayout(8, 8));
        cardForm.setBackground(TemaUI.COR_PAINEL);
        cardForm.setBorder(TemaUI.criarBordaTitulo("Cadastro e Manutenção de Clientes"));

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; campos.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; campos.add(txtId, gbc);
        gbc.gridx = 2; campos.add(new JLabel("Nome Completo *:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0; campos.add(txtNome, gbc);
        gbc.gridx = 4; gbc.weightx = 0; campos.add(new JLabel("Data Nasc. (dd/MM/aaaa):"), gbc);
        gbc.gridx = 5; campos.add(txtDataNasc, gbc);

        gbc.gridx = 0; gbc.gridy = 1; campos.add(new JLabel("E-mail:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.5; campos.add(txtEmail, gbc);
        gbc.gridx = 3; gbc.gridwidth = 1; gbc.weightx = 0; campos.add(new JLabel("Endereço:"), gbc);
        gbc.gridx = 4; gbc.gridwidth = 2; gbc.weightx = 1.0; campos.add(txtEndereco, gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        botoes.setOpaque(false);
        JButton btnLimpar = TemaUI.criarBotao("Limpar Campos", TemaUI.COR_NEUTRA);
        JButton btnInserir = TemaUI.criarBotao("Cadastrar Cliente", TemaUI.COR_SECUNDARIA);
        JButton btnAtualizar = TemaUI.criarBotao("Salvar Alterações", TemaUI.COR_PRIMARIA);
        JButton btnExcluir = TemaUI.criarBotao("Excluir Selecionado", TemaUI.COR_PERIGO);

        btnLimpar.addActionListener(e -> limparFormulario());
        btnInserir.addActionListener(e -> inserirCliente());
        btnAtualizar.addActionListener(e -> atualizarCliente());
        btnExcluir.addActionListener(e -> excluirCliente());

        botoes.add(btnLimpar);
        botoes.add(btnInserir);
        botoes.add(btnAtualizar);
        botoes.add(btnExcluir);

        cardForm.add(campos, BorderLayout.CENTER);
        cardForm.add(botoes, BorderLayout.SOUTH);
        add(cardForm, BorderLayout.NORTH);

        JPanel cardLista = new JPanel(new BorderLayout(8, 8));
        cardLista.setBackground(TemaUI.COR_PAINEL);
        cardLista.setBorder(TemaUI.criarBordaTitulo("Clientes Cadastrados no Banco de Dados"));

        JPanel barraBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        barraBusca.setOpaque(false);
        JButton btnFiltrar = TemaUI.criarBotao("Pesquisar", TemaUI.COR_PRIMARIA);
        JButton btnMostrarTodos = TemaUI.criarBotao("Mostrar Todos", TemaUI.COR_NEUTRA);
        btnFiltrar.addActionListener(e -> filtrar());
        btnMostrarTodos.addActionListener(e -> {
            txtBusca.setText("");
            recarregarDados();
        });
        barraBusca.add(new JLabel("Filtrar por nome ou e-mail:"));
        barraBusca.add(txtBusca);
        barraBusca.add(btnFiltrar);
        barraBusca.add(btnMostrarTodos);

        modeloTabela = new DefaultTableModel(
                new Object[]{"ID", "Nome", "E-mail", "Data de Nascimento", "Endereço"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TemaUI.estilizarTabela(tabela);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormularioDaSelecao();
            }
        });

        cardLista.add(barraBusca, BorderLayout.NORTH);
        cardLista.add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(cardLista, BorderLayout.CENTER);
    }

    public void recarregarDados() {
        try {
            carregarNaTabela(sistema.listarClientes());
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void filtrar() {
        try {
            carregarNaTabela(sistema.filtrarClientes(txtBusca.getText()));
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void carregarNaTabela(List<Cliente> clientes) {
        this.listaAtual = clientes;
        modeloTabela.setRowCount(0);
        for (Cliente c : clientes) {
            modeloTabela.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getNome(),
                    c.getEmail() != null ? c.getEmail() : "",
                    TemaUI.formatarData(c.getDataNascimento()),
                    c.getEndereco() != null ? c.getEndereco() : ""
            });
        }
    }

    private void preencherFormularioDaSelecao() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0 && linha < listaAtual.size()) {
            Cliente c = listaAtual.get(linha);
            txtId.setText(String.valueOf(c.getIdCliente()));
            txtNome.setText(c.getNome());
            txtEmail.setText(c.getEmail() != null ? c.getEmail() : "");
            txtDataNasc.setText(TemaUI.formatarData(c.getDataNascimento()));
            txtEndereco.setText(c.getEndereco() != null ? c.getEndereco() : "");
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtNome.setText("");
        txtEmail.setText("");
        txtDataNasc.setText("");
        txtEndereco.setText("");
        tabela.clearSelection();
    }

    private void inserirCliente() {
        try {
            Cliente c = new Cliente();
            c.setNome(txtNome.getText().trim());
            c.setEmail(txtEmail.getText().trim());
            c.setDataNascimento(TemaUI.parseDataOpcional(txtDataNasc.getText(), "Data de Nascimento"));
            c.setEndereco(txtEndereco.getText().trim());

            sistema.cadastrarCliente(c);
            limparFormulario();
            recarregarDados();
            TemaUI.exibirSucesso(this, "Cliente cadastrado com sucesso (ID: " + c.getIdCliente() + ")!");
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void atualizarCliente() {
        try {
            int id = TemaUI.parseInteiro(txtId.getText(), "ID do Cliente");
            Cliente c = new Cliente(
                    id,
                    txtNome.getText().trim(),
                    txtEmail.getText().trim(),
                    TemaUI.parseDataOpcional(txtDataNasc.getText(), "Data de Nascimento"),
                    txtEndereco.getText().trim()
            );
            sistema.atualizarCliente(c);
            limparFormulario();
            recarregarDados();
            TemaUI.exibirSucesso(this, "Dados do cliente atualizados com sucesso!");
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void excluirCliente() {
        try {
            int id = TemaUI.parseInteiro(txtId.getText(), "ID do Cliente");
            int conf = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja realmente excluir o cliente selecionado (ID " + id + ")?",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION
            );
            if (conf == JOptionPane.YES_OPTION) {
                sistema.excluirCliente(id);
                limparFormulario();
                recarregarDados();
                TemaUI.exibirSucesso(this, "Cliente excluído com sucesso!");
            }
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }
}

package apresentacao;

import dados.Animal;
import dados.Cliente;
import dados.Funcionario;
import negocio.SistemaPetShop;

import javax.swing.JButton;
import javax.swing.JComboBox;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class PainelAnimais extends JPanel {

    private final SistemaPetShop sistema;
    private final JTextField txtId = new JTextField(5);
    private final JTextField txtNome = new JTextField(16);
    private final JComboBox<String> cbEspecie = new JComboBox<>(
            new String[]{"Cão", "Gato", "Ave", "Roedor", "Peixe", "Réptil", "Outro"});
    private final JTextField txtRaca = new JTextField(14);
    private final JTextField txtDataNasc = new JTextField(10);
    private final JTextField txtDataAdmissao = new JTextField(10);
    private final JTextField txtMotivo = new JTextField(22);
    private final JComboBox<Cliente> cbCliente = new JComboBox<>();
    private final JComboBox<Funcionario> cbFuncionario = new JComboBox<>();
    private final JTextField txtFiltro = new JTextField(20);

    private final DefaultTableModel modeloTabela;
    private final JTable tabela;
    private List<Animal> listaAtual = new ArrayList<>();

    public PainelAnimais(SistemaPetShop sistema) {
        this.sistema = sistema;
        TemaUI.configurarPainel(this);
        setLayout(new BorderLayout(10, 10));

        txtId.setEditable(false);
        txtId.setBackground(new Color(235, 238, 242));
        cbEspecie.setEditable(true);
        txtDataAdmissao.setText(TemaUI.formatarData(LocalDate.now()));

        JPanel cardForm = new JPanel(new BorderLayout(8, 8));
        cardForm.setBackground(TemaUI.COR_PAINEL);
        cardForm.setBorder(TemaUI.criarBordaTitulo("Cadastro de Animais de Estimação (Pets)"));

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; campos.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; campos.add(txtId, gbc);
        gbc.gridx = 2; campos.add(new JLabel("Nome do Pet *:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0; campos.add(txtNome, gbc);
        gbc.gridx = 4; gbc.weightx = 0; campos.add(new JLabel("Espécie *:"), gbc);
        gbc.gridx = 5; campos.add(cbEspecie, gbc);
        gbc.gridx = 6; campos.add(new JLabel("Raça:"), gbc);
        gbc.gridx = 7; gbc.weightx = 0.8; campos.add(txtRaca, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; campos.add(new JLabel("Dono (Cliente) *:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0; campos.add(cbCliente, gbc);
        gbc.gridx = 4; gbc.gridwidth = 1; gbc.weightx = 0; campos.add(new JLabel("Responsável *:"), gbc);
        gbc.gridx = 5; gbc.gridwidth = 3; gbc.weightx = 1.0; campos.add(cbFuncionario, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0;
        campos.add(new JLabel("Data Nasc.:"), gbc);
        gbc.gridx = 1; campos.add(txtDataNasc, gbc);
        gbc.gridx = 2; campos.add(new JLabel("Data Admissão:"), gbc);
        gbc.gridx = 3; campos.add(txtDataAdmissao, gbc);
        gbc.gridx = 4; campos.add(new JLabel("Motivo / Observação:"), gbc);
        gbc.gridx = 5; gbc.gridwidth = 3; gbc.weightx = 1.0; campos.add(txtMotivo, gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        botoes.setOpaque(false);
        JButton btnLimpar = TemaUI.criarBotao("Limpar", TemaUI.COR_NEUTRA);
        JButton btnInserir = TemaUI.criarBotao("Cadastrar Animal", TemaUI.COR_SECUNDARIA);
        JButton btnAtualizar = TemaUI.criarBotao("Salvar Alterações", TemaUI.COR_PRIMARIA);
        JButton btnExcluir = TemaUI.criarBotao("Excluir Selecionado", TemaUI.COR_PERIGO);

        btnLimpar.addActionListener(e -> limparFormulario());
        btnInserir.addActionListener(e -> inserir());
        btnAtualizar.addActionListener(e -> atualizar());
        btnExcluir.addActionListener(e -> excluir());

        botoes.add(btnLimpar);
        botoes.add(btnInserir);
        botoes.add(btnAtualizar);
        botoes.add(btnExcluir);

        cardForm.add(campos, BorderLayout.CENTER);
        cardForm.add(botoes, BorderLayout.SOUTH);
        add(cardForm, BorderLayout.NORTH);

        JPanel cardLista = new JPanel(new BorderLayout(8, 8));
        cardLista.setBackground(TemaUI.COR_PAINEL);
        cardLista.setBorder(TemaUI.criarBordaTitulo("Animais Cadastrados"));

        JPanel barraFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        barraFiltro.setOpaque(false);
        JButton btnBuscar = TemaUI.criarBotao("Pesquisar", TemaUI.COR_PRIMARIA);
        JButton btnTodos = TemaUI.criarBotao("Mostrar Todos", TemaUI.COR_NEUTRA);
        btnBuscar.addActionListener(e -> filtrar());
        btnTodos.addActionListener(e -> {
            txtFiltro.setText("");
            recarregarDados();
        });
        barraFiltro.add(new JLabel("Filtrar por pet, espécie ou dono:"));
        barraFiltro.add(txtFiltro);
        barraFiltro.add(btnBuscar);
        barraFiltro.add(btnTodos);

        modeloTabela = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Espécie", "Raça", "Dono (Cliente)", "Profissional Resp.", "Admissão", "Motivo"}, 0) {
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
                preencherDaSelecao();
            }
        });

        cardLista.add(barraFiltro, BorderLayout.NORTH);
        cardLista.add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(cardLista, BorderLayout.CENTER);
    }

    public void recarregarDados() {
        try {
            cbCliente.removeAllItems();
            for (Cliente c : sistema.listarClientes()) {
                cbCliente.addItem(c);
            }
            cbFuncionario.removeAllItems();
            for (Funcionario f : sistema.listarFuncionarios()) {
                cbFuncionario.addItem(f);
            }
            carregarNaTabela(sistema.listarAnimais());
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void filtrar() {
        try {
            carregarNaTabela(sistema.filtrarAnimais(txtFiltro.getText()));
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void carregarNaTabela(List<Animal> animais) {
        this.listaAtual = animais;
        modeloTabela.setRowCount(0);
        for (Animal a : animais) {
            modeloTabela.addRow(new Object[]{
                    a.getIdAnimal(),
                    a.getNome(),
                    a.getEspecie(),
                    a.getRaca() != null ? a.getRaca() : "",
                    a.getCliente() != null ? a.getCliente().getNome() : "",
                    a.getFuncionario() != null ? a.getFuncionario().getNome() : "",
                    TemaUI.formatarData(a.getDataAdmissao()),
                    a.getMotivo() != null ? a.getMotivo() : ""
            });
        }
    }

    private void preencherDaSelecao() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0 && linha < listaAtual.size()) {
            Animal a = listaAtual.get(linha);
            txtId.setText(String.valueOf(a.getIdAnimal()));
            txtNome.setText(a.getNome());
            cbEspecie.setSelectedItem(a.getEspecie());
            txtRaca.setText(a.getRaca() != null ? a.getRaca() : "");
            txtDataNasc.setText(TemaUI.formatarData(a.getDataNasc()));
            txtDataAdmissao.setText(TemaUI.formatarData(a.getDataAdmissao()));
            txtMotivo.setText(a.getMotivo() != null ? a.getMotivo() : "");
            if (a.getCliente() != null) cbCliente.setSelectedItem(a.getCliente());
            if (a.getFuncionario() != null) cbFuncionario.setSelectedItem(a.getFuncionario());
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtNome.setText("");
        txtRaca.setText("");
        txtDataNasc.setText("");
        txtDataAdmissao.setText(TemaUI.formatarData(LocalDate.now()));
        txtMotivo.setText("");
        tabela.clearSelection();
    }

    private Animal montarAnimalDoFormulario() throws Exception {
        Animal a = new Animal();
        a.setNome(txtNome.getText().trim());
        Object esp = cbEspecie.getSelectedItem();
        a.setEspecie(esp != null ? esp.toString().trim() : "");
        a.setRaca(txtRaca.getText().trim());
        a.setDataNasc(TemaUI.parseDataOpcional(txtDataNasc.getText(), "Data de Nascimento"));
        a.setDataAdmissao(TemaUI.parseDataOpcional(txtDataAdmissao.getText(), "Data de Admissão"));
        a.setMotivo(txtMotivo.getText().trim());
        a.setCliente((Cliente) cbCliente.getSelectedItem());
        a.setFuncionario((Funcionario) cbFuncionario.getSelectedItem());
        return a;
    }

    private void inserir() {
        try {
            Animal a = montarAnimalDoFormulario();
            sistema.cadastrarAnimal(a);
            limparFormulario();
            recarregarDados();
            TemaUI.exibirSucesso(this, "Animal cadastrado com sucesso (ID: " + a.getIdAnimal() + ")!");
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void atualizar() {
        try {
            int id = TemaUI.parseInteiro(txtId.getText(), "ID do Animal");
            Animal a = montarAnimalDoFormulario();
            a.setIdAnimal(id);
            sistema.atualizarAnimal(a);
            limparFormulario();
            recarregarDados();
            TemaUI.exibirSucesso(this, "Dados do animal atualizados com sucesso!");
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void excluir() {
        try {
            int id = TemaUI.parseInteiro(txtId.getText(), "ID do Animal");
            int conf = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja realmente excluir o animal ID " + id + "?",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION
            );
            if (conf == JOptionPane.YES_OPTION) {
                sistema.excluirAnimal(id);
                limparFormulario();
                recarregarDados();
                TemaUI.exibirSucesso(this, "Animal excluído com sucesso!");
            }
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }
}

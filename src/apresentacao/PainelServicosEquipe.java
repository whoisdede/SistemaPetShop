package apresentacao;

import dados.Cliente;
import dados.Funcionario;
import dados.Servico;
import dados.Usuario;
import negocio.SistemaPetShop;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;

public final class PainelServicosEquipe extends JPanel {

    private final SistemaPetShop sistema;

    // Serviços
    private final JComboBox<String> cbTipoServico = new JComboBox<>(new String[]{
            "Banho", "Tosa", "Banho e Tosa", "Tosa Higiênica",
            "Consulta Veterinária", "Vacinação", "Exame Clínico"
    });
    private final JTextField txtValorServico = new JTextField("80.00", 8);
    private final JTextField txtDataServico = new JTextField(10);
    private final JComboBox<Cliente> cbClienteServico = new JComboBox<>();
    private final JComboBox<Usuario> cbUsuarioServico = new JComboBox<>();
    private final DefaultTableModel modeloServicos;

    // Funcionários
    private final JTextField txtNomeFunc = new JTextField(14);
    private final JComboBox<String> cbCargoFunc = new JComboBox<>(new String[]{
            "Vendedor", "Tosador(a)", "Banhista", "Veterinário(a)", "Gerente"
    });
    private final JTextField txtSalarioFunc = new JTextField("2800.00", 8);
    private final DefaultTableModel modeloFuncionarios;

    // Usuários
    private final JTextField txtLogin = new JTextField(10);
    private final JPasswordField txtSenha = new JPasswordField(10);
    private final JComboBox<String> cbNivel = new JComboBox<>(new String[]{"operador", "admin"});
    private final JComboBox<Funcionario> cbFuncUsuario = new JComboBox<>();
    private final DefaultTableModel modeloUsuarios;

    public PainelServicosEquipe(SistemaPetShop sistema) {
        this.sistema = sistema;
        TemaUI.configurarPainel(this);
        setLayout(new BorderLayout(10, 10));
        cbTipoServico.setEditable(true);
        cbCargoFunc.setEditable(true);
        txtDataServico.setText(TemaUI.formatarData(LocalDate.now()));

        // =====================================================
        // ESQUERDA: SERVIÇOS PRESTADOS
        // =====================================================
        JPanel pServ = new JPanel(new BorderLayout(8, 8));
        pServ.setBackground(TemaUI.COR_PAINEL);
        pServ.setBorder(TemaUI.criarBordaTitulo("Registro e Consulta de Serviços (Banho, Tosa e Clínica)"));

        JPanel formServ = new JPanel(new GridBagLayout());
        formServ.setOpaque(false);
        GridBagConstraints gs = new GridBagConstraints();
        gs.insets = new Insets(4, 5, 4, 5);
        gs.fill = GridBagConstraints.HORIZONTAL;

        gs.gridx = 0; gs.gridy = 0; formServ.add(new JLabel("Serviço *:"), gs);
        gs.gridx = 1; gs.weightx = 1.0; formServ.add(cbTipoServico, gs);
        gs.gridx = 2; gs.weightx = 0; formServ.add(new JLabel("Valor (R$) *:"), gs);
        gs.gridx = 3; formServ.add(txtValorServico, gs);

        gs.gridx = 0; gs.gridy = 1; formServ.add(new JLabel("Cliente *:"), gs);
        gs.gridx = 1; gs.gridwidth = 3; gs.weightx = 1.0; formServ.add(cbClienteServico, gs);

        gs.gridx = 0; gs.gridy = 2; gs.gridwidth = 1; gs.weightx = 0;
        formServ.add(new JLabel("Atendente (Usuário) *:"), gs);
        gs.gridx = 1; gs.weightx = 1.0; formServ.add(cbUsuarioServico, gs);
        gs.gridx = 2; gs.weightx = 0; formServ.add(new JLabel("Data:"), gs);
        gs.gridx = 3; formServ.add(txtDataServico, gs);

        JPanel btnServPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        btnServPanel.setOpaque(false);
        JButton btnRegServico = TemaUI.criarBotao("Registrar Serviço", TemaUI.COR_SECUNDARIA);
        btnRegServico.addActionListener(e -> registrarServico());
        btnServPanel.add(btnRegServico);

        JPanel topoServ = new JPanel(new BorderLayout());
        topoServ.setOpaque(false);
        topoServ.add(formServ, BorderLayout.CENTER);
        topoServ.add(btnServPanel, BorderLayout.SOUTH);

        modeloServicos = new DefaultTableModel(
                new Object[]{"ID", "Data", "Serviço", "Cliente", "Atendente", "Valor"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabServicos = new JTable(modeloServicos);
        TemaUI.estilizarTabela(tabServicos);

        pServ.add(topoServ, BorderLayout.NORTH);
        pServ.add(new JScrollPane(tabServicos), BorderLayout.CENTER);

        // =====================================================
        // DIREITA: FUNCIONÁRIOS + USUÁRIOS DO SISTEMA
        // =====================================================
        JPanel pFunc = new JPanel(new BorderLayout(6, 6));
        pFunc.setBackground(TemaUI.COR_PAINEL);
        pFunc.setBorder(TemaUI.criarBordaTitulo("Funcionários Cadastrados"));

        JPanel formFunc = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        formFunc.setOpaque(false);
        JButton btnCadFunc = TemaUI.criarBotao("Cadastrar", TemaUI.COR_PRIMARIA);
        btnCadFunc.addActionListener(e -> cadastrarFuncionario());
        formFunc.add(new JLabel("Nome:"));
        formFunc.add(txtNomeFunc);
        formFunc.add(new JLabel("Cargo:"));
        formFunc.add(cbCargoFunc);
        formFunc.add(new JLabel("Salário:"));
        formFunc.add(txtSalarioFunc);
        formFunc.add(btnCadFunc);

        modeloFuncionarios = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Cargo", "Salário"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabFunc = new JTable(modeloFuncionarios);
        TemaUI.estilizarTabela(tabFunc);
        pFunc.add(formFunc, BorderLayout.NORTH);
        pFunc.add(new JScrollPane(tabFunc), BorderLayout.CENTER);

        JPanel pUsu = new JPanel(new BorderLayout(6, 6));
        pUsu.setBackground(TemaUI.COR_PAINEL);
        pUsu.setBorder(TemaUI.criarBordaTitulo("Usuários"));

        JPanel formUsu = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        formUsu.setOpaque(false);
        JButton btnCadUsu = TemaUI.criarBotao("Criar Usuário", TemaUI.COR_PRIMARIA);
        btnCadUsu.addActionListener(e -> cadastrarUsuario());
        formUsu.add(new JLabel("Login:"));
        formUsu.add(txtLogin);
        formUsu.add(new JLabel("Senha:"));
        formUsu.add(txtSenha);
        formUsu.add(new JLabel("Nível:"));
        formUsu.add(cbNivel);
        formUsu.add(new JLabel("Funcionário:"));
        formUsu.add(cbFuncUsuario);
        formUsu.add(btnCadUsu);

        modeloUsuarios = new DefaultTableModel(
                new Object[]{"ID", "Login", "Nível de Acesso", "Funcionário Vinculado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabUsu = new JTable(modeloUsuarios);
        TemaUI.estilizarTabela(tabUsu);
        pUsu.add(formUsu, BorderLayout.NORTH);
        pUsu.add(new JScrollPane(tabUsu), BorderLayout.CENTER);

        JSplitPane splitEquipe = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pFunc, pUsu);
        splitEquipe.setResizeWeight(0.5);
        splitEquipe.setBorder(null);

        JSplitPane splitGeral = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pServ, splitEquipe);
        splitGeral.setResizeWeight(0.48);
        splitGeral.setBorder(null);
        add(splitGeral, BorderLayout.CENTER);
    }

    public void recarregarDados() {
        try {
            cbClienteServico.removeAllItems();
            for (Cliente c : sistema.listarClientes()) {
                cbClienteServico.addItem(c);
            }

            cbFuncUsuario.removeAllItems();
            modeloFuncionarios.setRowCount(0);
            for (Funcionario f : sistema.listarFuncionarios()) {
                cbFuncUsuario.addItem(f);
                modeloFuncionarios.addRow(new Object[]{
                        f.getIdFunc(),
                        f.getNome(),
                        f.getCargo() != null ? f.getCargo() : "",
                        TemaUI.formatarMoeda(f.getSalario())
                });
            }

            cbUsuarioServico.removeAllItems();
            modeloUsuarios.setRowCount(0);
            for (Usuario u : sistema.listarUsuarios()) {
                cbUsuarioServico.addItem(u);
                modeloUsuarios.addRow(new Object[]{
                        u.getIdUsuario(),
                        u.getLogin(),
                        u.getNivelAcesso(),
                        u.getFuncionario() != null ? u.getFuncionario().getNome() : ""
                });
            }

            modeloServicos.setRowCount(0);
            for (Servico s : sistema.listarServicos()) {
                modeloServicos.addRow(new Object[]{
                        s.getIdServico(),
                        TemaUI.formatarData(s.getDataTransacao()),
                        s.getTipoServico(),
                        s.getCliente() != null ? s.getCliente().getNome() : "",
                        s.getUsuario() != null ? s.getUsuario().toString() : "",
                        TemaUI.formatarMoeda(s.getValor())
                });
            }
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void registrarServico() {
        try {
            Object tipoObj = cbTipoServico.getSelectedItem();
            String tipo = tipoObj != null ? tipoObj.toString().trim() : "";
            LocalDate dt = TemaUI.parseDataOpcional(txtDataServico.getText(), "Data do Serviço");
            Servico s = new Servico(
                    0,
                    TemaUI.parseDecimal(txtValorServico.getText(), "Valor do Serviço"),
                    tipo,
                    dt != null ? dt : LocalDate.now(),
                    (Cliente) cbClienteServico.getSelectedItem(),
                    (Usuario) cbUsuarioServico.getSelectedItem()
            );
            sistema.registrarServico(s);
            recarregarDados();
            TemaUI.exibirSucesso(this, "Serviço registrado com sucesso (ID: " + s.getIdServico() + ")!");
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void cadastrarFuncionario() {
        try {
            Object cargoObj = cbCargoFunc.getSelectedItem();
            Funcionario f = new Funcionario(
                    0,
                    txtNomeFunc.getText().trim(),
                    cargoObj != null ? cargoObj.toString().trim() : "",
                    TemaUI.parseDecimal(txtSalarioFunc.getText(), "Salário")
            );
            sistema.cadastrarFuncionario(f);
            txtNomeFunc.setText("");
            recarregarDados();
            TemaUI.exibirSucesso(this, "Funcionário cadastrado com sucesso!");
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }

    private void cadastrarUsuario() {
        try {
            Usuario u = new Usuario(
                    0,
                    txtLogin.getText().trim(),
                    new String(txtSenha.getPassword()),
                    (String) cbNivel.getSelectedItem(),
                    (Funcionario) cbFuncUsuario.getSelectedItem()
            );
            sistema.cadastrarUsuario(u);
            txtLogin.setText("");
            txtSenha.setText("");
            recarregarDados();
            TemaUI.exibirSucesso(this, "Usuário criado com sucesso!");
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }
}

package apresentacao;

import excecoes.PersistenciaException;
import negocio.SistemaPetShop;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public final class TelaConexao extends JFrame {

    private final SistemaPetShop sistema;
    private final JTextField txtHost = new JTextField("localhost", 18);
    private final JTextField txtPorta = new JTextField("5432", 8);
    private final JTextField txtBanco = new JTextField("petshop", 18);
    private final JTextField txtUsuario = new JTextField("postgres", 18);
    private final JPasswordField txtSenha = new JPasswordField("postgres", 18);
    private final JCheckBox chkInicializar = new JCheckBox("Criar tabelas/triggers e dados iniciais automaticamente se o banco estiver vazio", true);

    public TelaConexao(SistemaPetShop sistema) {
        super("PetShop - Conexão ao Banco de Dados PostgreSQL");
        this.sistema = sistema;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(TemaUI.COR_PRIMARIA);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        JLabel lblTitulo = new JLabel("Sistema de Gestão de PetShop");
        lblTitulo.setFont(TemaUI.FONTE_TITULO);
        lblTitulo.setForeground(Color.WHITE);
        cabecalho.add(lblTitulo, BorderLayout.NORTH);
        add(cabecalho, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(18, 22, 12, 22));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        adicionarLinha(form, gbc, 0, "Host:", txtHost);
        adicionarLinha(form, gbc, 1, "Porta:", txtPorta);
        adicionarLinha(form, gbc, 2, "Banco de Dados:", txtBanco);
        adicionarLinha(form, gbc, 3, "Usuário:", txtUsuario);
        adicionarLinha(form, gbc, 4, "Senha do PostgreSQL:", txtSenha);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        chkInicializar.setOpaque(false);
        form.add(chkInicializar, gbc);

        add(form, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        rodape.setBackground(TemaUI.COR_FUNDO);
        JButton btnSair = TemaUI.criarBotao("Sair", TemaUI.COR_NEUTRA);
        JButton btnConectar = TemaUI.criarBotao("Conectar", TemaUI.COR_PRIMARIA);

        btnSair.addActionListener(e -> System.exit(0));
        btnConectar.addActionListener(e -> realizarConexao());
        getRootPane().setDefaultButton(btnConectar);

        rodape.add(btnSair);
        rodape.add(btnConectar);
        add(rodape, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void adicionarLinha(JPanel form, GridBagConstraints gbc, int linha, String rotulo, java.awt.Component campo) {
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        form.add(new JLabel(rotulo), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        form.add(campo, gbc);
    }

    private void realizarConexao() {
        String host = txtHost.getText();
        String porta = txtPorta.getText();
        String banco = txtBanco.getText();
        String usuario = txtUsuario.getText();
        String senha = new String(txtSenha.getPassword());

        try {
            try {
                sistema.conectar(host, porta, banco, usuario, senha);
            } catch (PersistenciaException ex) {
                if (sistema.isErroBancoInexistente(ex)) {
                    int resp = JOptionPane.showConfirmDialog(
                            this,
                            "O banco de dados '" + banco.trim() + "' ainda não existe no PostgreSQL.\n" +
                            "Deseja criá-lo automaticamente agora?",
                            "Criar Banco de Dados",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (resp == JOptionPane.YES_OPTION) {
                        sistema.criarBancoEConectar();
                    } else {
                        return;
                    }
                } else {
                    throw ex;
                }
            }

            if (!sistema.verificarEsquemaInstalado()) {
                boolean inicializar = chkInicializar.isSelected();
                if (!inicializar) {
                    int resp = JOptionPane.showConfirmDialog(
                            this,
                            "As tabelas do esquema ainda não existem neste banco.\n" +
                            "Deseja executar automaticamente os scripts DDL, Triggers e Dados Iniciais?",
                            "Inicializar Esquema SQL",
                            JOptionPane.YES_NO_OPTION
                    );
                    inicializar = (resp == JOptionPane.YES_OPTION);
                }
                if (inicializar) {
                    sistema.inicializarBancoDeDados(true);
                }
            }

            TelaPrincipal principal = new TelaPrincipal(sistema);
            principal.setVisible(true);
            dispose();
        } catch (Exception ex) {
            TemaUI.exibirErro(this, ex);
        }
    }
}

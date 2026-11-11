package apresentacao;

import negocio.SistemaPetShop;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class TelaPrincipal extends JFrame {

    private final SistemaPetShop sistema;
    private final PainelClientes painelClientes;
    private final PainelAnimais painelAnimais;
    private final PainelProdutosEstoque painelProdutosEstoque;
    private final PainelFornecedoresCompras painelFornecedoresCompras;
    private final PainelVendas painelVendas;
    private final PainelServicosEquipe painelServicosEquipe;
    private final PainelRelatorios painelRelatorios;

    public TelaPrincipal(SistemaPetShop sistema) {
        super("Sistema de Gestão de PetShop");
        this.sistema = sistema;

        this.painelClientes = new PainelClientes(sistema);
        this.painelAnimais = new PainelAnimais(sistema);
        this.painelProdutosEstoque = new PainelProdutosEstoque(sistema);
        this.painelFornecedoresCompras = new PainelFornecedoresCompras(sistema);
        this.painelVendas = new PainelVendas(sistema);
        this.painelServicosEquipe = new PainelServicosEquipe(sistema);
        this.painelRelatorios = new PainelRelatorios(sistema);

        inicializarJanela();
        atualizarAbaAtiva(0);
    }

    private void inicializarJanela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 680));
        setPreferredSize(new Dimension(1180, 720));
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sistema.encerrar();
            }
        });

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(TemaUI.COR_PRIMARIA);
        topo.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JPanel titulos = new JPanel(new BorderLayout());
        titulos.setOpaque(false);
        JLabel lblTitulo = new JLabel("Sistema de Gestão de PetShop");
        lblTitulo.setFont(TemaUI.FONTE_TITULO);
        lblTitulo.setForeground(Color.WHITE);
        JLabel lblConexao = new JLabel(sistema.getResumoConexao());
        lblConexao.setForeground(new Color(220, 235, 255));
        titulos.add(lblTitulo, BorderLayout.NORTH);
        titulos.add(lblConexao, BorderLayout.SOUTH);

        JPanel acoesTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        acoesTopo.setOpaque(false);
        JButton btnRecarregar = TemaUI.criarBotao("Sincronizar Dados", TemaUI.COR_SECUNDARIA);
        JTabbedPane abas = new JTabbedPane();
        btnRecarregar.addActionListener(e -> atualizarAbaAtiva(abas.getSelectedIndex()));
        acoesTopo.add(btnRecarregar);

        topo.add(titulos, BorderLayout.WEST);
        topo.add(acoesTopo, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);

        abas.addTab("1. Clientes", painelClientes);
        abas.addTab("2. Animais (Pets)", painelAnimais);
        abas.addTab("3. Produtos e Estoque", painelProdutosEstoque);
        abas.addTab("4. Fornecedores e Compras", painelFornecedoresCompras);
        abas.addTab("5. Vendas (PDV)", painelVendas);
        abas.addTab("6. Serviços e Equipe", painelServicosEquipe);
        abas.addTab("7. Consultas e Relatórios", painelRelatorios);

        abas.addChangeListener(e -> atualizarAbaAtiva(abas.getSelectedIndex()));
        add(abas, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void atualizarAbaAtiva(int indice) {
        switch (indice) {
            case 0 -> painelClientes.recarregarDados();
            case 1 -> painelAnimais.recarregarDados();
            case 2 -> painelProdutosEstoque.recarregarDados();
            case 3 -> painelFornecedoresCompras.recarregarDados();
            case 4 -> painelVendas.recarregarDados();
            case 5 -> painelServicosEquipe.recarregarDados();
            case 6 -> painelRelatorios.recarregarDados();
        }
    }
}

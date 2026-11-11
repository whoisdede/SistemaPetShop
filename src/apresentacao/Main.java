package apresentacao;

import negocio.SistemaPetShop;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TemaUI.aplicarTemaGlobal();
            SistemaPetShop sistema = new SistemaPetShop();
            TelaConexao telaConexao = new TelaConexao(sistema);
            telaConexao.setVisible(true);
        });
    }
}

package dados;

import java.math.BigDecimal;
import java.util.Objects;

public class Produto {
    private int idProduto;
    private String nomeProduto;
    private BigDecimal preco;
    private BigDecimal custo;
    private Estoque estoque;

    public Produto() {
    }

    public Produto(int idProduto, String nomeProduto, BigDecimal preco, BigDecimal custo, Estoque estoque) {
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.preco = preco;
        this.custo = custo;
        this.estoque = estoque;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public BigDecimal getCusto() {
        return custo;
    }

    public void setCusto(BigDecimal custo) {
        this.custo = custo;
    }

    public Estoque getEstoque() {
        return estoque;
    }

    public void setEstoque(Estoque estoque) {
        this.estoque = estoque;
    }

    @Override
    public String toString() {
        int disp = estoque != null ? estoque.getQuantidadeAtual() : 0;
        return idProduto + " - " + nomeProduto + " (R$ " + preco + " | Disp: " + disp + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produto)) return false;
        Produto produto = (Produto) o;
        return idProduto == produto.idProduto;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProduto);
    }
}

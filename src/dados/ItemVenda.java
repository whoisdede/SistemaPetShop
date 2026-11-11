package dados;

import java.math.BigDecimal;

public class ItemVenda {
    private Produto produto;
    private BigDecimal valorUnit;
    private int qtd;

    public ItemVenda() {
    }

    public ItemVenda(Produto produto, BigDecimal valorUnit, int qtd) {
        this.produto = produto;
        this.valorUnit = valorUnit;
        this.qtd = qtd;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public BigDecimal getValorUnit() {
        return valorUnit;
    }

    public void setValorUnit(BigDecimal valorUnit) {
        this.valorUnit = valorUnit;
    }

    public int getQtd() {
        return qtd;
    }

    public void setQtd(int qtd) {
        this.qtd = qtd;
    }

    public BigDecimal getSubtotal() {
        if (valorUnit == null) return BigDecimal.ZERO;
        return valorUnit.multiply(BigDecimal.valueOf(qtd));
    }
}

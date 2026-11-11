package dados;

import java.math.BigDecimal;
import java.util.Objects;

public class Estoque {
    private int idEstoque;
    private int quantidadeMinima;
    private int quantidadeAtual;
    private BigDecimal subtotal;

    public Estoque() {
        this.subtotal = BigDecimal.ZERO;
    }

    public Estoque(int idEstoque, int quantidadeMinima, int quantidadeAtual, BigDecimal subtotal) {
        this.idEstoque = idEstoque;
        this.quantidadeMinima = quantidadeMinima;
        this.quantidadeAtual = quantidadeAtual;
        this.subtotal = subtotal != null ? subtotal : BigDecimal.ZERO;
    }

    public int getIdEstoque() {
        return idEstoque;
    }

    public void setIdEstoque(int idEstoque) {
        this.idEstoque = idEstoque;
    }

    public int getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public void setQuantidadeMinima(int quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public int getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(int quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public boolean isAbaixoDoMinimo() {
        return quantidadeAtual <= quantidadeMinima;
    }

    @Override
    public String toString() {
        return "Estoque #" + idEstoque + " (Atual: " + quantidadeAtual + " | Mín: " + quantidadeMinima + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Estoque)) return false;
        Estoque estoque = (Estoque) o;
        return idEstoque == estoque.idEstoque;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEstoque);
    }
}

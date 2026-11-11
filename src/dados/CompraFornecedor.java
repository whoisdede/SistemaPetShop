package dados;

import java.time.LocalDate;

public class CompraFornecedor {
    private Fornecedor fornecedor;
    private Produto produto;
    private LocalDate data;
    private int qtd;

    public CompraFornecedor() {
    }

    public CompraFornecedor(Fornecedor fornecedor, Produto produto, LocalDate data, int qtd) {
        this.fornecedor = fornecedor;
        this.produto = produto;
        this.data = data;
        this.qtd = qtd;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public int getQtd() {
        return qtd;
    }

    public void setQtd(int qtd) {
        this.qtd = qtd;
    }
}

package dados;

import java.util.Objects;

public class Fornecedor {
    private int idFornecedor;
    private String nome;
    private String razaoSocial;
    private String cnpj;
    private String endereco;

    public Fornecedor() {
    }

    public Fornecedor(int idFornecedor, String nome, String razaoSocial, String cnpj, String endereco) {
        this.idFornecedor = idFornecedor;
        this.nome = nome;
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.endereco = endereco;
    }

    public int getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    @Override
    public String toString() {
        return idFornecedor + " - " + nome + (cnpj != null && !cnpj.isBlank() ? " (" + cnpj + ")" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fornecedor)) return false;
        Fornecedor that = (Fornecedor) o;
        return idFornecedor == that.idFornecedor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFornecedor);
    }
}

package dados;

import java.math.BigDecimal;
import java.util.Objects;

public class Funcionario {
    private int idFunc;
    private String nome;
    private String cargo;
    private BigDecimal salario;

    public Funcionario() {
    }

    public Funcionario(int idFunc, String nome, String cargo, BigDecimal salario) {
        this.idFunc = idFunc;
        this.nome = nome;
        this.cargo = cargo;
        this.salario = salario;
    }

    public int getIdFunc() {
        return idFunc;
    }

    public void setIdFunc(int idFunc) {
        this.idFunc = idFunc;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    @Override
    public String toString() {
        return idFunc + " - " + nome + (cargo != null && !cargo.isBlank() ? " (" + cargo + ")" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Funcionario)) return false;
        Funcionario that = (Funcionario) o;
        return idFunc == that.idFunc;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFunc);
    }
}

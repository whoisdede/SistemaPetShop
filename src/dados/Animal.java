package dados;

import java.time.LocalDate;
import java.util.Objects;

public class Animal {
    private int idAnimal;
    private String nome;
    private String raca;
    private String especie;
    private LocalDate dataNasc;
    private LocalDate dataAdmissao;
    private String motivo;
    private Cliente cliente;
    private Funcionario funcionario;

    public Animal() {
    }

    public Animal(int idAnimal, String nome, String raca, String especie, LocalDate dataNasc,
                  LocalDate dataAdmissao, String motivo, Cliente cliente, Funcionario funcionario) {
        this.idAnimal = idAnimal;
        this.nome = nome;
        this.raca = raca;
        this.especie = especie;
        this.dataNasc = dataNasc;
        this.dataAdmissao = dataAdmissao;
        this.motivo = motivo;
        this.cliente = cliente;
        this.funcionario = funcionario;
    }

    public int getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(int idAnimal) {
        this.idAnimal = idAnimal;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public LocalDate getDataNasc() {
        return dataNasc;
    }

    public void setDataNasc(LocalDate dataNasc) {
        this.dataNasc = dataNasc;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    @Override
    public String toString() {
        return idAnimal + " - " + nome + " (" + especie + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Animal)) return false;
        Animal animal = (Animal) o;
        return idAnimal == animal.idAnimal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAnimal);
    }
}

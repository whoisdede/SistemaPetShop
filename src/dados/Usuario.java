package dados;

import java.util.Objects;

public class Usuario {
    private int idUsuario;
    private String login;
    private String senha;
    private String nivelAcesso;
    private Funcionario funcionario;

    public Usuario() {
    }

    public Usuario(int idUsuario, String login, String senha, String nivelAcesso, Funcionario funcionario) {
        this.idUsuario = idUsuario;
        this.login = login;
        this.senha = senha;
        this.nivelAcesso = nivelAcesso;
        this.funcionario = funcionario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(String nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    @Override
    public String toString() {
        String nomeFunc = funcionario != null ? " (" + funcionario.getNome() + ")" : "";
        return idUsuario + " - " + login + nomeFunc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return idUsuario == usuario.idUsuario;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario);
    }
}

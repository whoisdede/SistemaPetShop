package dados;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Servico {
    private int idServico;
    private BigDecimal valor;
    private String tipoServico;
    private LocalDate dataTransacao;
    private Cliente cliente;
    private Usuario usuario;

    public Servico() {
    }

    public Servico(int idServico, BigDecimal valor, String tipoServico, LocalDate dataTransacao,
                   Cliente cliente, Usuario usuario) {
        this.idServico = idServico;
        this.valor = valor;
        this.tipoServico = tipoServico;
        this.dataTransacao = dataTransacao;
        this.cliente = cliente;
        this.usuario = usuario;
    }

    public int getIdServico() {
        return idServico;
    }

    public void setIdServico(int idServico) {
        this.idServico = idServico;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(String tipoServico) {
        this.tipoServico = tipoServico;
    }

    public LocalDate getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDate dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}

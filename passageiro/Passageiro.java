package passageiro;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Passageiro {

    private int id;
    private String nome;
    private LocalDate nascimento;
    private String documento;
    private String login;
    private String senha;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataModificacao;

    public Passageiro() {
    }

    public Passageiro(int id, String nome, LocalDate nascimento, String documento, String login, String senha,
            LocalDateTime dataCriacao, LocalDateTime dataModificacao) {
        this.id = id;
        this.nome = nome;
        this.nascimento = nascimento;
        this.documento = documento;
        this.login = login;
        this.senha = senha;
        this.dataCriacao = dataCriacao;
        this.dataModificacao = dataModificacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getNascimento() {
        return nascimento;
    }

    public void setNascimento(LocalDate nascimento) {
        this.nascimento = nascimento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
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

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataModificacao() {
        return dataModificacao;
    }

    public void setDataModificacao(LocalDateTime dataModificacao) {
        this.dataModificacao = dataModificacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Passageiro))
            return false;
        Passageiro that = (Passageiro) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Passageiro{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", nascimento=" + nascimento +
                ", documento='" + documento + '\'' +
                ", login='" + login + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", dataModificacao=" + dataModificacao +
                '}';
    }
}
package aeroporto;

import java.time.LocalDateTime;
import java.util.Objects;

public class Aeroporto {
    private int id;
    private String nome;
    private String abreviacao;
    private String cidade;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataModificacao;

    public Aeroporto() {
    }

    public Aeroporto(int id, String nome, String abreviacao, String cidade, LocalDateTime dataCriacao, LocalDateTime dataModificacao) {
        this.id = id;
        this.nome = nome;
        this.abreviacao = abreviacao;
        this.cidade = cidade;
        this.dataCriacao = dataCriacao;
        this.dataModificacao = dataModificacao;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}

    public String getAbreviacao() {return abreviacao;}
    public void setAbreviacao(String abreviacao) {this.abreviacao = abreviacao;}

    public String getCidade() {return cidade;}
    public void setCidade(String cidade) {this.cidade = cidade;}

    public LocalDateTime getDataCriacao() {return dataCriacao;}
    public void setDataCriacao(LocalDateTime dataCriacao) {this.dataCriacao = dataCriacao;}

    public LocalDateTime getDataModificacao() {return dataModificacao;}
    public void setDataModificacao(LocalDateTime dataModificacao) {this.dataModificacao = dataModificacao;}

    @Override 
    public int hashCode() { return Objects.hash (id);}

    @Override
    public String toString ()
    {
        return "Aeroporto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", abreviacao='" + abreviacao + '\'' +
                ", cidade='" + cidade + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", dataModificacao=" + dataModificacao +
                '}';
    }
}

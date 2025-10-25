package companhia;

import comum.EntidadeNome;
import java.time.LocalDateTime;

public class CompanhiaAerea extends EntidadeNome {

    private String abreviacao;

    public CompanhiaAerea() {
    }

    public CompanhiaAerea(int id, String nome, String abreviacao, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        super(id, nome, dataCriacao, dataAtualizacao);
        this.abreviacao = abreviacao;
    }

    public String getAbreviacao() {
        return abreviacao;
    }

    public void setAbreviacao(String abreviacao) {
        this.abreviacao = abreviacao;
    }

    @Override
    public boolean validar() {
        return super.validar() &&
                abreviacao != null &&
                abreviacao.length() >= 2 &&
                abreviacao.length() <= 5;
    }

    @Override
    public CompanhiaAerea clone() {
        return new CompanhiaAerea(getId(), getNome(), abreviacao, getDataCriacao(), getDataModificacao());

    }
}
package voo;

import companhia.CompanhiaAerea;
import comum.EntidadeBase;
import java.time.LocalDate;
import java.time.LocalTime;

public class Voo extends EntidadeBase {

    public String origem;
    public String destino;
    public LocalDate data;
    public LocalTime horario;
    public LocalTime duracao;
    public String ida;
    public String volta;
    public CompanhiaAerea companhiaAerea;
    public EstadoVoo estado;
    public int capacidade;

    public Voo(int id, String origem, String destino, LocalDate data, LocalTime horario, LocalTime duracao,
            CompanhiaAerea companhiaAerea, EstadoVoo estado, int capacidade, String ida, String volta) {
        super(id, null, null);
        this.origem = origem;
        this.destino = destino;
        this.data = data;
        this.horario = horario;
        this.duracao = duracao;
        this.companhiaAerea = companhiaAerea;
        this.estado = estado;
        this.capacidade = capacidade;
        this.ida = ida;
        this.volta = volta;
        this.estado = estado != null ? estado : EstadoVoo.PROGRAMADO;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem != null ? origem.trim().toUpperCase() : null;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino != null ? destino.trim().toUpperCase() : null;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getDuracao() {
        return duracao;
    }

    public void setDuracao(LocalTime duracao) {
        this.duracao = duracao;
    }

    public CompanhiaAerea getCompanhiaAerea() {
        return companhiaAerea;
    }

    public void setCompanhiaAerea(CompanhiaAerea companhiaAerea) {
        this.companhiaAerea = companhiaAerea;
    }

    public EstadoVoo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVoo estado) {
        this.estado = estado != null ? estado : EstadoVoo.PROGRAMADO;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public String getIda() {
        return ida;
    }

    public void setIda(String ida) {
        this.ida = ida;
    }

    public String getVolta() {
        return volta;
    }

    public void setVolta(String volta) {
        this.volta = volta;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    @Override
    public boolean validar() {
        return origem != null && !origem.isEmpty()
                && destino != null && !destino.isEmpty()
                && data != null
                && duracao != null
                && companhiaAerea != null
                && capacidade > 0
                && estado != null
                && ida != null && !ida.isEmpty()
                && volta != null && !volta.isEmpty()
                && horario != null;
    }

    @Override
    public String toString() {
        return "Voo{id=" + getId()
                + ", origem='" + origem + '\''
                + ", destino='" + destino + '\''
                + ", data=" + data
                + ", duracao=" + duracao
                + ", companhiaAerea=" + companhiaAerea
                + ", estado=" + estado
                + ", capacidade=" + capacidade
                + ", dataCriacao=" + getDataCriacao()
                + ", dataModificacao=" + getDataModificacao()
                + ", ida='" + ida + '\''
                + ", volta='" + volta + '\''
                + ", horario=" + horario
                + '}';
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

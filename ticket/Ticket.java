package ticket;

import comum.EntidadeBase;
import java.time.LocalDateTime;
import passageiro.Passageiro;
import voo.Voo;

public class Ticket extends EntidadeBase {
    public int id;
    public double valor;
    public Voo voo;
    public Passageiro passageiro;
    public String codigo;
    public LocalDateTime dataCriacao;
    public LocalDateTime dataModificacao;

    public Ticket() {
    }

    public Ticket(int id, double valor, Voo voo, Passageiro passageiro, String codigo,
            LocalDateTime dataCriacao, LocalDateTime dataModificacao) {
        this.id = id;
        this.valor = valor;
        this.voo = voo;
        this.passageiro = passageiro;
        this.codigo = codigo;
        this.dataCriacao = dataCriacao;
        this.dataModificacao = dataModificacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Voo getVoo() {
        return voo;
    }

    public void setVoo(Voo voo) {
        this.voo = voo;
    }

    public Passageiro getPassageiro() {
        return passageiro;
    }

    public void setPassageiro(Passageiro passageiro) {
        this.passageiro = passageiro;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", valor=" + valor +
                ", voo=" + (voo != null ? voo.getId() : null) +
                ", passageiro=" + (passageiro != null ? passageiro.getId() : null) +
                ", codigo='" + codigo + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", dataModificacao=" + dataModificacao +
                '}';
    }

    @Override
    public boolean validar() {
        return valor >= 0
                && voo != null
                && passageiro != null
                && codigo != null && !codigo.isEmpty();
    }
    
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
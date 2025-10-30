package ticket;

import comum.EntidadeBase;
import passageiro.Passageiro;
import voo.Voo;

public class Ticket extends EntidadeBase {
    public double valor;
    public Voo voo;
    public Passageiro passageiro;
    public String codigo;
    public String assento;

    public Ticket() {
    }

    public Ticket(int id, double valor, Voo voo, Passageiro passageiro, String codigo, String assento) {
        super(id, null, null);
        this.id = id;
        this.valor = valor;
        this.voo = voo;
        this.passageiro = passageiro;
        this.codigo = codigo;
        this.assento = assento;
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
    public String getAssento() {
        return assento;
    }
    public void setAssento(String assento) {
        this.assento = assento;
    }
    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", valor=" + valor +
                ", voo=" + (voo != null ? voo.getId() : null) +
                ", passageiro=" + (passageiro != null ? passageiro.getId() : null) +
                ", codigo='" + codigo + '\'' +
                ", assento='" + assento + '\'' +
                '}';
    }

    @Override
    public boolean validar() {
        return valor >= 0
                && voo != null
                && passageiro != null
                && codigo != null && !codigo.isEmpty()
                && assento != null && !assento.isEmpty();
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
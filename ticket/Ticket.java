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
    // Novos campos para suportar o padrão Decorator
    public double precoTotal;
    public String descricaoExtras;
    public boolean seguro;
    public int extraBagagens;
    public double promocaoPercent; // 0..100

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

    public double getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(double precoTotal) {
        this.precoTotal = precoTotal;
    }

    public String getDescricaoExtras() {
        return descricaoExtras;
    }

    public void setDescricaoExtras(String descricaoExtras) {
        this.descricaoExtras = descricaoExtras;
    }

    public boolean isSeguro() {
        return seguro;
    }

    public void setSeguro(boolean seguro) {
        this.seguro = seguro;
    }

    public int getExtraBagagens() {
        return extraBagagens;
    }

    public void setExtraBagagens(int extraBagagens) {
        this.extraBagagens = extraBagagens;
    }

    public double getPromocaoPercent() {
        return promocaoPercent;
    }

    public void setPromocaoPercent(double promocaoPercent) {
        this.promocaoPercent = promocaoPercent;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", valor=" + valor +
                ", precoTotal=" + precoTotal +
                ", voo=" + (voo != null ? voo.getId() : null) +
                ", passageiro=" + (passageiro != null ? passageiro.getId() : null) +
                ", codigo='" + codigo + '\'' +
                ", assento='" + assento + '\'' +
                ", descricaoExtras='" + descricaoExtras + '\'' +
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

}
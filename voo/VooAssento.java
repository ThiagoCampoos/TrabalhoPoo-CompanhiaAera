package voo;

import comum.EntidadeBase;
import java.time.LocalDateTime;
import passageiro.Passageiro;

public class VooAssento extends EntidadeBase {
    public Voo voo;
    public String codigoAssento;
    public boolean ocupado;
    public Passageiro passageiro;

    public VooAssento(int id, Voo voo, String codigoAssento, Passageiro passageiro, LocalDateTime dataCriacao,LocalDateTime dataModificacao, Boolean ocupado) {
        super(id, dataCriacao, dataModificacao);
        this.id = id;
        this.voo = voo;
        this.codigoAssento = codigoAssento != null ? codigoAssento.trim().toUpperCase() : null;
        this.ocupado = ocupado;
        this.passageiro = passageiro;
    }

    public Voo getvoo() {
        return voo;
    }

    public void setVoo(Voo Voo) {
        this.voo = voo;
    }

    public String getCodigoAssento() {
        return codigoAssento;
    }

    public void setCodigoAssento(String codigoAssento) {
        this.codigoAssento = codigoAssento != null ? codigoAssento.trim().toUpperCase() : null;
    }

    public Passageiro getPassageiro() {
        return passageiro;
    }

    public void setPassageiro(Passageiro passageiro) {
        this.passageiro = passageiro;
    }

    public boolean getOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    @Override
    public boolean validar() {
        return voo != null && codigoAssento != null && !codigoAssento.isEmpty() && passageiro != null
                && ocupado == (passageiro != null);

    }

    @Override
    public VooAssento clone() {
        return new VooAssento(getId(), voo, codigoAssento, passageiro, getDataCriacao(), getDataModificacao(), ocupado);
    }

    @Override
    public String toString() {
        return "VooAssento [codigoAssento=" + codigoAssento + ", ocupado=" + ocupado + ", passageiro=" + passageiro
                + ", voo=" + voo + "]";
    }
}

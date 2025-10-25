package voo;

import comum.SystemClock;
import passageiro.Passageiro;

public class VooAssentoService {
    private final VooAssentoDao dao;
    private final SystemClock clock;

    public VooAssentoService(VooAssentoDao dao, SystemClock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public VooAssento criarVooAssento(Voo voo, String codigoAssento, Passageiro passageiro, Boolean ocupado) {
        VooAssento assento = new VooAssento(0, voo, codigoAssento, passageiro, null, null, false);
        assento.auditar(clock);
        return dao.create(assento);
    }
    public VooAssento atualizarVooAssento(int id, Voo voo, String codigoAssento, Passageiro passageiro, Boolean ocupado){
        VooAssento assento = new VooAssento(id, voo, codigoAssento, passageiro, null, null, false);
        assento.auditar(clock);
        return dao.update(assento);
    }
    public Boolean delete (int id){
        return dao.deleteById(id);
    }
    public VooAssento findById(int id){
        return dao.findById(id);
    }
    public VooAssento[] findAll(){
        return dao.findAll();
    }
    public VooAssento findByAssentoId(String codigo) {
        return dao.findByCodigoAssento(codigo);
    }
    public VooAssento findByOcupado(boolean ocupado){
        VooAssento[] assentos = dao.findByOcupado(ocupado);
        return assentos.length > 0 ? assentos[0] : null;
    }
}

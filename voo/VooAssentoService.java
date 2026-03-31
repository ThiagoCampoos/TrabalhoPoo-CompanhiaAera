package voo;

import comum.Repositorio;
import comum.SystemClock;
import java.util.List;
import passageiro.Passageiro;

public class VooAssentoService {
    private final Repositorio<VooAssento> dao;
    private final SystemClock clock;

    public VooAssentoService(Repositorio<VooAssento> dao, SystemClock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public VooAssento criar(Voo voo, String codigoAssento, Passageiro passageiro, Boolean ocupado) {
        validarEntrada(voo, codigoAssento);
        assegurarCodigoUnico(codigoAssento);

        VooAssento assento = new VooAssento(0, null, null, null, null, null, false);
        assento.setVoo(voo);
        assento.setCodigoAssento(codigoAssento.trim().toUpperCase());
        assento.setPassageiro(passageiro);
        assento.setOcupado(ocupado != null && ocupado); 
        assento.auditar(clock);
        return dao.create(assento);
    }

    public VooAssento atualizar(int id, Voo voo, String codigoAssento, Passageiro passageiro, Boolean ocupado) {
        VooAssento existente = dao.findById(id);
        if (existente == null) throw new IllegalArgumentException("Assento nao encontrado.");

        if (voo != null) existente.setVoo(voo);
        if (codigoAssento != null && !codigoAssento.trim().isEmpty()) {
            if (!codigoAssento.trim().equalsIgnoreCase(existente.getCodigoAssento())) {
                assegurarCodigoUnico(codigoAssento);
            }
            existente.setCodigoAssento(codigoAssento.trim().toUpperCase());
        }
        if (passageiro != null) existente.setPassageiro(passageiro);
        if (ocupado != null) existente.setOcupado(ocupado);

        existente.auditar(clock);
        return dao.update(existente);
    }

    public boolean remover(int id) {
        return dao.deleteById(id);
    }

    public VooAssento buscarPorId(int id) {
        return dao.findById(id);
    }

    public VooAssento[] listarTodos() {
        List<VooAssento> todos = dao.findAll();
        return todos.toArray(new VooAssento[0]);
    }

    public VooAssento buscarPorCodigo(String codigo) {
        if (codigo == null) return null;
        if (dao instanceof VooAssentoDao memDao) {
            return memDao.findByCodigoAssento(codigo);
        }
        if (dao instanceof VooAssentoDaoJdbc jdbc) {
            return jdbc.findByCodigoAssento(codigo);
        }
        return dao.findAll().stream()
                .filter(a -> a != null && codigo.trim().equalsIgnoreCase(a.getCodigoAssento()))
                .findFirst()
                .orElse(null);
    }

    public VooAssento[] buscarPorVoo(int vooId) {
        if (dao instanceof VooAssentoDao memDao) {
            return memDao.findByVoo(vooId);
        }
        if (dao instanceof VooAssentoDaoJdbc jdbc) {
            return jdbc.findByVoo(vooId).toArray(new VooAssento[0]);
        }
        return dao.findAll().stream()
                .filter(a -> a != null && a.getvoo() != null && a.getvoo().getId() == vooId)
                .toArray(VooAssento[]::new);
    }

    public VooAssento[] buscarPorOcupacao(boolean ocupado) {
        if (dao instanceof VooAssentoDao memDao) {
            return memDao.findByOcupado(ocupado);
        }
        if (dao instanceof VooAssentoDaoJdbc jdbc) {
            return jdbc.findByOcupado(ocupado).toArray(new VooAssento[0]);
        }
        return dao.findAll().stream()
                .filter(a -> a != null && Boolean.TRUE.equals(a.getOcupado()) == ocupado)
                .toArray(VooAssento[]::new);
    }

    private void validarEntrada(Voo voo, String codigoAssento) {
        if (voo == null || voo.getId() <= 0) {
            throw new IllegalArgumentException("Voo invalido.");
        }
        if (codigoAssento == null || codigoAssento.trim().isEmpty()) {
            throw new IllegalArgumentException("Codigo do assento obrigatorio.");
        }
    }

    private void assegurarCodigoUnico(String codigoAssento) {
        VooAssento existente = buscarPorCodigo(codigoAssento);
        if (existente != null) {
            throw new IllegalArgumentException("Codigo de assento ja utilizado.");
        }
    }
}
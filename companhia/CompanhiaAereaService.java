package companhia;

import comum.SystemClock;

public class CompanhiaAereaService {
    public final CompanhiaAereaDao dao;
    private final SystemClock clock;

    public CompanhiaAereaService(CompanhiaAereaDao dao, SystemClock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public CompanhiaAerea criar(String nome, String abreviacao) {
        CompanhiaAerea ca = new CompanhiaAerea(0, nome, abreviacao, null, null);
        ca.auditar(clock);
        return dao.create(ca);
    }

    public CompanhiaAerea atualizar(int id, String nome, String abreviacao) {
        CompanhiaAerea ca = new CompanhiaAerea(id, nome, abreviacao, null, null);
        ca.auditar(clock);
        return dao.update(ca);
    }

    public boolean remover(int id) {
        return dao.deleteById(id);
    }

    public CompanhiaAerea buscarPorId(int id) {
        return dao.findById(id);
    }

    public CompanhiaAerea[] listarTodos() {
        return dao.findAll();
    }

    public CompanhiaAerea buscarPorAbreviacao(String abrev) {
        return dao.findByAbreviacao(abrev);
    }
}

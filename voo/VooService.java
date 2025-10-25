package voo;

import companhia.CompanhiaAerea;
import comum.SystemClock;
import java.time.LocalDate;
import java.time.LocalTime;

public class VooService {
    private final VooDao dao;
    private final SystemClock clock;

    public VooService(VooDao dao, SystemClock clock) {
        this.dao = dao;
        this.clock = clock;
        }

    public Voo criar(int id, String origem, String destino, LocalDate data, LocalTime duracao,
            CompanhiaAerea companhiaAerea, int capacidade, EstadoVoo estado, String ida, String volta) {
        Voo voo = new Voo(id, origem, destino, data, duracao, companhiaAerea, estado, capacidade, ida, volta);
        voo.auditar(clock);
        return dao.create(voo);
    }

    public Voo atualizar(int id, String origem, String destino, LocalDate data, LocalTime duracao,
            CompanhiaAerea companhiaAerea, int capacidade, EstadoVoo estado, String ida, String volta) {
        Voo voo = new Voo(id, origem, destino, data, duracao, companhiaAerea, estado, capacidade, ida, volta);
        voo.auditar(clock);
        return dao.update(voo);
    }

    public boolean remover(int id) {
        return dao.deleteById(id);
    }

    public Voo buscarPorId(int id) {
        return dao.findById(id);
    }

    public Voo[] listarTodos() {
        return dao.findAll();
    }

    public Voo[] buscarPorOrigem(String origem) {
        return dao.findByOrigem(origem);
    }

    public Voo[] buscarPorDestino(String destino) {
        return dao.findByDestino(destino);
    }

    public Voo[] buscarPorData(LocalDate data) {
        return dao.findByData(data);
    }

    public Voo[] buscarPorOrigemDestinoData(String origem, String destino, LocalDate data) {
        return dao.findByOrigemDestinoData(origem, destino, data);
    }

    public Voo[] buscarPorIdaVolta(String ida, String volta) {
        return dao.findByIdaVolta(ida, volta);
    }

    public void alterarEstado(int id, EstadoVoo novoEstado) {
        Voo voo = dao.findById(id);
        if (voo != null) {
            voo.setEstado(novoEstado);
            dao.update(voo);
        }
    }
}
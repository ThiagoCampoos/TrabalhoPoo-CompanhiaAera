package despacho;

import comum.Repositorio;
import comum.SystemClock;
import java.util.List;

public class DespachoService {
    private final Repositorio<Despacho> dao;
    private final SystemClock clock;

    public DespachoService(Repositorio<Despacho> dao, SystemClock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public Despacho criar(int ticketId, String documento, double peso) {
        if (ticketId <= 0) throw new IllegalArgumentException("Ticket invalido.");
        if (documento == null || documento.trim().isEmpty()) throw new IllegalArgumentException("Documento invalido.");
        if (peso < 0) throw new IllegalArgumentException("Peso invalido.");
        Despacho despacho = new Despacho(0, ticketId, documento.trim(), peso);
        despacho.auditar(clock);
        return dao.create(despacho);
    }

    public Despacho buscarPorId(int id) {
        return dao.findById(id);
    }

    public Despacho[] listarTodos() {
        List<Despacho> todos = dao.findAll();
        return todos.toArray(new Despacho[0]);
    }

    public Despacho atualizar(int id, String documento, double peso) {
        Despacho existente = dao.findById(id);
        if (existente == null) throw new IllegalArgumentException("Despacho nao encontrado.");
        if (documento != null && !documento.trim().isEmpty()) existente.setDocumento(documento.trim());
        if (peso >= 0) existente.setPeso(peso);
        existente.auditar(clock);
        return dao.update(existente);
    }

    public boolean remover(int id) {
        return dao.deleteById(id);
    }

    public Despacho buscarPorTicketId(int ticketId) {
        if (dao instanceof DespachoDaoJdbc jdbc) {
            return jdbc.findByTicketId(ticketId);
        }
        return dao.findAll().stream()
                .filter(d -> d != null && d.getTicketId() == ticketId)
                .findFirst()
                .orElse(null);
    }
}
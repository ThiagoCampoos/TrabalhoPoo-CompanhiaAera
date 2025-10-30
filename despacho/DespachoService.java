package despacho;

import comum.SystemClock;

public class DespachoService {
    private final DespachoDao dao;
    private final SystemClock clock;

    public DespachoService(DespachoDao dao, SystemClock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public Despacho criar(int ticketId, String documento, double peso) {
        if (documento == null || documento.trim().isEmpty()) throw new IllegalArgumentException("Documento invalido.");
        if (ticketId <= 0) throw new IllegalArgumentException("Ticket invalido.");
        int id = dao.nextId++;
        Despacho d = new Despacho(id, ticketId, documento.trim(), peso);
        d.auditar(clock);
        return dao.create(d);
    }

    public Despacho buscarPorId(int id) { return dao.findById(id); }
    public Despacho[] listarTodos() { return dao.findAll(); }
    public Despacho atualizar(int id, String documento, double peso) {
        Despacho d = dao.findById(id);
        if (d == null) throw new IllegalArgumentException("Despacho nao encontrado.");
        if (documento != null && !documento.trim().isEmpty()) d.setDocumento(documento.trim());
        d.setPeso(peso);
        d.auditar(clock);
        return dao.update(d);
    }
    public boolean remover(int id) { return dao.deleteById(id); }
}

package reserva;

import comum.SystemClock;


public class ReservaService {
    private final ReservaDao dao;
    private final SystemClock clock;

    public ReservaService(ReservaDao dao, SystemClock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public Reserva criar(String codigo, String sobrenome, int[] ticketIds) {
        if (codigo == null || codigo.trim().isEmpty()) throw new IllegalArgumentException("Codigo da reserva invalido.");
        if (sobrenome == null || sobrenome.trim().isEmpty()) throw new IllegalArgumentException("Sobrenome invalido.");
        int id = dao.nextId++;
        Reserva r = new Reserva(id, codigo.trim(), sobrenome.trim(), ticketIds);
        r.auditar(clock);
        return dao.create(r);
    }

    public Reserva buscarPorCodigoESobrenome(String codigo, String sobrenome) {
        if (codigo == null || sobrenome == null) return null;
        for (Reserva r : dao.findAll()) {
            if (r != null && codigo.equals(r.getCodigo()) && sobrenome.equalsIgnoreCase(r.getSobrenomePassageiro())) {
                return r;
            }
        }
        return null;
    }

    public Reserva buscarPorId(int id) { return dao.findById(id); }
    public Reserva[] listarTodos() { return dao.findAll(); }
    public boolean remover(int id) { return dao.deleteById(id); }

    public Reserva adicionarTicket(int reservaId, int ticketId) {
        Reserva r = dao.findById(reservaId);
        if (r == null) throw new IllegalArgumentException("Reserva nao encontrada.");
        int[] old = r.getTicketIds();
        int[] next = new int[old.length + 1];
        System.arraycopy(old, 0, next, 0, old.length);
        next[old.length] = ticketId;
        r.setTicketIds(next);
        r.auditar(clock);
        return dao.update(r);
    }
}
package ticket;

import comum.SystemClock;
import java.time.LocalDateTime;
import passageiro.Passageiro;
import voo.Voo;

public class TicketService {
    private final TicketDao dao;
    private final SystemClock clock;

    public TicketService(TicketDao dao, SystemClock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public Ticket criar(double valor, Voo voo, Passageiro passageiro) {
        int id = dao.nextId++;
        String codigo = gerarCodigo(id, voo);
        LocalDateTime agora = clock.now();
        Ticket ticket = new Ticket(id, valor, voo, passageiro, codigo, agora, agora);
        ticket.auditar(clock);
        return dao.create(ticket);
    }

    private String gerarCodigo(int id, Voo voo) {
        if (voo != null) {
            return id + "-" + voo.getId();
        }
        return "" + id + "-0";
    }

    public Ticket buscarPorId(int id) {
        return dao.findById(id);
    }

    public Ticket buscarPorCodigo(String codigo) {
        return dao.findByCodigo(codigo);
    }

    public Ticket[] listarTodos() {
        return dao.findAll();
    }

    public boolean remover(int id) {
        return dao.deleteById(id);
    }
}
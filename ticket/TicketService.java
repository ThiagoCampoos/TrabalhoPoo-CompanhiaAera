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

    public Ticket criar(double valor, Voo voo, Passageiro passageiro,  LocalDateTime dataCriacao,
            LocalDateTime dataModificacao, String assento) {
        if (passageiro == null) {
            throw new IllegalArgumentException("Ticket nao possui passageiro associado.");
        }
        if (voo == null) {
            throw new IllegalArgumentException("Ticket nao possui voo associado.");
        }
        if (valor < 0) {
            throw new IllegalArgumentException("Valor do ticket invalido.");
        }
        int id = dao.nextId++;
        String codigo = gerarCodigo(id, voo);
        Ticket ticket = new Ticket(id, valor, voo, passageiro, codigo, assento);
        ticket.auditar(clock);
        return dao.create(ticket);
    }

    public Ticket atribuirAssento(int ticketId, String assento) {
        Ticket t = dao.findById(ticketId);
        if (t == null)
            throw new IllegalArgumentException("Ticket nao encontrado.");
        if (assento == null || assento.trim().isEmpty())
            throw new IllegalArgumentException("Assento invalido.");
        t.setAssento(assento.trim());
        t.auditar(clock);
        return dao.update(t);
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